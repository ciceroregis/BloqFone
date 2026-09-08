package br.com.bloqfone.features

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureAccessPolicyTest {

    @Test
    fun `free tier keeps core features available and premium locked`() {
        assertTrue(FeatureAccessPolicy.isUnlocked(AppFeature.BLOCK_SPECIFIC, SubscriptionTier.FREE))
        assertTrue(FeatureAccessPolicy.isUnlocked(AppFeature.BLACKLIST, SubscriptionTier.FREE))
        assertTrue(FeatureAccessPolicy.isUnlocked(AppFeature.WHITELIST, SubscriptionTier.FREE))

        assertFalse(FeatureAccessPolicy.isUnlocked(AppFeature.MODE_FOCUS, SubscriptionTier.FREE))
        assertFalse(FeatureAccessPolicy.isUnlocked(AppFeature.BLOCK_TELEMARKETING, SubscriptionTier.FREE))
        assertFalse(FeatureAccessPolicy.isUnlocked(AppFeature.SEND_TO_VOICEMAIL, SubscriptionTier.FREE))
    }

    @Test
    fun `premium tier unlocks advanced protection features`() {
        assertTrue(FeatureAccessPolicy.isUnlocked(AppFeature.MODE_FOCUS, SubscriptionTier.PREMIUM))
        assertTrue(FeatureAccessPolicy.isUnlocked(AppFeature.BLOCK_INTERNATIONAL, SubscriptionTier.PREMIUM))
        assertTrue(FeatureAccessPolicy.isUnlocked(AppFeature.BLOCK_COUNTRY, SubscriptionTier.PREMIUM))
        assertTrue(FeatureAccessPolicy.isUnlocked(AppFeature.BLOCK_DDD, SubscriptionTier.PREMIUM))
        assertTrue(FeatureAccessPolicy.isUnlocked(AppFeature.BLOCK_ROBOCALLS, SubscriptionTier.PREMIUM))
        assertTrue(FeatureAccessPolicy.isUnlocked(AppFeature.BLOCK_SPAM, SubscriptionTier.PREMIUM))
    }
}
