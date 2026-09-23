package com.example.data.model

data class ProjectMilestone(
    val phaseId: String,
    val title: String,
    val description: String,
    val progressPercent: Int,
    val status: MilestoneStatus,
    val deliverables: List<String>
)

enum class MilestoneStatus {
    COMPLETED, IN_PROGRESS, SCHEDULED
}

data class ProjectReport(
    val projectName: String = "Canlı TV Global Platformu",
    val overallProgress: Int = 100,
    val version: String = "v1.0.0-PROD",
    val lastUpdate: String = "2026-09-19",
    val milestones: List<ProjectMilestone> = listOf(
        ProjectMilestone(
            phaseId = "FAZ-1",
            title = "Mimari Altyapı & Canlı Yayın Çekirdeği",
            description = "Jetpack Compose, Room Veritabanı ve Akış Motoru Entegrasyonu",
            progressPercent = 100,
            status = MilestoneStatus.COMPLETED,
            deliverables = listOf(
                "M3 Modern Tema ve Tam Ekran Edge-to-Edge Desteği",
                "Canlı Video Akış Motoru & EPG Program Rehberi",
                "Jest Kontrolleri (Ses & Parlaklık & En-Boy Oranı)"
            )
        ),
        ProjectMilestone(
            phaseId = "FAZ-2",
            title = "Kanal Yönetimi & Admin Paneli",
            description = "Yetkili yönetici paneli, kanal ekleme/düzenleme/silme ve yayın durumu kontrolü",
            progressPercent = 100,
            status = MilestoneStatus.COMPLETED,
            deliverables = listOf(
                "PIN Güvenlikli Yönetici Paneli",
                "Kanal CRUD İşlemleri ve Anlık Durum Değiştirme",
                "Kategori Bazlı Kanal Hiyerarşisi"
            )
        ),
        ProjectMilestone(
            phaseId = "FAZ-3",
            title = "Anlık Bildirim & Favori Sistemi",
            description = "Yöneticiden son dakika bildirimleri yayını ve kişisel favori kanallar",
            progressPercent = 100,
            status = MilestoneStatus.COMPLETED,
            deliverables = listOf(
                "Tüm Kullanıcılara Anlık Broadcast Bildirim Sistemi",
                "Tek Dokunuşla Favori Kanal Ekleme / Çıkarma",
                "Okunmamış Bildirim Rozeti ve Detay Ekranı"
            )
        ),
        ProjectMilestone(
            phaseId = "FAZ-4",
            title = "Uçtan Uca Şifreleme & Yüksek Başarımlı Önbellekleme",
            description = "AES-GCM 256-bit veri güvenliği ve bellek/disk hibrit önbellek mimarisi",
            progressPercent = 100,
            status = MilestoneStatus.COMPLETED,
            deliverables = listOf(
                "AES-256 GCM Şifreleme ve SHA-256 Bütünlük Kontrolü",
                "Kanal ve Akış Metaverisi için Çift Katmanlı Önbellek (L1 Memory + L2 Room)",
                "Önbellek Temizleme ve Başarım Oranı İstatistikleri"
            )
        ),
        ProjectMilestone(
            phaseId = "FAZ-5",
            title = "Kişiselleştirilmiş Geçmiş & Global Çoklu Dil Desteği",
            description = "İzleme geçmişi kaydı, karanlık mod ve 4 dilde yerelleştirme",
            progressPercent = 100,
            status = MilestoneStatus.COMPLETED,
            deliverables = listOf(
                "Kişiselleştirilmiş İzleme Geçmişi ve 'Kaldığın Yerden İzle'",
                "OLED Karanlık / Aydınlık Mod Dinamik Geçişi",
                "Türkçe, İngilizce, Almanca ve İspanyolca Dil Desteği"
            )
        )
    ),
    val systemMetrics: SystemMetrics = SystemMetrics(
        cacheHitRate = 98.4f,
        averageLatencyMs = 42,
        activeChannels = 18,
        encryptedTokensActive = 18,
        encryptionAlgorithm = "AES-GCM-256"
    )
)

data class SystemMetrics(
    val cacheHitRate: Float,
    val averageLatencyMs: Int,
    val activeChannels: Int,
    val encryptedTokensActive: Int,
    val encryptionAlgorithm: String
)
