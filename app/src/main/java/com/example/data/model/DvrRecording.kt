package com.example.data.model

data class DvrRecording(
    val id: String,
    val channelId: String,
    val channelName: String,
    val programTitle: String,
    val category: String,
    val recordedDate: String,
    val durationText: String,
    val sizeMb: Int,
    val streamUrl: String
)
