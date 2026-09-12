package br.com.bloqfone.services

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.core.content.ContextCompat
import br.com.bloqfone.data.BlockedCallsRepository
import br.com.bloqfone.data.ConfigRepository
import br.com.bloqfone.data.ContactsRepository
import br.com.bloqfone.data.maskPhoneNumberForLog
import br.com.bloqfone.data.parsePhoneNumber

class CallInterceptorService : CallScreeningService() {

    private lateinit var contactsRepository: ContactsRepository
    private lateinit var configRepository: ConfigRepository
    private lateinit var blockedCallsRepository: BlockedCallsRepository

    companion object {
        private const val TAG = "BloqFone:CallInterceptor"
    }

    override fun onCreate() {
        super.onCreate()
        try {
            // Initialize the repositories and notification channel when the service is created
            contactsRepository = ContactsRepository(this)
            configRepository = ConfigRepository(this)
            blockedCallsRepository = BlockedCallsRepository(this)
            NotificationHelper.createNotificationChannel(this)
            Log.i(TAG, "[SUCESSO] CallInterceptorService criado e repositórios inicializados com sucesso.")
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Falha ao inicializar dependências do CallInterceptorService.", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "[RASTREAMENTO] CallInterceptorService destruído (onDestroy).")
    }

    override fun onScreenCall(callDetails: Call.Details) {
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) {
            Log.d(TAG, "[RASTREAMENTO] Chamada ignorada: direção não é entrante (direction=${callDetails.callDirection}).")
            return
        }

        val rawIncomingNumber = callDetails.handle?.schemeSpecificPart
        val maskedNumber = maskPhoneNumberForLog(rawIncomingNumber)
        Log.i(TAG, "[RASTREAMENTO] Nova chamada recebida para triagem: número=$maskedNumber, presentation=${callDetails.handlePresentation}")

        try {
            val parsedNumber = parsePhoneNumber(rawIncomingNumber)
            val hasContactsPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            val isInContacts = if (hasContactsPermission) {
                contactsRepository.doesNumberExistInContacts(parsedNumber.normalized)
            } else {
                false
            }
            Log.d(TAG, "[RASTREAMENTO] Verificação de contato para $maskedNumber: estáNosContatos=$isInContacts (permissãoContatos=$hasContactsPermission)")

            val callerDisplayName = callDetails.callerDisplayName ?: callDetails.contactDisplayName
            val callerVerificationStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                callDetails.callerNumberVerificationStatus
            } else {
                0
            }

            val blockReason = CallBlockEvaluator.evaluateBlockReason(
                rawIncomingNumber = rawIncomingNumber,
                handlePresentation = callDetails.handlePresentation,
                isInContacts = isInContacts,
                snapshot = currentSnapshot(hasContactsPermission),
                callerDisplayName = callerDisplayName,
                callerVerificationStatus = callerVerificationStatus
            )

            val responseBuilder = CallResponse.Builder()
            if (blockReason != null) {
                applyBlockingAction(responseBuilder)
                blockedCallsRepository.recordBlockedCall(
                    rawNumber = rawIncomingNumber,
                    reason = blockReason,
                    autoReject = configRepository.shouldAutoReject
                )
                NotificationHelper.notifyBlockedCall(
                    context = this,
                    rawNumber = rawIncomingNumber,
                    reason = blockReason
                )
                Log.i(
                    TAG,
                    "[SUCESSO] Chamada BLOQUEADA para $maskedNumber. Motivo: $blockReason. Ação aplicada: autoReject=${configRepository.shouldAutoReject}, silenciada=true, skipNotification=true."
                )
            } else {
                responseBuilder
                    .setDisallowCall(false)
                    .setRejectCall(false)
                    .setSilenceCall(false)
                    .setSkipNotification(false)
                Log.i(TAG, "[SUCESSO] Chamada PERMITIDA para $maskedNumber. Nenhuma regra de bloqueio violada.")
            }

            respondToCall(callDetails, responseBuilder.build())
            Log.d(TAG, "[SUCESSO] Resposta de triagem despachada ao Telecom com sucesso para $maskedNumber.")
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Falha ao processar triagem de chamada para $maskedNumber: ${e.message}", e)
            try {
                // Fallback seguro: permite a chamada para que o usuário não perca ligações por falhas inesperadas
                val fallbackResponse = CallResponse.Builder()
                    .setDisallowCall(false)
                    .setRejectCall(false)
                    .setSilenceCall(false)
                    .setSkipNotification(false)
                    .build()
                respondToCall(callDetails, fallbackResponse)
                Log.i(TAG, "[SUCESSO] Resposta de fallback (permitir chamada) despachada após erro.")
            } catch (fallbackEx: Exception) {
                Log.e(TAG, "[ERRO] Falha catastrófica ao despachar resposta de fallback ao Telecom.", fallbackEx)
            }
        }
    }

    private fun applyBlockingAction(builder: CallResponse.Builder) {
        val autoReject = configRepository.shouldAutoReject

        builder
            .setDisallowCall(true)
            .setRejectCall(autoReject)
            .setSilenceCall(true)
            .setSkipCallLog(false)
            .setSkipNotification(true)
    }

    private fun currentSnapshot(hasContactsPermission: Boolean): BlockingSnapshot {
        return BlockingSnapshot(
            isFocusModeEnabled = configRepository.isFocusModeEnabled,
            hasContactsPermission = hasContactsPermission,
            shouldBlockUnknownNumbers = configRepository.shouldBlockUnknownNumbers,
            shouldBlockPrivateNumbers = configRepository.shouldBlockPrivateNumbers,
            shouldBlockNoCallerId = configRepository.shouldBlockNoCallerId,
            shouldBlockInternationalNumbers = configRepository.shouldBlockInternationalNumbers,
            shouldBlockTelemarketing = configRepository.shouldBlockTelemarketing,
            shouldBlockRobocalls = configRepository.shouldBlockRobocalls,
            shouldBlockSpam = configRepository.shouldBlockSpam,
            whitelistNumbers = configRepository.getWhitelistNumbers(),
            blacklistNumbers = configRepository.getBlacklistNumbers(),
            blockedCountryCodes = configRepository.getBlockedCountryCodes(),
            blockedDdds = configRepository.getBlockedDdds()
        )
    }
}