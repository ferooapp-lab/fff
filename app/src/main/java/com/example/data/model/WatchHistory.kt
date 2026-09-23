package com.example.data.model

data class WatchHistory(
    val id: Long = 0,
    val channelId: String,
    val channelName: String,
    val category: String,
    val resolution: String = "1080p",
    val timestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 1
)
