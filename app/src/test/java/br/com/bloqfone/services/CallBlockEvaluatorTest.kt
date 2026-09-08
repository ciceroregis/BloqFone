package br.com.bloqfone.services

import br.com.bloqfone.data.HANDLE_PRESENTATION_ALLOWED
import br.com.bloqfone.data.HANDLE_PRESENTATION_RESTRICTED
import br.com.bloqfone.data.HANDLE_PRESENTATION_UNAVAILABLE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CallBlockEvaluatorTest {

    private fun defaultSnapshot(
        isFocusModeEnabled: Boolean = false,
        shouldBlockUnknownNumbers: Boolean = false,
        shouldBlockPrivateNumbers: Boolean = false,
        shouldBlockNoCallerId: Boolean = false,
        shouldBlockInternationalNumbers: Boolean = false,
        shouldBlockTelemarketing: Boolean = false,
        shouldBlockRobocalls: Boolean = false,
        shouldBlockSpam: Boolean = false,
        whitelistNumbers: Set<String> = emptySet(),
        blacklistNumbers: Set<String> = emptySet(),
        blockedCountryCodes: Set<String> = emptySet(),
        blockedDdds: Set<String> = emptySet()
    ) = BlockingSnapshot(
        isFocusModeEnabled = isFocusModeEnabled,
        shouldBlockUnknownNumbers = shouldBlockUnknownNumbers,
        shouldBlockPrivateNumbers = shouldBlockPrivateNumbers,
        shouldBlockNoCallerId = shouldBlockNoCallerId,
        shouldBlockInternationalNumbers = shouldBlockInternationalNumbers,
        shouldBlockTelemarketing = shouldBlockTelemarketing,
        shouldBlockRobocalls = shouldBlockRobocalls,
        shouldBlockSpam = shouldBlockSpam,
        whitelistNumbers = whitelistNumbers,
        blacklistNumbers = blacklistNumbers,
        blockedCountryCodes = blockedCountryCodes,
        blockedDdds = blockedDdds
    )

    @Test
    fun `whitelist has priority over other rules`() {
        val reason = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5511999999999",
            handlePresentation = HANDLE_PRESENTATION_RESTRICTED,
            isInContacts = false,
            snapshot = defaultSnapshot(
                shouldBlockPrivateNumbers = true,
                whitelistNumbers = setOf("+5511999999999")
            )
        )
        assertNull(reason)
    }

    @Test
    fun `blacklist blocks number`() {
        val reason = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5511988887777",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = true,
            snapshot = defaultSnapshot(blacklistNumbers = setOf("+5511988887777"))
        )
        assertEquals("lista negra", reason)
    }

    @Test
    fun `private presentation is blocked when enabled`() {
        val reason = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5511988887777",
            handlePresentation = HANDLE_PRESENTATION_RESTRICTED,
            isInContacts = true,
            snapshot = defaultSnapshot(shouldBlockPrivateNumbers = true)
        )
        assertEquals("número privado/oculto", reason)
    }

    @Test
    fun `unavailable caller id is blocked when enabled`() {
        val reason = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = null,
            handlePresentation = HANDLE_PRESENTATION_UNAVAILABLE,
            isInContacts = true,
            snapshot = defaultSnapshot(shouldBlockNoCallerId = true)
        )
        assertEquals("sem identificação", reason)
    }

    @Test
    fun `unknown number is blocked when enabled`() {
        val reason = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = true,
            snapshot = defaultSnapshot(shouldBlockUnknownNumbers = true)
        )
        assertEquals("número desconhecido", reason)
    }

    @Test
    fun `focus mode blocks non contacts`() {
        val reason = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5511988887777",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = false,
            snapshot = defaultSnapshot(isFocusModeEnabled = true)
        )
        assertEquals("modo foco", reason)
    }

    @Test
    fun `international numbers are blocked`() {
        val reason = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+14155552671",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = true,
            snapshot = defaultSnapshot(shouldBlockInternationalNumbers = true)
        )
        assertEquals("internacional", reason)
    }

    @Test
    fun `country code list blocks configured ddi`() {
        val reason = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+14155552671",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = true,
            snapshot = defaultSnapshot(blockedCountryCodes = setOf("1"))
        )
        assertEquals("país bloqueado (1)", reason)
    }

    @Test
    fun `ddd list blocks configured ddd`() {
        val reason = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5521988887777",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = true,
            snapshot = defaultSnapshot(blockedDdds = setOf("21"))
        )
        assertEquals("DDD bloqueado (21)", reason)
    }

    @Test
    fun `telemarketing, robocall and spam rules block when enabled`() {
        val telemarketing = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "030312345678",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = true,
            snapshot = defaultSnapshot(shouldBlockTelemarketing = true)
        )
        assertEquals("telemarketing", telemarketing)

        val robocall = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "11111111111",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = true,
            snapshot = defaultSnapshot(shouldBlockRobocalls = true)
        )
        assertEquals("robocall", robocall)

        val spam = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "90901234567",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = true,
            snapshot = defaultSnapshot(shouldBlockSpam = true)
        )
        assertEquals("spam", spam)
    }
}
