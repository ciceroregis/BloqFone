package br.com.bloqfone.services

import br.com.bloqfone.data.ParsedPhoneNumber
import br.com.bloqfone.data.getBrazilDdd
import br.com.bloqfone.data.isBrazilianTelemarketingNumber
import br.com.bloqfone.data.isInternationalForBrazil
import br.com.bloqfone.data.isLikelyRobocall
import br.com.bloqfone.data.isLikelySpam
import br.com.bloqfone.data.isNoCallerId
import br.com.bloqfone.data.isPrivateOrHiddenPresentation
import br.com.bloqfone.data.isTelemarketingDisplayName
import br.com.bloqfone.data.matchesNumberList
import br.com.bloqfone.data.parsePhoneNumber

data class BlockingSnapshot(
    val isFocusModeEnabled: Boolean,
    val hasContactsPermission: Boolean = true,
    val shouldBlockUnknownNumbers: Boolean,
    val shouldBlockPrivateNumbers: Boolean,
    val shouldBlockNoCallerId: Boolean,
    val shouldBlockInternationalNumbers: Boolean,
    val shouldBlockTelemarketing: Boolean,
    val shouldBlockRobocalls: Boolean,
    val shouldBlockSpam: Boolean,
    val whitelistNumbers: Set<String>,
    val blacklistNumbers: Set<String>,
    val blockedCountryCodes: Set<String>,
    val blockedDdds: Set<String>
)

object CallBlockEvaluator {
    fun evaluateBlockReason(
        rawIncomingNumber: String?,
        handlePresentation: Int,
        isInContacts: Boolean,
        snapshot: BlockingSnapshot,
        callerDisplayName: String? = null,
        callerVerificationStatus: Int = 0
    ): String? {
        val number = parsePhoneNumber(rawIncomingNumber)

        if (isWhitelisted(number, snapshot)) return null
        if (isBlacklisted(number, snapshot)) return "lista negra"
        if (shouldBlockPrivate(handlePresentation, snapshot)) return "número privado/oculto"
        if (shouldBlockNoCallerId(handlePresentation, number, snapshot)) return "sem identificação"
        if (shouldBlockUnknown(number, snapshot)) return "número desconhecido"

        if (snapshot.shouldBlockTelemarketing && (isBrazilianTelemarketingNumber(number) || isTelemarketingDisplayName(callerDisplayName))) {
            return "telemarketing"
        }
        if (snapshot.shouldBlockRobocalls && isLikelyRobocall(number)) return "robocall"
        if (snapshot.shouldBlockSpam && isLikelySpam(number, callerDisplayName, callerVerificationStatus)) {
            return "spam"
        }

        val dddMatch = blockedDddMatch(number, snapshot)
        if (dddMatch != null) return "DDD bloqueado ($dddMatch)"

        val countryCodeMatch = blockedCountryCodeMatch(number, snapshot)
        if (countryCodeMatch != null) return "país bloqueado ($countryCodeMatch)"

        if (shouldBlockInternational(number, snapshot)) return "internacional"

        if (shouldBlockByFocusMode(isInContacts, snapshot)) return "modo foco"
        return null
    }

    private fun isWhitelisted(number: ParsedPhoneNumber, snapshot: BlockingSnapshot): Boolean {
        return matchesNumberList(number, snapshot.whitelistNumbers)
    }

    private fun isBlacklisted(number: ParsedPhoneNumber, snapshot: BlockingSnapshot): Boolean {
        return matchesNumberList(number, snapshot.blacklistNumbers)
    }

    private fun shouldBlockPrivate(handlePresentation: Int, snapshot: BlockingSnapshot): Boolean {
        return snapshot.shouldBlockPrivateNumbers && isPrivateOrHiddenPresentation(handlePresentation)
    }

    private fun shouldBlockNoCallerId(
        handlePresentation: Int,
        parsedPhoneNumber: ParsedPhoneNumber,
        snapshot: BlockingSnapshot
    ): Boolean {
        return snapshot.shouldBlockNoCallerId && isNoCallerId(handlePresentation, parsedPhoneNumber)
    }

    private fun shouldBlockUnknown(number: ParsedPhoneNumber, snapshot: BlockingSnapshot): Boolean {
        return snapshot.shouldBlockUnknownNumbers && number.isUnavailable
    }

    private fun shouldBlockByFocusMode(isInContacts: Boolean, snapshot: BlockingSnapshot): Boolean {
        // Modo foco só bloqueia quando a permissão de contatos estiver ativa e o número não for da agenda.
        // Se a permissão não foi concedida pelo usuário, o Modo Foco não bloqueia cegamente todas as chamadas.
        return snapshot.isFocusModeEnabled && snapshot.hasContactsPermission && !isInContacts
    }

    private fun shouldBlockInternational(number: ParsedPhoneNumber, snapshot: BlockingSnapshot): Boolean {
        return snapshot.shouldBlockInternationalNumbers && isInternationalForBrazil(number)
    }

    private fun blockedCountryCodeMatch(number: ParsedPhoneNumber, snapshot: BlockingSnapshot): String? {
        if (snapshot.blockedCountryCodes.isEmpty()) return null
        val normalizedDigits = when {
            number.normalized.startsWith("+") -> number.digits
            number.digits.startsWith("00") -> number.digits.drop(2)
            else -> return null
        }
        return snapshot.blockedCountryCodes
            .sortedByDescending { it.length }
            .firstOrNull { normalizedDigits.startsWith(it) }
    }

    private fun blockedDddMatch(number: ParsedPhoneNumber, snapshot: BlockingSnapshot): String? {
        if (snapshot.blockedDdds.isEmpty()) return null
        val ddd = getBrazilDdd(number) ?: return null
        return ddd.takeIf { snapshot.blockedDdds.contains(it) }
    }
}
