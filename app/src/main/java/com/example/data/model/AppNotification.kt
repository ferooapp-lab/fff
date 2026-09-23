package com.example.data.model

data class AppNotification(
    val id: Long = 0,
    val title: String,
    val message: String,
    val channelId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val priority: NotificationPriority = NotificationPriority.NORMAL,
    val category: String = "Genel"
)

enum class NotificationPriority {
    LOW, NORMAL, HIGH, BREAKING
}
