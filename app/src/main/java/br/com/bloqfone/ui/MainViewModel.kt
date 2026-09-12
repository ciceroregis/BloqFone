package br.com.bloqfone.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bloqfone.data.BlockedCallRecord
import br.com.bloqfone.data.BlockedCallsRepository
import br.com.bloqfone.data.ConfigRepository
import br.com.bloqfone.data.maskPhoneNumberForLog
import br.com.bloqfone.data.parsePhoneNumber
import br.com.bloqfone.features.AppFeature
import br.com.bloqfone.features.FeatureAccessPolicy
import br.com.bloqfone.features.SubscriptionTier
import br.com.bloqfone.services.BlockingSnapshot
import br.com.bloqfone.services.CallBlockEvaluator

class MainViewModel(
    private val configRepository: ConfigRepository,
    private val blockedCallsRepository: BlockedCallsRepository
) : ViewModel() {

    companion object {
        private const val TAG = "BloqFone:MainViewModel"
    }

    var selectedNavIndex by mutableIntStateOf(0)

    fun selectNavTab(index: Int) {
        selectedNavIndex = index
    }

    var isCallScreeningRoleHeld by mutableStateOf(false)
        private set

    var hasContactsPermission by mutableStateOf(false)
        private set

    fun updateContactsPermission(isGranted: Boolean) {
        hasContactsPermission = isGranted
        Log.i(TAG, "[RASTREAMENTO] Permissão de contatos atualizada na ViewModel: isGranted=$isGranted")
    }

    var isPremiumUser by mutableStateOf(configRepository.isPremiumUser)
        private set

    var isFocusModeOn by mutableStateOf(configRepository.isFocusModeEnabled)
        private set

    var blockUnknownNumbers by mutableStateOf(configRepository.shouldBlockUnknownNumbers)
        private set
    var blockPrivateNumbers by mutableStateOf(configRepository.shouldBlockPrivateNumbers)
        private set
    var blockNoCallerId by mutableStateOf(configRepository.shouldBlockNoCallerId)
        private set
    var blockInternationalNumbers by mutableStateOf(configRepository.shouldBlockInternationalNumbers)
        private set
    var blockTelemarketing by mutableStateOf(configRepository.shouldBlockTelemarketing)
        private set
    var blockRobocalls by mutableStateOf(configRepository.shouldBlockRobocalls)
        private set
    var blockSpam by mutableStateOf(configRepository.shouldBlockSpam)
        private set

    var silentBlocking by mutableStateOf(configRepository.isSilentBlockingEnabled)
        private set
    var sendToVoicemail by mutableStateOf(configRepository.shouldSendToVoicemail)
        private set
    var autoReject by mutableStateOf(configRepository.shouldAutoReject)
        private set

    var blacklistNumbers by mutableStateOf(configRepository.getBlacklistNumbers().toList().sorted())
        private set
    var whitelistNumbers by mutableStateOf(configRepository.getWhitelistNumbers().toList().sorted())
        private set
    var blockedCountryCodes by mutableStateOf(configRepository.getBlockedCountryCodes().toList().sorted())
        private set
    var blockedDdds by mutableStateOf(configRepository.getBlockedDdds().toList().sorted())
        private set

    var currentPopupMessage by mutableStateOf<PopupMessage?>(null)
        private set

    fun showFeedback(message: String, type: FeedbackType = FeedbackType.SUCCESS) {
        currentPopupMessage = PopupMessage(id = System.currentTimeMillis(), message = message, type = type)
    }

    fun dismissFeedback() {
        currentPopupMessage = null
    }

    var blacklistSearchQuery by mutableStateOf("")
    var whitelistSearchQuery by mutableStateOf("")

    val filteredBlacklist: List<String>
        get() {
            val query = blacklistSearchQuery.trim()
            if (query.isEmpty()) return blacklistNumbers
            val digits = query.filter { it.isDigit() }
            return blacklistNumbers.filter { item ->
                item.contains(query, ignoreCase = true) ||
                    (digits.isNotEmpty() && item.filter { it.isDigit() }.contains(digits))
            }
        }

    val filteredWhitelist: List<String>
        get() {
            val query = whitelistSearchQuery.trim()
            if (query.isEmpty()) return whitelistNumbers
            val digits = query.filter { it.isDigit() }
            return whitelistNumbers.filter { item ->
                item.contains(query, ignoreCase = true) ||
                    (digits.isNotEmpty() && item.filter { it.isDigit() }.contains(digits))
            }
        }

    var blockedCalls by mutableStateOf(blockedCallsRepository.getBlockedCalls())
        private set

    var reportSearchQuery by mutableStateOf("")

    val filteredBlockedCalls: List<BlockedCallRecord>
        get() {
            val query = reportSearchQuery.trim()
            if (query.isEmpty()) return blockedCalls
            val digits = query.filter { it.isDigit() }
            return blockedCalls.filter { record ->
                record.formattedDisplayNumber.contains(query, ignoreCase = true) ||
                    record.reason.contains(query, ignoreCase = true) ||
                    (digits.isNotEmpty() && record.rawNumber?.filter { it.isDigit() }?.contains(digits) == true)
            }
        }

    fun refreshBlockedCalls() {
        blockedCalls = blockedCallsRepository.getBlockedCalls()
    }

    fun clearBlockedCalls() {
        val success = blockedCallsRepository.clearAll()
        if (success) {
            blockedCalls = emptyList()
            showFeedback("Relatório de chamadas limpo com sucesso.", FeedbackType.INFO)
            Log.i(TAG, "[SUCESSO] Relatório de chamadas esvaziado pelo usuário.")
        } else {
            showFeedback("Falha ao limpar relatório.", FeedbackType.ERROR)
            Log.e(TAG, "[ERRO] Falha ao esvaziar relatório de chamadas.")
        }
    }

    fun deleteBlockedCall(id: String) {
        val success = blockedCallsRepository.deleteRecord(id)
        if (success) {
            blockedCalls = blockedCallsRepository.getBlockedCalls()
            showFeedback("Chamada removida do relatório.", FeedbackType.INFO)
            Log.i(TAG, "[SUCESSO] Chamada $id removida do relatório.")
        } else {
            showFeedback("Falha ao remover chamada do relatório.", FeedbackType.ERROR)
            Log.e(TAG, "[ERRO] Falha ao remover chamada $id do relatório.")
        }
    }

    fun updateRoleStatus(isHeld: Boolean) {
        Log.i(TAG, "[RASTREAMENTO] Role de triagem atualizada na ViewModel: isHeld=$isHeld")
        isCallScreeningRoleHeld = isHeld
    }

    fun canUseFeature(feature: AppFeature): Boolean {
        // Modo 100% gratuito: todas as funcionalidades estão liberadas
        return true
    }

    fun activatePremium(enabled: Boolean) {
        isPremiumUser = enabled
    }

    fun toggleFocusMode(enabled: Boolean, onRequestPermission: () -> Unit = {}) {
        if (enabled && !canUseFeature(AppFeature.MODE_FOCUS)) return
        if (enabled && !hasContactsPermission) {
            onRequestPermission()
            showFeedback("Permissão de contatos necessária para ativar o Modo Foco.", FeedbackType.WARNING)
            return
        }
        isFocusModeOn = enabled
        configRepository.isFocusModeEnabled = enabled
        Log.i(TAG, "[RASTREAMENTO] Regra 'modo foco' alterada para: $enabled")
    }

    fun toggleBlockUnknownNumbers(enabled: Boolean) {
        blockUnknownNumbers = enabled
        configRepository.shouldBlockUnknownNumbers = enabled
        Log.i(TAG, "[RASTREAMENTO] Regra 'bloquear números desconhecidos' alterada para: $enabled")
    }

    fun toggleBlockPrivateNumbers(enabled: Boolean) {
        blockPrivateNumbers = enabled
        configRepository.shouldBlockPrivateNumbers = enabled
        Log.i(TAG, "[RASTREAMENTO] Regra 'bloquear números privados' alterada para: $enabled")
    }

    fun toggleBlockNoCallerId(enabled: Boolean) {
        blockNoCallerId = enabled
        configRepository.shouldBlockNoCallerId = enabled
        Log.i(TAG, "[RASTREAMENTO] Regra 'bloquear sem identificação' alterada para: $enabled")
    }

    fun toggleBlockInternationalNumbers(enabled: Boolean) {
        if (enabled && !canUseFeature(AppFeature.BLOCK_INTERNATIONAL)) return
        blockInternationalNumbers = enabled
        configRepository.shouldBlockInternationalNumbers = enabled
        Log.i(TAG, "[RASTREAMENTO] Regra 'bloquear internacionais' alterada para: $enabled")
    }

    fun toggleBlockTelemarketing(enabled: Boolean) {
        if (enabled && !canUseFeature(AppFeature.BLOCK_TELEMARKETING)) return
        blockTelemarketing = enabled
        configRepository.shouldBlockTelemarketing = enabled
        Log.i(TAG, "[RASTREAMENTO] Regra 'bloquear telemarketing' alterada para: $enabled")
    }

    fun toggleBlockRobocalls(enabled: Boolean) {
        if (enabled && !canUseFeature(AppFeature.BLOCK_ROBOCALLS)) return
        blockRobocalls = enabled
        configRepository.shouldBlockRobocalls = enabled
        Log.i(TAG, "[RASTREAMENTO] Regra 'bloquear robocalls' alterada para: $enabled")
    }

    fun toggleBlockSpam(enabled: Boolean) {
        if (enabled && !canUseFeature(AppFeature.BLOCK_SPAM)) return
        blockSpam = enabled
        configRepository.shouldBlockSpam = enabled
        Log.i(TAG, "[RASTREAMENTO] Regra 'bloquear spam' alterada para: $enabled")
    }

    fun toggleSilentBlocking(enabled: Boolean) {
        if (enabled && !canUseFeature(AppFeature.SILENT_BLOCKING)) return
        silentBlocking = enabled
        configRepository.isSilentBlockingEnabled = enabled
        Log.i(TAG, "[RASTREAMENTO] Regra 'bloqueio silencioso' alterada para: $enabled")
    }

    fun toggleSendToVoicemail(enabled: Boolean) {
        if (enabled && !canUseFeature(AppFeature.SEND_TO_VOICEMAIL)) return
        sendToVoicemail = enabled
        configRepository.shouldSendToVoicemail = enabled
        Log.i(TAG, "[RASTREAMENTO] Regra 'enviar para correio de voz' alterada para: $enabled")
        if (enabled) {
            autoReject = false
            configRepository.shouldAutoReject = false
        }
    }

    fun toggleAutoReject(enabled: Boolean) {
        autoReject = enabled
        configRepository.shouldAutoReject = enabled
        Log.i(TAG, "[RASTREAMENTO] Regra 'recusar automaticamente' alterada para: $enabled")
        if (enabled) {
            sendToVoicemail = false
            configRepository.shouldSendToVoicemail = false
        }
    }

    fun addBlacklistNumber(number: String): Boolean {
        val wasAdded = configRepository.addToBlacklist(number)
        if (wasAdded) {
            blacklistNumbers = configRepository.getBlacklistNumbers().toList().sorted()
            Log.i(TAG, "[SUCESSO] Número inserido na lista negra via ViewModel: ${maskPhoneNumberForLog(number)}")
        } else {
            Log.w(TAG, "[ERRO] Falha ao adicionar número na lista negra via ViewModel: ${maskPhoneNumberForLog(number)}")
        }
        return wasAdded
    }

    fun removeBlacklistNumber(number: String) {
        configRepository.removeFromBlacklist(number)
        blacklistNumbers = configRepository.getBlacklistNumbers().toList().sorted()
        Log.i(TAG, "[SUCESSO] Número removido da lista negra via ViewModel: ${maskPhoneNumberForLog(number)}")
    }

    fun addWhitelistNumber(number: String): Boolean {
        val wasAdded = configRepository.addToWhitelist(number)
        if (wasAdded) {
            whitelistNumbers = configRepository.getWhitelistNumbers().toList().sorted()
            Log.i(TAG, "[SUCESSO] Número inserido na lista branca via ViewModel: ${maskPhoneNumberForLog(number)}")
        } else {
            Log.w(TAG, "[ERRO] Falha ao adicionar número na lista branca via ViewModel: ${maskPhoneNumberForLog(number)}")
        }
        return wasAdded
    }

    fun removeWhitelistNumber(number: String) {
        configRepository.removeFromWhitelist(number)
        whitelistNumbers = configRepository.getWhitelistNumbers().toList().sorted()
        Log.i(TAG, "[SUCESSO] Número removido da lista branca via ViewModel: ${maskPhoneNumberForLog(number)}")
    }

    fun addBlockedCountryCode(code: String): Boolean {
        if (!canUseFeature(AppFeature.BLOCK_COUNTRY)) return false
        val wasAdded = configRepository.addBlockedCountryCode(code)
        if (wasAdded) {
            blockedCountryCodes = configRepository.getBlockedCountryCodes().toList().sorted()
            Log.i(TAG, "[SUCESSO] Código DDI (+${code}) adicionado via ViewModel.")
        } else {
            Log.w(TAG, "[ERRO] Falha ao adicionar código DDI (+${code}) via ViewModel.")
        }
        return wasAdded
    }

    fun removeBlockedCountryCode(code: String) {
        if (!canUseFeature(AppFeature.BLOCK_COUNTRY)) return
        configRepository.removeBlockedCountryCode(code)
        blockedCountryCodes = configRepository.getBlockedCountryCodes().toList().sorted()
        Log.i(TAG, "[SUCESSO] Código DDI (+${code}) removido via ViewModel.")
    }

    fun addBlockedDdd(ddd: String): Boolean {
        if (!canUseFeature(AppFeature.BLOCK_DDD)) return false
        val wasAdded = configRepository.addBlockedDdd(ddd)
        if (wasAdded) {
            blockedDdds = configRepository.getBlockedDdds().toList().sorted()
            Log.i(TAG, "[SUCESSO] DDD ($ddd) adicionado via ViewModel.")
        } else {
            Log.w(TAG, "[ERRO] Falha ao adicionar DDD ($ddd) via ViewModel.")
        }
        return wasAdded
    }

    fun removeBlockedDdd(ddd: String) {
        if (!canUseFeature(AppFeature.BLOCK_DDD)) return
        configRepository.removeBlockedDdd(ddd)
        blockedDdds = configRepository.getBlockedDdds().toList().sorted()
        Log.i(TAG, "[SUCESSO] DDD ($ddd) removido via ViewModel.")
    }

    fun blocklistCount(): Int = blacklistNumbers.size
    fun whitelistCount(): Int = whitelistNumbers.size

    fun moveToWhitelist(number: String) {
        Log.i(TAG, "[SUCESSO] Movendo ${maskPhoneNumberForLog(number)} da lista negra para a lista branca.")
        removeBlacklistNumber(number)
        addWhitelistNumber(number)
    }

    fun moveToBlacklist(number: String) {
        Log.i(TAG, "[SUCESSO] Movendo ${maskPhoneNumberForLog(number)} da lista branca para a lista negra.")
        removeWhitelistNumber(number)
        addBlacklistNumber(number)
    }

    fun activeRulesCount(): Int {
        var count = 0
        if (isFocusModeOn) count++
        if (blockUnknownNumbers) count++
        if (blockPrivateNumbers) count++
        if (blockNoCallerId) count++
        if (blockTelemarketing) count++
        if (blockRobocalls) count++
        if (blockSpam) count++
        if (blockInternationalNumbers) count++
        if (blockedCountryCodes.isNotEmpty()) count++
        if (blockedDdds.isNotEmpty()) count++
        if (autoReject) count++
        return count
    }

    fun refreshSettings() {
        isPremiumUser = configRepository.isPremiumUser
        isFocusModeOn = configRepository.isFocusModeEnabled
        blockUnknownNumbers = configRepository.shouldBlockUnknownNumbers
        blockPrivateNumbers = configRepository.shouldBlockPrivateNumbers
        blockNoCallerId = configRepository.shouldBlockNoCallerId
        blockInternationalNumbers = configRepository.shouldBlockInternationalNumbers
        blockTelemarketing = configRepository.shouldBlockTelemarketing
        blockRobocalls = configRepository.shouldBlockRobocalls
        blockSpam = configRepository.shouldBlockSpam
        silentBlocking = configRepository.isSilentBlockingEnabled
        sendToVoicemail = configRepository.shouldSendToVoicemail
        autoReject = configRepository.shouldAutoReject
        blacklistNumbers = configRepository.getBlacklistNumbers().toList().sorted()
        whitelistNumbers = configRepository.getWhitelistNumbers().toList().sorted()
        blockedCountryCodes = configRepository.getBlockedCountryCodes().toList().sorted()
        blockedDdds = configRepository.getBlockedDdds().toList().sorted()
        blockedCalls = blockedCallsRepository.getBlockedCalls()
    }
}

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                configRepository = ConfigRepository(context),
                blockedCallsRepository = BlockedCallsRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class FeedbackType {
    SUCCESS,
    WARNING,
    ERROR,
    INFO
}

data class PopupMessage(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val type: FeedbackType = FeedbackType.SUCCESS
)
