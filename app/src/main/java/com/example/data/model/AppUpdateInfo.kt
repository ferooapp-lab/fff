package com.example.data.model

data class AppUpdateInfo(
    val currentVersion: String = "1.0.0",
    val latestVersion: String = "1.2.0",
    val versionCode: Int = 120,
    val releaseDate: String = "19 Eylül 2026",
    val isUpdateAvailable: Boolean = true,
    val isMandatory: Boolean = false,
    val downloadSizeMb: Float = 14.5f,
    val changelog: List<String> = listOf(
        "Yeni 4K UHD ve Düşük Veri Modu (Data Saver) desteği",
        "Otomatik Uyku Zamanlayıcısı (Sleep Timer: 15-60 dk)",
        "Gelecek programlar için anlık hatırlatıcı (Program Reminder)",
        "Resim İçinde Resim (Picture-in-Picture / PiP) oynatma",
        "Kanal paylaşımı ve canlı izleyici istatistikleri",
        "Performans, düşük gecikme ve bellek optimizasyonu"
    )
)
