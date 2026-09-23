package com.example.data.repository

import com.example.data.cache.ChannelCacheManager
import com.example.data.db.AppDatabase
import com.example.data.db.ChannelEntity
import com.example.data.db.ChatMessageEntity
import com.example.data.db.CommunityPollEntity
import com.example.data.db.HistoryEntity
import com.example.data.db.NotificationEntity
import com.example.data.model.AppNotification
import com.example.data.model.Channel
import com.example.data.model.ChatMessage
import com.example.data.model.CommunityPoll
import com.example.data.model.NotificationPriority
import com.example.data.model.WatchHistory
import com.example.data.security.CryptoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TvRepository(
    private val database: AppDatabase,
    val cacheManager: ChannelCacheManager = ChannelCacheManager()
) {
    private val dao = database.tvDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (dao.getChannelsCount() == 0) {
                seedInitialData()
            }
        }
    }

    val allChannels: Flow<List<Channel>> = dao.getAllChannels().map { list ->
        val mapped = list.map { it.toModel() }
        cacheManager.putAll(mapped)
        mapped
    }

    val activeChannels: Flow<List<Channel>> = dao.getActiveChannels().map { list ->
        list.map { it.toModel() }
    }

    val favoriteChannels: Flow<List<Channel>> = dao.getFavoriteChannels().map { list ->
        list.map { it.toModel() }
    }

    val watchHistory: Flow<List<WatchHistory>> = dao.getWatchHistory().map { list ->
        list.map { it.toModel() }
    }

    val notifications: Flow<List<AppNotification>> = dao.getNotifications().map { list ->
        list.map { it.toModel() }
    }

    val unreadNotificationsCount: Flow<Int> = dao.getUnreadNotificationCount()

    fun getChatMessages(channelId: String): Flow<List<ChatMessage>> {
        return dao.getChatMessages(channelId).map { list -> list.map { it.toModel() } }
    }

    suspend fun sendChatMessage(message: ChatMessage) = withContext(Dispatchers.IO) {
        dao.insertChatMessage(message.toEntity())
    }

    suspend fun likeChatMessage(id: Long) = withContext(Dispatchers.IO) {
        dao.likeChatMessage(id)
    }

    fun getPolls(channelId: String): Flow<List<CommunityPoll>> {
        return dao.getPolls(channelId).map { list -> list.map { it.toModel() } }
    }

    suspend fun votePoll(pollId: String, optionIndex: Int) = withContext(Dispatchers.IO) {
        when (optionIndex) {
            1 -> dao.votePollOption1(pollId)
            2 -> dao.votePollOption2(pollId)
            3 -> dao.votePollOption3(pollId)
        }
    }

    fun getChannelsByCategory(category: String): Flow<List<Channel>> {
        return if (category == "all" || category == "Tümü") {
            activeChannels
        } else {
            dao.getChannelsByCategory(category).map { list -> list.map { it.toModel() } }
        }
    }

    suspend fun toggleFavorite(channelId: String, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        val newFav = !currentFavorite
        dao.setFavorite(channelId, newFav)
        cacheManager.get(channelId)?.let {
            cacheManager.put(it.copy(isFavorite = newFav))
        }
    }

    suspend fun toggleActive(channelId: String, currentActive: Boolean) = withContext(Dispatchers.IO) {
        val newActive = !currentActive
        dao.setActiveStatus(channelId, newActive)
        cacheManager.get(channelId)?.let {
            cacheManager.put(it.copy(isActive = newActive))
        }
    }

    suspend fun addChannel(channel: Channel) = withContext(Dispatchers.IO) {
        val encryptedToken = CryptoManager.createStreamToken(channel.id)
        val entity = channel.copy(encryptedToken = encryptedToken).toEntity()
        dao.insertChannel(entity)
        cacheManager.put(channel.copy(encryptedToken = encryptedToken))
    }

    suspend fun updateChannel(channel: Channel) = withContext(Dispatchers.IO) {
        val encryptedToken = CryptoManager.createStreamToken(channel.id)
        val entity = channel.copy(encryptedToken = encryptedToken).toEntity()
        dao.updateChannel(entity)
        cacheManager.put(channel.copy(encryptedToken = encryptedToken))
    }

    suspend fun deleteChannel(channelId: String) = withContext(Dispatchers.IO) {
        dao.deleteChannelById(channelId)
    }

    suspend fun recordWatchHistory(channel: Channel, durationMinutes: Int = 1) = withContext(Dispatchers.IO) {
        val entity = HistoryEntity(
            channelId = channel.id,
            channelName = channel.name,
            category = channel.category,
            resolution = channel.resolution,
            timestamp = System.currentTimeMillis(),
            durationMinutes = durationMinutes
        )
        dao.insertWatchHistory(entity)
    }

    suspend fun clearWatchHistory() = withContext(Dispatchers.IO) {
        dao.clearWatchHistory()
    }

    suspend fun deleteHistoryItem(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteHistoryItem(id)
    }

    suspend fun sendBroadcastNotification(
        title: String,
        message: String,
        channelId: String? = null,
        priority: NotificationPriority = NotificationPriority.NORMAL,
        category: String = "Canlı TV"
    ) = withContext(Dispatchers.IO) {
        val notification = NotificationEntity(
            title = title,
            message = message,
            channelId = channelId,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            priority = priority.name,
            category = category
        )
        dao.insertNotification(notification)
    }

    suspend fun markNotificationAsRead(id: Long) = withContext(Dispatchers.IO) {
        dao.markNotificationRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        dao.markAllNotificationsRead()
    }

    suspend fun clearNotifications() = withContext(Dispatchers.IO) {
        dao.clearAllNotifications()
    }

    fun flushCache() {
        cacheManager.clear()
    }

    private suspend fun seedInitialData() = withContext(Dispatchers.IO) {
        val initialChannels = listOf(
            ChannelEntity(
                id = "trt1",
                name = "TRT 1 HD",
                category = "Ulusal",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/TRT_1_logo_2021.svg/320px-TRT_1_logo_2021.svg.png",
                resolution = "1080p FHD",
                isLive = true,
                isActive = true,
                currentProgram = "Gönül Dağı - Yeni Bölüm",
                nextProgram = "Ana Haber Bülteni",
                epgProgress = 0.65f,
                isFavorite = true,
                encryptedToken = CryptoManager.createStreamToken("trt1"),
                bitrateMbps = 6.2f,
                sortOrder = 1
            ),
            ChannelEntity(
                id = "atv",
                name = "ATV HD",
                category = "Ulusal",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Atv_logo_2011.png/320px-Atv_logo_2011.png",
                resolution = "1080p FHD",
                isLive = true,
                isActive = true,
                currentProgram = "Müge Anlı ile Tatlı Sert",
                nextProgram = "atv Gün Ortası",
                epgProgress = 0.40f,
                isFavorite = true,
                encryptedToken = CryptoManager.createStreamToken("atv"),
                bitrateMbps = 5.8f,
                sortOrder = 2
            ),
            ChannelEntity(
                id = "trthaber",
                name = "TRT Haber",
                category = "Haber",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/TRT_Haber_logo_2020.png/320px-TRT_Haber_logo_2020.png",
                resolution = "1080p FHD",
                isLive = true,
                isActive = true,
                currentProgram = "Öğle Bülteni & Canlı Bağlantılar",
                nextProgram = "Dünya Gündemi",
                epgProgress = 0.80f,
                isFavorite = false,
                encryptedToken = CryptoManager.createStreamToken("trthaber"),
                bitrateMbps = 4.5f,
                sortOrder = 3
            ),
            ChannelEntity(
                id = "trtspor",
                name = "TRT Spor HD",
                category = "Spor",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/TRT_Spor_logo_2021.png/320px-TRT_Spor_logo_2021.png",
                resolution = "1080p 60fps",
                isLive = true,
                isActive = true,
                currentProgram = "Spor Stüdyosu - Canlı Maç Analizi",
                nextProgram = "Futbol Aklı",
                epgProgress = 0.30f,
                isFavorite = true,
                encryptedToken = CryptoManager.createStreamToken("trtspor"),
                bitrateMbps = 8.0f,
                sortOrder = 4
            ),
            ChannelEntity(
                id = "trtbelgesel",
                name = "TRT Belgesel HD",
                category = "Belgesel",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/TRT_Belgesel_logo_2020.png/320px-TRT_Belgesel_logo_2020.png",
                resolution = "4K UHD",
                isLive = true,
                isActive = true,
                currentProgram = "Yaban Hayatın Gizemi: Anadolu",
                nextProgram = "Usta Eller Belgeseli",
                epgProgress = 0.55f,
                isFavorite = false,
                encryptedToken = CryptoManager.createStreamToken("trtbelgesel"),
                bitrateMbps = 9.4f,
                sortOrder = 5
            ),
            ChannelEntity(
                id = "sinematv",
                name = "Sinema TV Aksiyon",
                category = "Sinema",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                logoUrl = "",
                resolution = "1080p FHD",
                isLive = true,
                isActive = true,
                currentProgram = "Görevimiz Tehlike: Yansımalar",
                nextProgram = "Galaksinin Koruyucuları",
                epgProgress = 0.72f,
                isFavorite = true,
                encryptedToken = CryptoManager.createStreamToken("sinematv"),
                bitrateMbps = 6.8f,
                sortOrder = 6
            ),
            ChannelEntity(
                id = "trt_cocuk",
                name = "TRT Çocuk",
                category = "Çocuk",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/TRT_%C3%87ocuk_logo_2021.svg/320px-TRT_%C3%87ocuk_logo_2021.svg.png",
                resolution = "1080p HD",
                isLive = true,
                isActive = true,
                currentProgram = "Rafadan Tayfa - Göbeklitepe",
                nextProgram = "İbi ve Tosi Maceraları",
                epgProgress = 0.20f,
                isFavorite = false,
                encryptedToken = CryptoManager.createStreamToken("trt_cocuk"),
                bitrateMbps = 4.2f,
                sortOrder = 7
            ),
            ChannelEntity(
                id = "powertv",
                name = "Power TV HD",
                category = "Müzik",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4",
                logoUrl = "",
                resolution = "1080p HD",
                isLive = true,
                isActive = true,
                currentProgram = "Top 40 Global Hits - Canlı Konser",
                nextProgram = "Akustik Akşamlar",
                epgProgress = 0.90f,
                isFavorite = false,
                encryptedToken = CryptoManager.createStreamToken("powertv"),
                bitrateMbps = 5.0f,
                sortOrder = 8
            ),
            ChannelEntity(
                id = "kanald",
                name = "Kanal D HD",
                category = "Ulusal",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackSeeTheWorld.mp4",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8a/Kanal_D_logo_2011.png/320px-Kanal_D_logo_2011.png",
                resolution = "1080p FHD",
                isLive = true,
                isActive = true,
                currentProgram = "İnci Taneleri - Canlı Yayın",
                nextProgram = "Kanal D Ana Haber",
                epgProgress = 0.50f,
                isFavorite = false,
                encryptedToken = CryptoManager.createStreamToken("kanald"),
                bitrateMbps = 6.0f,
                sortOrder = 9
            ),
            ChannelEntity(
                id = "aspor",
                name = "A Spor HD",
                category = "Spor",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4",
                logoUrl = "",
                resolution = "1080p 60fps",
                isLive = true,
                isActive = true,
                currentProgram = "Son Sayfa - Canlı Skorlar & Özetler",
                nextProgram = "Artı Futbol",
                epgProgress = 0.35f,
                isFavorite = false,
                encryptedToken = CryptoManager.createStreamToken("aspor"),
                bitrateMbps = 7.5f,
                sortOrder = 10
            ),
            ChannelEntity(
                id = "showtv",
                name = "Show TV HD",
                category = "Ulusal",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                logoUrl = "",
                resolution = "1080p FHD",
                isLive = true,
                isActive = true,
                currentProgram = "Kızılcık Şerbeti",
                nextProgram = "Show Ana Haber",
                epgProgress = 0.60f,
                isFavorite = false,
                encryptedToken = CryptoManager.createStreamToken("showtv"),
                bitrateMbps = 5.9f,
                sortOrder = 11
            ),
            ChannelEntity(
                id = "tv8",
                name = "TV8 HD",
                category = "Eğlence",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                logoUrl = "",
                resolution = "1080p FHD",
                isLive = true,
                isActive = true,
                currentProgram = "MasterChef Türkiye - Canlı Eleme",
                nextProgram = "Survivor Panorama",
                epgProgress = 0.45f,
                isFavorite = false,
                encryptedToken = CryptoManager.createStreamToken("tv8"),
                bitrateMbps = 6.4f,
                sortOrder = 12
            )
        )
        dao.insertChannels(initialChannels)

        // Seed initial notifications
        val initialNotifications = listOf(
            NotificationEntity(
                title = "🔴 Canlı Yayın Başladı!",
                message = "Süper Lig Dev Derbi mücadelesi canlı yayınla TRT Spor ekranlarında başladı. Kaçırmayın!",
                channelId = "trtspor",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
                isRead = false,
                priority = "HIGH",
                category = "Spor"
            ),
            NotificationEntity(
                title = "🌟 Hoş Geldiniz - Canlı TV v1.0",
                message = "Uçtan uca şifrelemeli, yüksek başarımlı önbellek ve gelişmiş canlı video oynatıcınız hazır!",
                channelId = null,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                isRead = false,
                priority = "NORMAL",
                category = "Sistem"
            ),
            NotificationEntity(
                title = "⚡ Yeni Bölüm Yayında",
                message = "Gönül Dağı bu akşam sürpriz gelişmelerle TRT 1'de yayında.",
                channelId = "trt1",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 5,
                isRead = true,
                priority = "NORMAL",
                category = "Dizi"
            )
        )
        initialNotifications.forEach { dao.insertNotification(it) }

        // Seed watch history
        val initialHistory = listOf(
            HistoryEntity(
                channelId = "trt1",
                channelName = "TRT 1 HD",
                category = "Ulusal",
                resolution = "1080p FHD",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 35,
                durationMinutes = 45
            ),
            HistoryEntity(
                channelId = "trtspor",
                channelName = "TRT Spor HD",
                category = "Spor",
                resolution = "1080p 60fps",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 180,
                durationMinutes = 90
            )
        )
        initialHistory.forEach { dao.insertWatchHistory(it) }

        // Seed initial live community chat messages
        val initialChat = listOf(
            ChatMessageEntity(
                channelId = "all",
                channelName = "Genel Sohbet",
                senderName = "Sistem Moderatörü",
                senderRole = "Moderatör",
                avatarColorHex = 0xFF00E676,
                message = "🎉 Canlı TV Topluluk Odasına hoş geldiniz! Canlı yayınları izlerken yorum yapabilir, maç ve dizi heyecanını paylaşabilirsiniz.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                likesCount = 28,
                isPinned = true
            ),
            ChatMessageEntity(
                channelId = "all",
                channelName = "Genel Sohbet",
                senderName = "Ahmet Yılmaz",
                senderRole = "VIP",
                avatarColorHex = 0xFFFFD600,
                message = "Yayın akışı ve ses kalitesi gerçekten çok akıcı olmuş, emeği geçenlere tebrikler! 👏",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 18,
                likesCount = 14
            ),
            ChatMessageEntity(
                channelId = "all",
                channelName = "Genel Sohbet",
                senderName = "Mert Demir",
                senderRole = "İzleyici",
                avatarColorHex = 0xFF00B0FF,
                message = "Gönül Dağı bu akşam çok heyecanlı başladı! 📺🔥",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 8,
                likesCount = 9
            ),
            ChatMessageEntity(
                channelId = "all",
                channelName = "Genel Sohbet",
                senderName = "Zeynep Kaya",
                senderRole = "İzleyici",
                avatarColorHex = 0xFFFF4081,
                message = "TRT Belgesel'de vahşi yaşam programı var, görüntü kalitesi 1080p harika görünüyor ✨",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 3,
                likesCount = 6
            ),
            ChatMessageEntity(
                channelId = "trtspor",
                channelName = "TRT Spor Maç Odası",
                senderName = "Can Polat",
                senderRole = "VIP",
                avatarColorHex = 0xFF7C4DFF,
                message = "Haftanın maç özetleri ve canlı istatistikler mükemmel! ⚽🔥",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 12,
                likesCount = 19
            )
        )
        dao.insertChatMessages(initialChat)

        // Seed initial live community poll
        val initialPolls = listOf(
            CommunityPollEntity(
                id = "poll_live_rating",
                channelId = "all",
                question = "Bu akşam hangi kategoriyi daha çok izliyorsunuz?",
                option1 = "⚽ Canlı Spor & Maç",
                votes1 = 142,
                option2 = "🎬 Dizi & Sinema Kuşağı",
                votes2 = 189,
                option3 = "📰 Güncel Haber & Belgesel",
                votes3 = 86,
                isActive = true
            )
        )
        dao.insertPolls(initialPolls)
    }

    private fun ChannelEntity.toModel(): Channel {
        return Channel(
            id = id,
            name = name,
            category = category,
            streamUrl = streamUrl,
            logoUrl = logoUrl,
            resolution = resolution,
            isLive = isLive,
            isActive = isActive,
            currentProgram = currentProgram,
            nextProgram = nextProgram,
            epgProgress = epgProgress,
            isFavorite = isFavorite,
            encryptedToken = encryptedToken.ifEmpty { CryptoManager.createStreamToken(id) },
            bitrateMbps = bitrateMbps
        )
    }

    private fun Channel.toEntity(): ChannelEntity {
        return ChannelEntity(
            id = id,
            name = name,
            category = category,
            streamUrl = streamUrl,
            logoUrl = logoUrl,
            resolution = resolution,
            isLive = isLive,
            isActive = isActive,
            currentProgram = currentProgram,
            nextProgram = nextProgram,
            epgProgress = epgProgress,
            isFavorite = isFavorite,
            encryptedToken = encryptedToken,
            bitrateMbps = bitrateMbps
        )
    }

    private fun HistoryEntity.toModel(): WatchHistory {
        return WatchHistory(
            id = id,
            channelId = channelId,
            channelName = channelName,
            category = category,
            resolution = resolution,
            timestamp = timestamp,
            durationMinutes = durationMinutes
        )
    }

    private fun NotificationEntity.toModel(): AppNotification {
        val prio = try {
            NotificationPriority.valueOf(priority)
        } catch (e: Exception) {
            NotificationPriority.NORMAL
        }
        return AppNotification(
            id = id,
            title = title,
            message = message,
            channelId = channelId,
            timestamp = timestamp,
            isRead = isRead,
            priority = prio,
            category = category
        )
    }

    private fun ChatMessageEntity.toModel(): ChatMessage {
        return ChatMessage(
            id = id,
            channelId = channelId,
            channelName = channelName,
            senderName = senderName,
            senderRole = senderRole,
            avatarColorHex = avatarColorHex,
            message = message,
            timestamp = timestamp,
            likesCount = likesCount,
            isLikedByUser = isLikedByUser,
            isUserMessage = isUserMessage,
            isPinned = isPinned
        )
    }

    private fun ChatMessage.toEntity(): ChatMessageEntity {
        return ChatMessageEntity(
            id = id,
            channelId = channelId,
            channelName = channelName,
            senderName = senderName,
            senderRole = senderRole,
            avatarColorHex = avatarColorHex,
            message = message,
            timestamp = timestamp,
            likesCount = likesCount,
            isLikedByUser = isLikedByUser,
            isUserMessage = isUserMessage,
            isPinned = isPinned
        )
    }

    private fun CommunityPollEntity.toModel(): CommunityPoll {
        return CommunityPoll(
            id = id,
            channelId = channelId,
            question = question,
            option1 = option1,
            votes1 = votes1,
            option2 = option2,
            votes2 = votes2,
            option3 = option3,
            votes3 = votes3,
            userVotedOption = userVotedOption,
            isActive = isActive
        )
    }
}
