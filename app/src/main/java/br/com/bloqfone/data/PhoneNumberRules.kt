package br.com.bloqfone.data

const val HANDLE_PRESENTATION_ALLOWED = 1
const val HANDLE_PRESENTATION_RESTRICTED = 2
const val HANDLE_PRESENTATION_UNKNOWN = 3
const val HANDLE_PRESENTATION_PAYPHONE = 4
const val HANDLE_PRESENTATION_UNAVAILABLE = 5

data class ParsedPhoneNumber(
    val raw: String?,
    val normalized: String,
    val digits: String
) {
    val isUnavailable: Boolean
        get() = raw.isNullOrBlank() || normalized.isEmpty()
}

fun parsePhoneNumber(raw: String?): ParsedPhoneNumber {
    val cleanedRaw = raw?.trim().orEmpty()
    val normalized = if (cleanedRaw.startsWith("+")) {
        "+${cleanedRaw.drop(1).filter { it.isDigit() }}"
    } else {
        cleanedRaw.filter { it.isDigit() }
    }.let { if (it == "+") "" else it }

    val digits = normalized.filter { it.isDigit() }
    return ParsedPhoneNumber(raw = raw, normalized = normalized, digits = digits)
}

fun isPrivateOrHiddenPresentation(handlePresentation: Int): Boolean {
    return handlePresentation == HANDLE_PRESENTATION_RESTRICTED ||
        handlePresentation == HANDLE_PRESENTATION_PAYPHONE ||
        handlePresentation == HANDLE_PRESENTATION_UNKNOWN
}

fun isNoCallerId(handlePresentation: Int, parsed: ParsedPhoneNumber): Boolean {
    return handlePresentation == HANDLE_PRESENTATION_UNAVAILABLE || parsed.isUnavailable
}

fun extractNationalDigits(digits: String): String {
    var d = digits
    if (d.startsWith("55") && d.length >= 10) {
        d = d.drop(2)
    }
    if (d.startsWith("0") && !d.startsWith("0303") && !d.startsWith("0304") && !d.startsWith("0800") && !d.startsWith("0300")) {
        d = d.dropWhile { it == '0' }
    }
    return d
}

fun isBrazilianTelemarketingNumber(number: ParsedPhoneNumber): Boolean {
    if (number.digits.isEmpty()) return false
    val d = extractNationalDigits(number.digits)
    // Prefixo oficial Anatel 0303 (telemarketing) ou 0304 (tele-cobrança)
    if (d.startsWith("0303") || d.startsWith("0304")) return true
    // Quando precedido por DDD (2 dígitos, ex: 110303..., 210303..., 110304...)
    if (d.length >= 6 && (d.substring(2).startsWith("0303") || d.substring(2).startsWith("0304"))) return true
    return false
}

fun isLikelyRobocall(number: ParsedPhoneNumber): Boolean {
    if (number.digits.length < 8) return false
    val d = extractNationalDigits(number.digits)
    // Dígitos totais repetitivos (ex: 11111111111)
    if (d.toSet().size <= 2) return true
    // Parte do assinante (últimos 8 dígitos) com apenas 1 dígito repetido (ex: 90000000, 99999999)
    val subscriber = d.takeLast(8)
    if (subscriber.toSet().size == 1) return true
    // Finais óbvios de discadores automáticos em massa com 5 dígitos idênticos
    if (d.length >= 9 && d.takeLast(5).toSet().size == 1) return true
    return false
}

fun isLikelySpam(
    number: ParsedPhoneNumber,
    callerDisplayName: String? = null,
    callerVerificationStatus: Int = 0
): Boolean {
    // 1. Verificação de autenticidade da linha via operadora (STIR/SHAKEN - Spoofing detectado)
    // 2 = Call.Details.VERIFICATION_STATUS_FAILED
    if (callerVerificationStatus == 2) return true

    // 2. Identificador de chamadas do sistema ou operadora marcando spam
    if (isSpamDisplayName(callerDisplayName)) return true

    // 3. Verificação por padrões numéricos conhecidos
    if (number.digits.length < 8) return false
    val d = extractNationalDigits(number.digits)

    val spamPrefixes = listOf("9090", "3003", "4003", "4004", "0800")
    if (spamPrefixes.any { d.startsWith(it) }) return true
    // Quando precedido por DDD (ex: 113003..., 119090..., 114004...)
    if (d.length >= 10 && spamPrefixes.any { d.substring(2).startsWith(it) }) return true

    // Finais clássicos de centrais automáticas suspeitas (ex: 0000)
    if (d.endsWith("0000")) return true

    return false
}

