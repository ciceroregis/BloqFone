package br.com.bloqfone.features

enum class SubscriptionTier {
    FREE,
    PREMIUM
}

enum class AppFeature(
    val id: String,
    val tier: SubscriptionTier,
    val label: String
) {
    BLOCK_SPECIFIC("block_specific", SubscriptionTier.FREE, "Bloquear número específico"),
    BLACKLIST("blacklist", SubscriptionTier.FREE, "Lista negra"),
    WHITELIST("whitelist", SubscriptionTier.FREE, "Lista branca"),
    BLOCK_UNKNOWN("block_unknown", SubscriptionTier.FREE, "Bloquear números desconhecidos"),
    BLOCK_PRIVATE("block_private", SubscriptionTier.FREE, "Bloquear privados/ocultos"),
    BLOCK_NO_CALLER_ID("block_no_caller_id", SubscriptionTier.FREE, "Bloquear sem identificação"),
    AUTO_REJECT("auto_reject", SubscriptionTier.FREE, "Recusar automaticamente"),
    MODE_FOCUS("mode_focus", SubscriptionTier.PREMIUM, "Modo foco rigoroso"),
    BLOCK_INTERNATIONAL("block_international", SubscriptionTier.PREMIUM, "Bloquear internacionais"),
    BLOCK_COUNTRY("block_country", SubscriptionTier.PREMIUM, "Bloquear por DDI/país"),
    BLOCK_DDD("block_ddd", SubscriptionTier.PREMIUM, "Bloquear por DDD"),
    BLOCK_TELEMARKETING("block_telemarketing", SubscriptionTier.PREMIUM, "Bloquear telemarketing"),
    BLOCK_ROBOCALLS("block_robocalls", SubscriptionTier.PREMIUM, "Bloquear robocalls"),
    BLOCK_SPAM("block_spam", SubscriptionTier.PREMIUM, "Bloquear spam"),
    SILENT_BLOCKING("silent_blocking", SubscriptionTier.PREMIUM, "Bloqueio silencioso"),
    SEND_TO_VOICEMAIL("send_to_voicemail", SubscriptionTier.PREMIUM, "Enviar para correio de voz");
}

object FeatureAccessPolicy {
    fun isUnlocked(feature: AppFeature, tier: SubscriptionTier): Boolean {
        return feature.tier == SubscriptionTier.FREE || tier == SubscriptionTier.PREMIUM
    }

    fun premiumFeatures(): List<AppFeature> = AppFeature.entries.filter { it.tier == SubscriptionTier.PREMIUM }
}
