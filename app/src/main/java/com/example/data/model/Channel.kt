package com.example.data.model

data class Channel(
    val id: String,
    val name: String,
    val category: String,
    val streamUrl: String,
    val logoUrl: String = "",
    val resolution: String = "1080p HD",
    val isLive: Boolean = true,
    val isActive: Boolean = true,
    val currentProgram: String = "Canlı Yayın",
    val nextProgram: String = "Bülten",
    val epgProgress: Float = 0.45f,
    val isFavorite: Boolean = false,
    val encryptedToken: String = "",
    val bitrateMbps: Float = 5.8f,
    val isTrending: Boolean = false,
    val is4K: Boolean = false,
    val viewerCount: Int = 14200
)

enum class ChannelCategory(val id: String, val displayNameTr: String, val iconName: String) {
    ALL("all", "Tümü", "all"),
    NATIONAL("national", "Ulusal", "tv"),
    NEWS("news", "Haber", "newspaper"),
    SPORTS("sports", "Spor", "sports"),
    DOCUMENTARY("doc", "Belgesel", "nature"),
    CINEMA("cinema", "Sinema", "movie"),
    KIDS("kids", "Çocuk", "child"),
    MUSIC("music", "Müzik", "music"),
    ENTERTAINMENT("entertainment", "Eğlence", "theater")
}
