package br.com.bloqfone.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bloqfone.data.ConfigRepository

class MainViewModel(private val configRepository: ConfigRepository) : ViewModel() {

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

    fun toggleFocusMode(enabled: Boolean) {
        isFocusModeOn = enabled
        configRepository.isFocusModeEnabled = enabled
    }

    fun toggleBlockUnknownNumbers(enabled: Boolean) {
        blockUnknownNumbers = enabled
        configRepository.shouldBlockUnknownNumbers = enabled
    }

    fun toggleBlockPrivateNumbers(enabled: Boolean) {
        blockPrivateNumbers = enabled
        configRepository.shouldBlockPrivateNumbers = enabled
    }

    fun toggleBlockNoCallerId(enabled: Boolean) {
        blockNoCallerId = enabled
        configRepository.shouldBlockNoCallerId = enabled
    }

    fun toggleBlockInternationalNumbers(enabled: Boolean) {
        blockInternationalNumbers = enabled
        configRepository.shouldBlockInternationalNumbers = enabled
    }

    fun toggleBlockTelemarketing(enabled: Boolean) {
        blockTelemarketing = enabled
        configRepository.shouldBlockTelemarketing = enabled
    }

    fun toggleBlockRobocalls(enabled: Boolean) {
        blockRobocalls = enabled
        configRepository.shouldBlockRobocalls = enabled
    }

    fun toggleBlockSpam(enabled: Boolean) {
        blockSpam = enabled
        configRepository.shouldBlockSpam = enabled
    }

    fun toggleSilentBlocking(enabled: Boolean) {
        silentBlocking = enabled
        configRepository.isSilentBlockingEnabled = enabled
    }

    fun toggleSendToVoicemail(enabled: Boolean) {
        sendToVoicemail = enabled
        configRepository.shouldSendToVoicemail = enabled
        if (enabled) {
            autoReject = false
            configRepository.shouldAutoReject = false
        }
    }

    fun toggleAutoReject(enabled: Boolean) {
        autoReject = enabled
        configRepository.shouldAutoReject = enabled
        if (enabled) {
            sendToVoicemail = false
            configRepository.shouldSendToVoicemail = false
        }
    }

    fun addBlacklistNumber(number: String): Boolean {
        val wasAdded = configRepository.addToBlacklist(number)
        if (wasAdded) {
            blacklistNumbers = configRepository.getBlacklistNumbers().toList().sorted()
        }
        return wasAdded
    }

    fun removeBlacklistNumber(number: String) {
        configRepository.removeFromBlacklist(number)
        blacklistNumbers = configRepository.getBlacklistNumbers().toList().sorted()
    }

    fun addWhitelistNumber(number: String): Boolean {
        val wasAdded = configRepository.addToWhitelist(number)
        if (wasAdded) {
            whitelistNumbers = configRepository.getWhitelistNumbers().toList().sorted()
        }
        return wasAdded
    }

    fun removeWhitelistNumber(number: String) {
        configRepository.removeFromWhitelist(number)
        whitelistNumbers = configRepository.getWhitelistNumbers().toList().sorted()
    }

    fun addBlockedCountryCode(code: String): Boolean {
        val wasAdded = configRepository.addBlockedCountryCode(code)
        if (wasAdded) {
            blockedCountryCodes = configRepository.getBlockedCountryCodes().toList().sorted()
        }
        return wasAdded
    }

    fun removeBlockedCountryCode(code: String) {
        configRepository.removeBlockedCountryCode(code)
        blockedCountryCodes = configRepository.getBlockedCountryCodes().toList().sorted()
    }

    fun addBlockedDdd(ddd: String): Boolean {
        val wasAdded = configRepository.addBlockedDdd(ddd)
        if (wasAdded) {
            blockedDdds = configRepository.getBlockedDdds().toList().sorted()
        }
        return wasAdded
    }

    fun removeBlockedDdd(ddd: String) {
        configRepository.removeBlockedDdd(ddd)
        blockedDdds = configRepository.getBlockedDdds().toList().sorted()
    }
}

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(ConfigRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
