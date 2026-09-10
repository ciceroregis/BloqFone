package br.com.bloqfone.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneNumberRulesTest {

    @Test
    fun `parsePhoneNumber normalizes symbols`() {
        val parsed = parsePhoneNumber("+55 (11) 98888-7777")
        assertEquals("+5511988887777", parsed.normalized)
        assertEquals("5511988887777", parsed.digits)
    }

    @Test
    fun `parsePhoneNumber returns unavailable for empty input`() {
        val parsed = parsePhoneNumber("  ")
        assertTrue(parsed.isUnavailable)
        assertEquals("", parsed.normalized)
    }

    @Test
    fun `private presentation is detected`() {
        assertTrue(isPrivateOrHiddenPresentation(HANDLE_PRESENTATION_RESTRICTED))
        assertTrue(isPrivateOrHiddenPresentation(HANDLE_PRESENTATION_PAYPHONE))
        assertFalse(isPrivateOrHiddenPresentation(HANDLE_PRESENTATION_ALLOWED))
    }

    @Test
    fun `no caller id is detected by unavailable presentation`() {
        val parsed = parsePhoneNumber("+5511999999999")
        assertTrue(isNoCallerId(HANDLE_PRESENTATION_UNAVAILABLE, parsed))
    }

    @Test
    fun `telemarketing 0303 is detected`() {
        assertTrue(isBrazilianTelemarketingNumber(parsePhoneNumber("030312345678")))
        assertTrue(isBrazilianTelemarketingNumber(parsePhoneNumber("+55030312345678")))
        assertFalse(isBrazilianTelemarketingNumber(parsePhoneNumber("+5511988887777")))
    }

    @Test
    fun `robocall heuristic detects repeated digits`() {
        assertTrue(isLikelyRobocall(parsePhoneNumber("+5511111111111")))
        assertFalse(isLikelyRobocall(parsePhoneNumber("+5511988887777")))
    }

    @Test
    fun `spam heuristic detects known suspicious prefixes`() {
        assertTrue(isLikelySpam(parsePhoneNumber("90901234567")))
        assertTrue(isLikelySpam(parsePhoneNumber("30031234567")))
        assertTrue(isLikelySpam(parsePhoneNumber("1199990000")))
        assertFalse(isLikelySpam(parsePhoneNumber("1199991234")))
    }

    @Test
    fun `international detection works for plus and 00 formats`() {
        assertTrue(isInternationalForBrazil(parsePhoneNumber("+14155552671")))
        assertTrue(isInternationalForBrazil(parsePhoneNumber("0014155552671")))
        assertFalse(isInternationalForBrazil(parsePhoneNumber("+5511988887777")))
        assertFalse(isInternationalForBrazil(parsePhoneNumber("005511988887777")))
    }

    @Test
    fun `country code extraction supports brazil and non plus numbers`() {
        assertEquals("55", getCountryCode(parsePhoneNumber("+5511988887777")))
        assertEquals(null, getCountryCode(parsePhoneNumber("11988887777")))
    }

    @Test
    fun `ddd extraction works for local and with country code`() {
        assertEquals("11", getBrazilDdd(parsePhoneNumber("11988887777")))
        assertEquals("21", getBrazilDdd(parsePhoneNumber("+5521988887777")))
        assertEquals(null, getBrazilDdd(parsePhoneNumber("9999")))
    }

    @Test
    fun `formatPhoneNumberForDisplay formats Brazilian numbers cleanly`() {
        assertEquals("+55 (11) 98888-7777", formatPhoneNumberForDisplay("+5511988887777"))
        assertEquals("+55 (11) 3333-4444", formatPhoneNumberForDisplay("+551133334444"))
        assertEquals("(11) 98888-7777", formatPhoneNumberForDisplay("11988887777"))
        assertEquals("(11) 3333-4444", formatPhoneNumberForDisplay("1133334444"))
        assertEquals("+14155552671", formatPhoneNumberForDisplay("+14155552671"))
        assertEquals("", formatPhoneNumberForDisplay("   "))
    }

    @Test
    fun `maskPhoneNumberForLog masks digits safely for tracking logs`() {
        assertEquals("****7777", maskPhoneNumberForLog("+55 (11) 98888-7777"))
        assertEquals("****1234", maskPhoneNumberForLog("91234"))
        assertEquals("****", maskPhoneNumberForLog("1234"))
        assertEquals("****", maskPhoneNumberForLog("123"))
        assertEquals("[NÃO IDENTIFICADO]", maskPhoneNumberForLog(""))
        assertEquals("[NÃO IDENTIFICADO]", maskPhoneNumberForLog(null))
    }
}
