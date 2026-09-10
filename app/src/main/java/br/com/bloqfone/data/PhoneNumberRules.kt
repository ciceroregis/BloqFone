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

fun isBrazilianTelemarketingNumber(number: ParsedPhoneNumber): Boolean {
    return number.digits.startsWith("0303") || number.digits.startsWith("550303")
}

fun isLikelyRobocall(number: ParsedPhoneNumber): Boolean {
    if (number.digits.length < 8) return false
    val uniqueDigits = number.digits.toSet().size
    return uniqueDigits <= 2
}

fun isLikelySpam(number: ParsedPhoneNumber): Boolean {
    if (number.digits.length < 8) return false
    return number.digits.startsWith("9090") ||
        number.digits.startsWith("3003") ||
        number.digits.endsWith("0000")
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
    val digits = when {
        number.digits.startsWith("55") && number.digits.length >= 12 -> number.digits.drop(2)
        else -> number.digits
    }
    return if (digits.length >= 10) digits.take(2) else null
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
