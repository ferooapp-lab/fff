package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val streamUrl: String,
    val logoUrl: String = "",
    val resolution: String = "1080p",
    val isLive: Boolean = true,
    val isActive: Boolean = true,
    val currentProgram: String = "Canlı Yayın",
    val nextProgram: String = "Günün Özeti",
    val epgProgress: Float = 0.45f,
    val isFavorite: Boolean = false,
    val encryptedToken: String = "",
    val bitrateMbps: Float = 5.8f,
    val sortOrder: Int = 0
)

@Entity(tableName = "watch_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val channelId: String,
    val channelName: String,
    val category: String,
    val resolution: String = "1080p",
    val timestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 1
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val channelId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val priority: String = "NORMAL",
    val category: String = "Genel"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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

@Entity(tableName = "community_polls")
data class CommunityPollEntity(
    @PrimaryKey val id: String,
    val channelId: String = "all",
    val question: String,
    val option1: String,
    val votes1: Int = 0,
    val option2: String,
    val votes2: Int = 0,
    val option3: String = "",
    val votes3: Int = 0,
    val userVotedOption: Int = 0, // 0 = not voted, 1, 2, 3
    val isActive: Boolean = true
)