fun isSpamDisplayName(displayName: String?): Boolean {
    if (displayName.isNullOrBlank()) return false
    val lower = displayName.lowercase(java.util.Locale.ROOT)
    return lower.contains("spam") ||
        lower.contains("suspeita") ||
        lower.contains("fraude") ||
        lower.contains("golpe") ||
        lower.contains("trote") ||
        lower.contains("presidio") ||
        lower.contains("presídio")
}

fun isTelemarketingDisplayName(displayName: String?): Boolean {
    if (displayName.isNullOrBlank()) return false
    val lower = displayName.lowercase(java.util.Locale.ROOT)
    return lower.contains("telemarketing") ||
        lower.contains("cobranca") ||
        lower.contains("cobrança") ||
        lower.contains("call center")
}

fun matchesNumberList(candidate: ParsedPhoneNumber, list: Set<String>): Boolean {
    if (candidate.digits.isEmpty() || list.isEmpty()) return false
    val candidateDigits = candidate.digits
    val candidateNational = extractNationalDigits(candidateDigits)
    val candidateLast8 = if (candidateDigits.length >= 8) candidateDigits.takeLast(8) else ""

    for (entry in list) {
        val entryDigits = entry.filter { it.isDigit() }
        if (entryDigits.isEmpty()) continue
        val entryNational = extractNationalDigits(entryDigits)
        val entryLast8 = if (entryDigits.length >= 8) entryDigits.takeLast(8) else ""

        // 1. Comparação exata normalizada (+55...)
        if (candidate.normalized == entry) return true
        // 2. Comparação exata de dígitos
        if (candidateDigits == entryDigits) return true
        // 3. Comparação nacional (elimina disparidade de ter ou não +55)
        if (candidateNational.isNotEmpty() && candidateNational == entryNational) return true
        // 4. Comparação pelos últimos 8 ou 9 dígitos garantindo mesmo DDD
        if (candidateLast8.isNotEmpty() && entryLast8.isNotEmpty() && candidateLast8 == entryLast8) {
            val candidateDdd = getBrazilDdd(candidate)
            val entryDdd = getBrazilDdd(parsePhoneNumber(entry))
            if (candidateDdd == null || entryDdd == null || candidateDdd == entryDdd) {
                return true
            }
        }
    }
    return false
}

fun getCountryCode(number: ParsedPhoneNumber): String? {
    if (!number.normalized.startsWith("+")) return null
    val digits = number.digits
    val maxLen = minOf(3, digits.length)
    for (size in 1..maxLen) {
        val code = digits.take(size)
        if (code == "55") return "55"
    }
    return digits.take(maxLen).ifBlank { null }
}

fun isInternationalForBrazil(number: ParsedPhoneNumber): Boolean {
    if (number.normalized.startsWith("+")) {
        return !number.normalized.startsWith("+55")
    }
    if (number.digits.startsWith("00")) {
        return !number.digits.startsWith("0055")
    }
    return false
}

fun getBrazilDdd(number: ParsedPhoneNumber): String? {
    val national = extractNationalDigits(number.digits)
    // No Brasil, números nacionais com DDD têm 10 dígitos (fixo) ou 11 dígitos (móvel)
    return if (national.length in 10..11) {
        national.take(2)
    } else null
}

fun formatPhoneNumberForDisplay(number: String): String {
    val trimmed = number.trim()
    if (trimmed.isEmpty()) return ""
    val hasPlus = trimmed.startsWith("+")
    val digits = trimmed.filter { it.isDigit() }

    if (hasPlus && digits.startsWith("55") && (digits.length == 12 || digits.length == 13)) {
        val ddd = digits.substring(2, 4)
        val rest = digits.substring(4)
        return if (rest.length == 9) {
            "+55 ($ddd) ${rest.substring(0, 5)}-${rest.substring(5)}"
        } else {
            "+55 ($ddd) ${rest.substring(0, 4)}-${rest.substring(4)}"
        }
    }

    if (!hasPlus) {
        if (digits.length == 11) {
            val ddd = digits.substring(0, 2)
            val rest = digits.substring(2)
            return "($ddd) ${rest.substring(0, 5)}-${rest.substring(5)}"
        }

        if (digits.length == 10) {
            val ddd = digits.substring(0, 2)
            val rest = digits.substring(2)
            return "($ddd) ${rest.substring(0, 4)}-${rest.substring(4)}"
        }
    }

    return if (hasPlus) "+$digits" else digits
}

fun maskPhoneNumberForLog(number: String?): String {
    if (number.isNullOrBlank()) return "[NÃO IDENTIFICADO]"
    val digits = number.filter { it.isDigit() }
    if (digits.length <= 4) return "****"
    return "****${digits.takeLast(4)}"
}
