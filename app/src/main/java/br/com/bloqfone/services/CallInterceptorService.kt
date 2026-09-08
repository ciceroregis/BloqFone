package br.com.bloqfone.services

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import br.com.bloqfone.data.ConfigRepository
import br.com.bloqfone.data.ContactsRepository
import br.com.bloqfone.data.parsePhoneNumber

class CallInterceptorService : CallScreeningService() {

    private lateinit var contactsRepository: ContactsRepository
    private lateinit var configRepository: ConfigRepository

    override fun onCreate() {
        super.onCreate()
        // Initialize the repositories when the service is created
        contactsRepository = ContactsRepository(this)
        configRepository = ConfigRepository(this)
    }

    override fun onScreenCall(callDetails: Call.Details) {
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) return

        val rawIncomingNumber = callDetails.handle?.schemeSpecificPart
        val parsedNumber = parsePhoneNumber(rawIncomingNumber)
        debugLog("Analisando chamada de: ${maskNumberForLog(parsedNumber.normalized)}")

        val responseBuilder = CallResponse.Builder()
        val isInContacts = contactsRepository.doesNumberExistInContacts(parsedNumber.normalized)
        val blockReason = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = rawIncomingNumber,
            handlePresentation = callDetails.handlePresentation,
            isInContacts = isInContacts,
            snapshot = currentSnapshot()
        )

        if (blockReason != null) {
            debugLog("Bloqueado por regra: $blockReason")
            applyBlockingAction(responseBuilder)
        } else {
            responseBuilder
                .setDisallowCall(false)
                .setRejectCall(false)
                .setSilenceCall(false)
                .setSkipNotification(false)
        }

        respondToCall(callDetails, responseBuilder.build())
    }

    private fun applyBlockingAction(builder: CallResponse.Builder) {
        val sendToVoicemail = configRepository.shouldSendToVoicemail
        val autoReject = configRepository.shouldAutoReject
        val silentBlocking = configRepository.isSilentBlockingEnabled

        val rejectCall = when {
            sendToVoicemail -> false
            autoReject -> true
            else -> false
        }

        builder
            .setDisallowCall(true)
            .setRejectCall(rejectCall)
            .setSilenceCall(silentBlocking || sendToVoicemail)
            .setSkipCallLog(false)
            .setSkipNotification(silentBlocking || sendToVoicemail)
    }

    private fun currentSnapshot(): BlockingSnapshot {
        return BlockingSnapshot(
            isFocusModeEnabled = configRepository.isFocusModeEnabled,
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

    private fun maskNumberForLog(number: String): String {
        val digits = number.filter { it.isDigit() }
        if (digits.length <= 4) return "****"
        return "****${digits.takeLast(4)}"
    }

    private fun debugLog(message: String) {
        Log.d("CallInterceptor", message)
    }
}