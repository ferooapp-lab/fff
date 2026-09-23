package com.example.data.model

data class ChatMessage(
    val id: Long = 0,
    val channelId: String = "all",
    val channelName: String = "Genel Sohbet",
    val senderName: String,
    val senderRole: String = "İzleyici", // "Yönetici", "VIP", "Moderatör", "İzleyici"
    val avatarColorHex: Long = 0xFF2979FF,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val isUserMessage: Boolean = false,
    val isPinned: Boolean = false
)

data class CommunityPoll(
    val id: String,
    val channelId: String = "all",
    val question: String,
    val option1: String,
    val votes1: Int = 0,
    val option2: String,
    val votes2: Int = 0,
    val option3: String = "",
    val votes3: Int = 0,
    val userVotedOption: Int = 0,
    val isActive: Boolean = true
) {
    val totalVotes: Int get() = votes1 + votes2 + votes3
    val percent1: Float get() = if (totalVotes > 0) votes1.toFloat() / totalVotes else 0f
    val percent2: Float get() = if (totalVotes > 0) votes2.toFloat() / totalVotes else 0f
    val percent3: Float get() = if (totalVotes > 0 && option3.isNotEmpty()) votes3.toFloat() / totalVotes else 0f
}

enum class SoundPreset(val id: String, val titleTr: String, val subtitleTr: String, val iconName: String) {
    STANDARD("std", "Standart", "Dengeli stüdyo sesi", "graphic_eq"),
    CINEMA("cinema", "Sinema (Bass Boost)", "Derin bas ve surround atmosfer", "movie"),
    SPORTS("sports", "Spor & Stadyum", "Seyirci tezahüratı ve canlı saha hissi", "sports_soccer"),
    NEWS("news", "Net Haber & Vokal", "Konuşmacı seslerini öne çıkarır", "record_voice_over"),
    MUSIC("music", "Canlı Konser", "Geniş dinamik aralık ve parlak tizler", "music_note")
}

enum class ChannelSortOrder(val id: String, val titleTr: String) {
    POPULAR("popular", "Popüler"),
    VIEWERS("viewers", "En Çok İzlenen"),
    NAME("name", "A - Z"),
    QUALITY("quality", "4K / Full HD")
}
