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
        hasContactsPermission: Boolean = true,
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
        hasContactsPermission = hasContactsPermission,
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
            snapshot = defaultSnapshot(isFocusModeEnabled = true, hasContactsPermission = true)
        )
        assertEquals("modo foco", reason)
    }

    @Test
    fun `focus mode does not block when contacts permission is not granted`() {
        val reason = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5511988887777",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = false,
            snapshot = defaultSnapshot(isFocusModeEnabled = true, hasContactsPermission = false)
        )
        assertNull(reason)
    }

    @Test
    fun `telemarketing with ddd and 0304 are blocked`() {
        val call0303WithDdd = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+55110303123456",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = true,
            snapshot = defaultSnapshot(shouldBlockTelemarketing = true)
        )
        assertEquals("telemarketing", call0303WithDdd)

        val call0304 = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "55110304123456",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = true,
            snapshot = defaultSnapshot(shouldBlockTelemarketing = true)
        )
        assertEquals("telemarketing", call0304)
    }

    @Test
    fun `spam with ddd and caller display name are blocked`() {
        val spamWithDdd = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+551130031234",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = true,
            snapshot = defaultSnapshot(shouldBlockSpam = true)
        )
        assertEquals("spam", spamWithDdd)

        val spamByName = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5511988887777",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = false,
            snapshot = defaultSnapshot(shouldBlockSpam = true),
            callerDisplayName = "Suspeita de Spam"
        )
        assertEquals("spam", spamByName)
    }

    @Test
    fun `blacklist matches numbers saved without country code or with mask`() {
        val reason = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5511988887777",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = false,
            snapshot = defaultSnapshot(blacklistNumbers = setOf("11988887777"))
        )
        assertEquals("lista negra", reason)
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

    @Test
    fun `free mode allows safe calls and blocks blacklist`() {
        val freeSnapshot = defaultSnapshot(
            shouldBlockUnknownNumbers = true,
            shouldBlockPrivateNumbers = true,
            shouldBlockNoCallerId = true,
            blacklistNumbers = setOf("+5511999991111"),
            whitelistNumbers = setOf("+5511999992222")
        )

        // Safe unknown-to-blacklist number allowed
        val safeCall = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5511988887777",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = false,
            snapshot = freeSnapshot
        )
        assertNull(safeCall)

        // Blacklisted number blocked
        val blacklistedCall = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5511999991111",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = false,
            snapshot = freeSnapshot
        )
        assertEquals("lista negra", blacklistedCall)

        // Whitelisted number always allowed even if restricted
        val whitelistedCall = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5511999992222",
            handlePresentation = HANDLE_PRESENTATION_RESTRICTED,
            isInContacts = false,
            snapshot = freeSnapshot
        )
        assertNull(whitelistedCall)
    }

    @Test
    fun `specific threat rules take precedence over focus mode`() {
        // When focus mode is enabled and number is not in contacts:
        // Telemarketing should report "telemarketing", NOT "modo foco"
        val telemarketingCall = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "030312345678",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = false,
            snapshot = defaultSnapshot(isFocusModeEnabled = true, shouldBlockTelemarketing = true)
        )
        assertEquals("telemarketing", telemarketingCall)

        // Spam should report "spam", NOT "modo foco"
        val spamCall = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "90901234567",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = false,
            snapshot = defaultSnapshot(isFocusModeEnabled = true, shouldBlockSpam = true)
        )
        assertEquals("spam", spamCall)

        // Blocked DDD should report "DDD bloqueado (21)", NOT "modo foco"
        val dddCall = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5521988887777",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = false,
            snapshot = defaultSnapshot(isFocusModeEnabled = true, blockedDdds = setOf("21"))
        )
        assertEquals("DDD bloqueado (21)", dddCall)

        // Blacklist should report "lista negra", NOT "modo foco"
        val blacklistCall = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5511988887777",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = false,
            snapshot = defaultSnapshot(isFocusModeEnabled = true, blacklistNumbers = setOf("+5511988887777"))
        )
        assertEquals("lista negra", blacklistCall)
    }

    @Test
    fun `when focus mode is disabled non contacts without rules are allowed`() {
        val call = CallBlockEvaluator.evaluateBlockReason(
            rawIncomingNumber = "+5511988887777",
            handlePresentation = HANDLE_PRESENTATION_ALLOWED,
            isInContacts = false,
            snapshot = defaultSnapshot(isFocusModeEnabled = false)
        )
        assertNull(call)
    }
}
