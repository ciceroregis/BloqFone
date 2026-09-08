package br.com.bloqfone.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bloqfone.data.ConfigRepository
import br.com.bloqfone.features.AppFeature
import br.com.bloqfone.features.FeatureAccessPolicy
import br.com.bloqfone.features.SubscriptionTier

class MainViewModel(private val configRepository: ConfigRepository) : ViewModel() {

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

    fun canUseFeature(feature: AppFeature): Boolean {
        return FeatureAccessPolicy.isUnlocked(feature, if (isPremiumUser) SubscriptionTier.PREMIUM else SubscriptionTier.FREE)
    }

    fun activatePremium(enabled: Boolean) {
        isPremiumUser = enabled
        configRepository.isPremiumUser = enabled
    }

    fun toggleFocusMode(enabled: Boolean) {
        if (enabled && !canUseFeature(AppFeature.MODE_FOCUS)) return
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
        if (enabled && !canUseFeature(AppFeature.BLOCK_INTERNATIONAL)) return
        blockInternationalNumbers = enabled
        configRepository.shouldBlockInternationalNumbers = enabled
    }

    fun toggleBlockTelemarketing(enabled: Boolean) {
        if (enabled && !canUseFeature(AppFeature.BLOCK_TELEMARKETING)) return
        blockTelemarketing = enabled
        configRepository.shouldBlockTelemarketing = enabled
    }

    fun toggleBlockRobocalls(enabled: Boolean) {
        if (enabled && !canUseFeature(AppFeature.BLOCK_ROBOCALLS)) return
        blockRobocalls = enabled
        configRepository.shouldBlockRobocalls = enabled
    }

    fun toggleBlockSpam(enabled: Boolean) {
        if (enabled && !canUseFeature(AppFeature.BLOCK_SPAM)) return
        blockSpam = enabled
        configRepository.shouldBlockSpam = enabled
    }

    fun toggleSilentBlocking(enabled: Boolean) {
        if (enabled && !canUseFeature(AppFeature.SILENT_BLOCKING)) return
        silentBlocking = enabled
        configRepository.isSilentBlockingEnabled = enabled
    }

    fun toggleSendToVoicemail(enabled: Boolean) {
        if (enabled && !canUseFeature(AppFeature.SEND_TO_VOICEMAIL)) return
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
        if (!canUseFeature(AppFeature.BLOCK_COUNTRY)) return false
        val wasAdded = configRepository.addBlockedCountryCode(code)
        if (wasAdded) {
            blockedCountryCodes = configRepository.getBlockedCountryCodes().toList().sorted()
        }
        return wasAdded
    }

    fun removeBlockedCountryCode(code: String) {
        if (!canUseFeature(AppFeature.BLOCK_COUNTRY)) return
        configRepository.removeBlockedCountryCode(code)
        blockedCountryCodes = configRepository.getBlockedCountryCodes().toList().sorted()
    }

    fun addBlockedDdd(ddd: String): Boolean {
        if (!canUseFeature(AppFeature.BLOCK_DDD)) return false
        val wasAdded = configRepository.addBlockedDdd(ddd)
        if (wasAdded) {
            blockedDdds = configRepository.getBlockedDdds().toList().sorted()
        }
        return wasAdded
    }

    fun removeBlockedDdd(ddd: String) {
        if (!canUseFeature(AppFeature.BLOCK_DDD)) return
        configRepository.removeBlockedDdd(ddd)
        blockedDdds = configRepository.getBlockedDdds().toList().sorted()
    }

    fun blocklistCount(): Int = blacklistNumbers.size
    fun whitelistCount(): Int = whitelistNumbers.size
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
