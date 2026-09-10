package br.com.bloqfone.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

class ConfigRepository(context: Context) {

    // Creates a local SharedPreferences file to save the user's settings
    private val prefs: SharedPreferences = context.getSharedPreferences("bloqfone_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "BloqFone:ConfigRepo"
        private const val KEY_PREMIUM_USER = "is_premium_user"
        private const val KEY_FOCUS_MODE = "focus_mode"
        private const val KEY_BLOCK_UNKNOWN = "block_unknown_numbers"
        private const val KEY_BLOCK_PRIVATE = "block_private_numbers"
        private const val KEY_BLOCK_NO_CALLER_ID = "block_no_caller_id"
        private const val KEY_BLOCK_INTERNATIONAL = "block_international_numbers"
        private const val KEY_BLOCK_TELEMARKETING = "block_telemarketing"
        private const val KEY_BLOCK_ROBOCALLS = "block_robocalls"
        private const val KEY_BLOCK_SPAM = "block_spam"
        private const val KEY_SILENT_BLOCKING = "silent_blocking"
        private const val KEY_SEND_TO_VOICEMAIL = "send_to_voicemail"
        private const val KEY_AUTO_REJECT = "auto_reject"
        private const val KEY_BLACKLIST = "blacklist_numbers"
        private const val KEY_WHITELIST = "whitelist_numbers"
        private const val KEY_BLOCKED_COUNTRIES = "blocked_country_codes"
        private const val KEY_BLOCKED_DDDS = "blocked_ddds"
    }

    var isPremiumUser: Boolean
        get() = prefs.getBoolean(KEY_PREMIUM_USER, false)
        set(value) = prefs.edit { putBoolean(KEY_PREMIUM_USER, value) }

    private fun getSet(key: String): Set<String> = prefs.getStringSet(key, emptySet())?.toSet() ?: emptySet()
    private fun putSet(key: String, value: Set<String>) = prefs.edit { putStringSet(key, value) }

    private fun sanitizePhone(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""
        val normalized = if (trimmed.startsWith("+")) {
            "+${trimmed.drop(1).filter { it.isDigit() }}"
        } else {
            trimmed.filter { it.isDigit() }
        }
        return if (normalized == "+") "" else normalized
    }

    private fun sanitizeDigits(input: String): String = input.filter { it.isDigit() }

    var isFocusModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_FOCUS_MODE, false)
        set(value) = prefs.edit { putBoolean(KEY_FOCUS_MODE, value) }

    var shouldBlockUnknownNumbers: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_UNKNOWN, true)
        set(value) = prefs.edit { putBoolean(KEY_BLOCK_UNKNOWN, value) }

    var shouldBlockPrivateNumbers: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_PRIVATE, true)
        set(value) = prefs.edit { putBoolean(KEY_BLOCK_PRIVATE, value) }

    var shouldBlockNoCallerId: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_NO_CALLER_ID, true)
        set(value) = prefs.edit { putBoolean(KEY_BLOCK_NO_CALLER_ID, value) }

    var shouldBlockInternationalNumbers: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_INTERNATIONAL, false)
        set(value) = prefs.edit { putBoolean(KEY_BLOCK_INTERNATIONAL, value) }

    var shouldBlockTelemarketing: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_TELEMARKETING, true)
        set(value) = prefs.edit { putBoolean(KEY_BLOCK_TELEMARKETING, value) }

    var shouldBlockRobocalls: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_ROBOCALLS, true)
        set(value) = prefs.edit { putBoolean(KEY_BLOCK_ROBOCALLS, value) }

    var shouldBlockSpam: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_SPAM, true)
        set(value) = prefs.edit { putBoolean(KEY_BLOCK_SPAM, value) }

    var isSilentBlockingEnabled: Boolean
        get() = prefs.getBoolean(KEY_SILENT_BLOCKING, true)
        set(value) = prefs.edit { putBoolean(KEY_SILENT_BLOCKING, value) }

    var shouldSendToVoicemail: Boolean
        get() = prefs.getBoolean(KEY_SEND_TO_VOICEMAIL, false)
        set(value) = prefs.edit { putBoolean(KEY_SEND_TO_VOICEMAIL, value) }

    var shouldAutoReject: Boolean
        get() = prefs.getBoolean(KEY_AUTO_REJECT, true)
        set(value) = prefs.edit { putBoolean(KEY_AUTO_REJECT, value) }

    fun getBlacklistNumbers(): Set<String> = getSet(KEY_BLACKLIST)
    fun getWhitelistNumbers(): Set<String> = getSet(KEY_WHITELIST)
    fun getBlockedCountryCodes(): Set<String> = getSet(KEY_BLOCKED_COUNTRIES)
    fun getBlockedDdds(): Set<String> = getSet(KEY_BLOCKED_DDDS)

    fun addToBlacklist(number: String): Boolean {
        val normalized = sanitizePhone(number)
        if (normalized.isEmpty()) {
            Log.w(TAG, "[ERRO] Falha ao adicionar à lista negra: número inválido ou vazio ('${maskPhoneNumberForLog(number)}').")
            return false
        }
        return try {
            val updated = getBlacklistNumbers().toMutableSet().apply { add(normalized) }
            putSet(KEY_BLACKLIST, updated)
            Log.i(TAG, "[SUCESSO] Número adicionado à lista negra: ${maskPhoneNumberForLog(normalized)}.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Exceção ao salvar número na lista negra: ${maskPhoneNumberForLog(normalized)}.", e)
            false
        }
    }

    fun removeFromBlacklist(number: String) {
        val normalized = sanitizePhone(number)
        if (normalized.isEmpty()) {
            Log.w(TAG, "[AVISO] Falha ao remover da lista negra: número inválido ou vazio.")
            return
        }
        try {
            val updated = getBlacklistNumbers().toMutableSet().apply { remove(normalized) }
            putSet(KEY_BLACKLIST, updated)
            Log.i(TAG, "[SUCESSO] Número removido da lista negra: ${maskPhoneNumberForLog(normalized)}.")
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Exceção ao remover número da lista negra: ${maskPhoneNumberForLog(normalized)}.", e)
        }
    }

    fun addToWhitelist(number: String): Boolean {
        val normalized = sanitizePhone(number)
        if (normalized.isEmpty()) {
            Log.w(TAG, "[ERRO] Falha ao adicionar à lista branca: número inválido ou vazio ('${maskPhoneNumberForLog(number)}').")
            return false
        }
        return try {
            val updated = getWhitelistNumbers().toMutableSet().apply { add(normalized) }
            putSet(KEY_WHITELIST, updated)
            Log.i(TAG, "[SUCESSO] Número adicionado à lista branca: ${maskPhoneNumberForLog(normalized)}.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Exceção ao salvar número na lista branca: ${maskPhoneNumberForLog(normalized)}.", e)
            false
        }
    }

    fun removeFromWhitelist(number: String) {
        val normalized = sanitizePhone(number)
        if (normalized.isEmpty()) {
            Log.w(TAG, "[AVISO] Falha ao remover da lista branca: número inválido ou vazio.")
            return
        }
        try {
            val updated = getWhitelistNumbers().toMutableSet().apply { remove(normalized) }
            putSet(KEY_WHITELIST, updated)
            Log.i(TAG, "[SUCESSO] Número removido da lista branca: ${maskPhoneNumberForLog(normalized)}.")
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Exceção ao remover número da lista branca: ${maskPhoneNumberForLog(normalized)}.", e)
        }
    }

    fun addBlockedCountryCode(code: String): Boolean {
        val normalized = sanitizeDigits(code)
        if (normalized.length !in 1..3) {
            Log.w(TAG, "[ERRO] Falha ao adicionar código de país: '$code' inválido (deve ter 1 a 3 dígitos).")
            return false
        }
        return try {
            val updated = getBlockedCountryCodes().toMutableSet().apply { add(normalized) }
            putSet(KEY_BLOCKED_COUNTRIES, updated)
            Log.i(TAG, "[SUCESSO] Código de país DDI (+$normalized) adicionado aos bloqueios.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Exceção ao salvar código de país bloqueado: $normalized.", e)
            false
        }
    }

    fun removeBlockedCountryCode(code: String) {
        val normalized = sanitizeDigits(code)
        if (normalized.isEmpty()) {
            Log.w(TAG, "[AVISO] Falha ao remover código de país: entrada vazia.")
            return
        }
        try {
            val updated = getBlockedCountryCodes().toMutableSet().apply { remove(normalized) }
            putSet(KEY_BLOCKED_COUNTRIES, updated)
            Log.i(TAG, "[SUCESSO] Código de país DDI (+$normalized) removido dos bloqueios.")
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Exceção ao remover código de país bloqueado: $normalized.", e)
        }
    }

    fun addBlockedDdd(ddd: String): Boolean {
        val normalized = sanitizeDigits(ddd)
        if (normalized.length != 2) {
            Log.w(TAG, "[ERRO] Falha ao adicionar DDD: '$ddd' inválido (deve conter exatamente 2 dígitos).")
            return false
        }
        return try {
            val updated = getBlockedDdds().toMutableSet().apply { add(normalized) }
            putSet(KEY_BLOCKED_DDDS, updated)
            Log.i(TAG, "[SUCESSO] DDD ($normalized) adicionado aos bloqueios.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Exceção ao salvar DDD bloqueado: $normalized.", e)
            false
        }
    }

    fun removeBlockedDdd(ddd: String) {
        val normalized = sanitizeDigits(ddd)
        if (normalized.isEmpty()) {
            Log.w(TAG, "[AVISO] Falha ao remover DDD: entrada vazia.")
            return
        }
        try {
            val updated = getBlockedDdds().toMutableSet().apply { remove(normalized) }
            putSet(KEY_BLOCKED_DDDS, updated)
            Log.i(TAG, "[SUCESSO] DDD ($normalized) removido dos bloqueios.")
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Exceção ao remover DDD bloqueado: $normalized.", e)
        }
    }
}