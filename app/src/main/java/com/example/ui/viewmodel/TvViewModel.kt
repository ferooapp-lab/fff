package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cache.CacheMetrics
import com.example.data.db.AppDatabase
import com.example.data.model.AppNotification
import com.example.data.model.AppUpdateInfo
import com.example.data.model.Channel
import com.example.data.model.ChannelSortOrder
import com.example.data.model.ChatMessage
import com.example.data.model.CommunityPoll
import com.example.data.model.DvrRecording
import com.example.data.model.NotificationPriority
import com.example.data.model.ProjectReport
import com.example.data.model.SoundPreset
import com.example.data.model.WatchHistory
import com.example.data.repository.TvRepository
import com.example.data.security.CryptoManager
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.LocalizedStrings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TvViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = TvRepository(database)

    // UI state
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentPlayingChannel = MutableStateFlow<Channel?>(null)
    val currentPlayingChannel: StateFlow<Channel?> = _currentPlayingChannel.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _volume = MutableStateFlow(0.85f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _brightness = MutableStateFlow(0.9f)
    val brightness: StateFlow<Float> = _brightness.asStateFlow()

    private val _isPlayerFullscreen = MutableStateFlow(false)
    val isPlayerFullscreen: StateFlow<Boolean> = _isPlayerFullscreen.asStateFlow()

    private val _aspectRatioMode = MutableStateFlow("16:9")
    val aspectRatioMode: StateFlow<String> = _aspectRatioMode.asStateFlow()

    private val _isGridView = MutableStateFlow(false)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    private val _themeMode = MutableStateFlow("DARK") // DARK, LIGHT, SYSTEM
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _isDynamicColorEnabled = MutableStateFlow(true)
    val isDynamicColorEnabled: StateFlow<Boolean> = _isDynamicColorEnabled.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.TURKISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    val strings: StateFlow<AppStrings> = _currentLanguage.combine(_currentLanguage) { lang, _ ->
        LocalizedStrings.get(lang)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, LocalizedStrings.get(AppLanguage.TURKISH))

    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    private val _inAppBroadcastBanner = MutableStateFlow<AppNotification?>(null)
    val inAppBroadcastBanner: StateFlow<AppNotification?> = _inAppBroadcastBanner.asStateFlow()

    private val _cacheMetrics = MutableStateFlow(repository.cacheManager.getMetrics())
    val cacheMetrics: StateFlow<CacheMetrics> = _cacheMetrics.asStateFlow()

    val projectReport = MutableStateFlow(ProjectReport()).asStateFlow()

    // App Update State
    private val _updateInfo = MutableStateFlow(AppUpdateInfo())
    val updateInfo: StateFlow<AppUpdateInfo> = _updateInfo.asStateFlow()

    private val _isCheckingUpdate = MutableStateFlow(false)
    val isCheckingUpdate: StateFlow<Boolean> = _isCheckingUpdate.asStateFlow()

    private val _updateDownloadProgress = MutableStateFlow<Float?>(null)
    val updateDownloadProgress: StateFlow<Float?> = _updateDownloadProgress.asStateFlow()

    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog: StateFlow<Boolean> = _showUpdateDialog.asStateFlow()

    // Sleep Timer State
    private var sleepTimerJob: Job? = null
    private val _sleepTimerRemainingSeconds = MutableStateFlow<Int?>(null)
    val sleepTimerRemainingSeconds: StateFlow<Int?> = _sleepTimerRemainingSeconds.asStateFlow()

    // Stream Quality State
    private val _streamQuality = MutableStateFlow("1080p FHD")
    val streamQuality: StateFlow<String> = _streamQuality.asStateFlow()

    // Program Reminders State (channel IDs)
    private val _scheduledReminders = MutableStateFlow<Set<String>>(emptySet())
    val scheduledReminders: StateFlow<Set<String>> = _scheduledReminders.asStateFlow()

    // Cloud DVR State
    private val _dvrRecordings = MutableStateFlow<List<DvrRecording>>(
        listOf(
            DvrRecording(
                id = "rec-1",
                channelId = "ch_trthaber",
                channelName = "TRT Haber",
                programTitle = "Dünya Gündemi ve Analiz",
                category = "Haber",
                recordedDate = "Bugün, 14:30",
                durationText = "42 dk",
                sizeMb = 385,
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
            DvrRecording(
                id = "rec-2",
                channelId = "ch_trtspor",
                channelName = "TRT Spor",
                programTitle = "Süper Lig Maç Önü & Analiz",
                category = "Spor",
                recordedDate = "Dün, 20:00",
                durationText = "65 dk",
                sizeMb = 640,
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            )
        )
    )
    val dvrRecordings: StateFlow<List<DvrRecording>> = _dvrRecordings.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()
    private var recordingJob: Job? = null

    // Audio Track & Subtitles
    private val _selectedAudioTrack = MutableStateFlow("Türkçe (Orijinal)")
    val selectedAudioTrack: StateFlow<String> = _selectedAudioTrack.asStateFlow()

    private val _selectedSubtitle = MutableStateFlow("Kapalı")
    val selectedSubtitle: StateFlow<String> = _selectedSubtitle.asStateFlow()

    // Screen Lock
    private val _isScreenLocked = MutableStateFlow(false)
    val isScreenLocked: StateFlow<Boolean> = _isScreenLocked.asStateFlow()

    // Diagnostics / Stats for Nerds
    private val _showDiagnostics = MutableStateFlow(false)
    val showDiagnostics: StateFlow<Boolean> = _showDiagnostics.asStateFlow()

    // Quick Zap Bar
    private val _showZapBar = MutableStateFlow(false)
    val showZapBar: StateFlow<Boolean> = _showZapBar.asStateFlow()

    // Audio Equalizer & Sound FX Mode
    private val _selectedSoundPreset = MutableStateFlow(SoundPreset.STANDARD)
    val selectedSoundPreset: StateFlow<SoundPreset> = _selectedSoundPreset.asStateFlow()

    // Channel Sort Order
    private val _channelSortOrder = MutableStateFlow(ChannelSortOrder.POPULAR)
    val channelSortOrder: StateFlow<ChannelSortOrder> = _channelSortOrder.asStateFlow()

    // Community Live Chat & Polls
    private val _chatChannelFilter = MutableStateFlow<String>("all")
    val chatChannelFilter: StateFlow<String> = _chatChannelFilter.asStateFlow()

    val chatMessages: StateFlow<List<ChatMessage>> = _chatChannelFilter
        .flatMapLatest { filter -> repository.getChatMessages(filter) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val communityPolls: StateFlow<List<CommunityPoll>> = _chatChannelFilter
        .flatMapLatest { filter -> repository.getPolls(filter) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Database flows
    val allChannels = repository.allChannels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeChannels = repository.activeChannels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteChannels = repository.favoriteChannels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val watchHistory = repository.watchHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val notifications = repository.notifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val unreadCount = repository.unreadNotificationsCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Filtered channels combining query, category, tab, and sorting
    val filteredChannels: StateFlow<List<Channel>> = combine(
        activeChannels,
        _selectedCategory,
        _searchQuery,
        _selectedTab,
        _channelSortOrder
    ) { channels, category, query, tab, sortOrder ->
        var list = when (tab) {
            1 -> channels.filter { it.isFavorite }
            else -> channels
        }

        if (category != "all" && category != "Tümü" && tab != 1) {
            list = when (category.lowercase()) {
                "trend", "trendler" -> list.filter { it.isTrending || it.bitrateMbps >= 5.5f }
                "4k", "4k ultra hd" -> list.filter { it.is4K || it.resolution.contains("4K") }
                else -> list.filter { it.category.equals(category, ignoreCase = true) }
            }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                        it.category.lowercase().contains(q) ||
                        it.currentProgram.lowercase().contains(q)
            }
        }

        when (sortOrder) {
            ChannelSortOrder.POPULAR, ChannelSortOrder.VIEWERS -> list.sortedByDescending { it.viewerCount }
            ChannelSortOrder.NAME -> list.sortedBy { it.name }
            ChannelSortOrder.QUALITY -> list.sortedByDescending { if (it.is4K || it.resolution.contains("4K")) 2 else 1 }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recently watched channels for quick zap carousel
    val recentlyWatchedChannels: StateFlow<List<Channel>> = combine(
        activeChannels,
        watchHistory
    ) { channels, history ->
        val channelMap = channels.associateBy { it.id }
        history.mapNotNull { channelMap[it.channelId] }.distinctBy { it.id }.take(10)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Auto-select first channel when channels load
        viewModelScope.launch {
            activeChannels.collect { channels ->
                if (_currentPlayingChannel.value == null && channels.isNotEmpty()) {
                    _currentPlayingChannel.value = channels.first()
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectChannel(channel: Channel) {
        _currentPlayingChannel.value = channel
        _isPlaying.value = true
        // Record watch history
        viewModelScope.launch {
            repository.recordWatchHistory(channel)
            refreshCacheStats()
        }
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun setVolume(vol: Float) {
        _volume.value = vol.coerceIn(0f, 1f)
        if (_volume.value > 0f) _isMuted.value = false
    }

    fun setBrightness(b: Float) {
        _brightness.value = b.coerceIn(0.1f, 1f)
    }

    fun toggleFullscreen() {
        _isPlayerFullscreen.value = !_isPlayerFullscreen.value
    }

    fun toggleAspectRatio() {
        _aspectRatioMode.value = when (_aspectRatioMode.value) {
            "16:9" -> "FILL"
            "FILL" -> "FIT"
            else -> "16:9"
        }
    }

    fun toggleGridView() {
        _isGridView.value = !_isGridView.value
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    fun toggleDynamicColor(enabled: Boolean) {
        _isDynamicColorEnabled.value = enabled
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun setChatChannelFilter(channelId: String) {
        _chatChannelFilter.value = channelId
    }

    fun sendChatMessage(messageText: String, senderName: String = "Ben (İzleyici)") {
        if (messageText.isBlank()) return
        val currentChannel = _currentPlayingChannel.value
        val channelId = if (_chatChannelFilter.value != "all") _chatChannelFilter.value else (currentChannel?.id ?: "all")
        val channelName = if (channelId == "all") "Genel Sohbet" else (currentChannel?.name ?: "Canlı Yayın Odası")
        val newMsg = ChatMessage(
            channelId = channelId,
            channelName = channelName,
            senderName = senderName.ifBlank { "Ben (İzleyici)" },
            senderRole = "İzleyici",
            avatarColorHex = 0xFF00E5FF,
            message = messageText.trim(),
            timestamp = System.currentTimeMillis(),
            isUserMessage = true
        )
        viewModelScope.launch {
            repository.sendChatMessage(newMsg)
        }
    }

    fun likeChatMessage(id: Long) {
        viewModelScope.launch {
            repository.likeChatMessage(id)
        }
    }

    fun votePoll(pollId: String, optionIndex: Int) {
        viewModelScope.launch {
            repository.votePoll(pollId, optionIndex)
        }
    }

    fun setSoundPreset(preset: SoundPreset) {
        _selectedSoundPreset.value = preset
    }

    fun setChannelSortOrder(order: ChannelSortOrder) {
        _channelSortOrder.value = order
    }

    fun toggleFavorite(channel: Channel) {
        viewModelScope.launch {
            repository.toggleFavorite(channel.id, channel.isFavorite)
            // If currently playing channel is this one, update it
            if (_currentPlayingChannel.value?.id == channel.id) {
                _currentPlayingChannel.value = _currentPlayingChannel.value?.copy(isFavorite = !channel.isFavorite)
            }
        }
    }

    fun toggleChannelActive(channel: Channel) {
        viewModelScope.launch {
            repository.toggleActive(channel.id, channel.isActive)
        }
    }

    fun saveChannel(channel: Channel, isNew: Boolean) {
        viewModelScope.launch {
            if (isNew) {
                repository.addChannel(channel)
            } else {
                repository.updateChannel(channel)
            }
            refreshCacheStats()
        }
    }

    fun deleteChannel(channelId: String) {
        viewModelScope.launch {
            repository.deleteChannel(channelId)
            if (_currentPlayingChannel.value?.id == channelId) {
                _currentPlayingChannel.value = activeChannels.value.firstOrNull { it.id != channelId }
            }
            refreshCacheStats()
        }
    }

    fun sendBroadcastNotification(
        title: String,
        message: String,
        channelId: String?,
        priority: NotificationPriority,
        category: String
    ) {
        viewModelScope.launch {
            repository.sendBroadcastNotification(title, message, channelId, priority, category)
            // Trigger instant in-app banner for feedback
            val banner = AppNotification(
                title = title,
                message = message,
                channelId = channelId,
                timestamp = System.currentTimeMillis(),
                priority = priority,
                category = category
            )
            _inAppBroadcastBanner.value = banner
        }
    }

    fun dismissBroadcastBanner() {
        _inAppBroadcastBanner.value = null
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearNotifications()
        }
    }

    fun clearWatchHistory() {
        viewModelScope.launch {
            repository.clearWatchHistory()
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun flushCache() {
        repository.flushCache()
        refreshCacheStats()
    }

    fun refreshCacheStats() {
        _cacheMetrics.value = repository.cacheManager.getMetrics()
    }

    fun unlockAdmin(pin: String): Boolean {
        return if (pin.trim() == "1234" || pin.trim() == "0000") {
            _isAdminUnlocked.value = true
            true
        } else {
            false
        }
    }

    fun lockAdmin() {
        _isAdminUnlocked.value = false
    }

    // App Update actions
    fun setUpdateDialogVisible(visible: Boolean) {
        _showUpdateDialog.value = visible
    }

    fun checkForUpdates(manual: Boolean = true) {
        viewModelScope.launch {
            _isCheckingUpdate.value = true
            delay(1200) // Simulated network check
            _isCheckingUpdate.value = false
            if (manual) {
                _showUpdateDialog.value = true
            }
        }
    }

    fun startSimulatedUpdateDownload(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _updateDownloadProgress.value = 0.05f
            for (step in 1..10) {
                delay(200)
                _updateDownloadProgress.value = step * 0.10f
            }
            delay(300)
            _updateDownloadProgress.value = null
            _updateInfo.value = _updateInfo.value.copy(
                currentVersion = _updateInfo.value.latestVersion,
                isUpdateAvailable = false
            )
            onComplete()
        }
    }

    fun publishUpdateFromAdmin(version: String, notes: String, isMandatory: Boolean) {
        val notesList = notes.lines().filter { it.isNotBlank() }
        _updateInfo.value = _updateInfo.value.copy(
            latestVersion = version,
            isUpdateAvailable = true,
            isMandatory = isMandatory,
            changelog = if (notesList.isNotEmpty()) notesList else listOf(notes)
        )
        // Also broadcast notification to users
        sendBroadcastNotification(
            title = "🚀 Yeni Güncelleme: v$version",
            message = "Canlı TV v$version yayında! Yeni özellikleri kullanmak için uygulamayı güncelleyin.",
            channelId = null,
            priority = if (isMandatory) NotificationPriority.BREAKING else NotificationPriority.HIGH,
            category = "Sistem"
        )
    }

    // Sleep Timer actions
    fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes <= 0) {
            _sleepTimerRemainingSeconds.value = null
            return
        }
        val totalSeconds = minutes * 60
        _sleepTimerRemainingSeconds.value = totalSeconds

        sleepTimerJob = viewModelScope.launch {
            var remaining = totalSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _sleepTimerRemainingSeconds.value = remaining
            }
            _isPlaying.value = false
            _sleepTimerRemainingSeconds.value = null
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        _sleepTimerRemainingSeconds.value = null
    }

    // Stream Quality actions
    fun setStreamQuality(quality: String) {
        _streamQuality.value = quality
    }

    // Reminder actions
    fun toggleProgramReminder(channel: Channel): Boolean {
        val current = _scheduledReminders.value.toMutableSet()
        val isAdded: Boolean
        if (current.contains(channel.id)) {
            current.remove(channel.id)
            isAdded = false
        } else {
            current.add(channel.id)
            isAdded = true
            sendBroadcastNotification(
                title = "⏰ Program Hatırlatıcı Kuruldu: ${channel.name}",
                message = "'${channel.nextProgram}' başladığında canlı yayın uyarısı alacaksınız.",
                channelId = channel.id,
                priority = NotificationPriority.NORMAL,
                category = "Hatırlatıcı"
            )
        }
        _scheduledReminders.value = current
        return isAdded
    }

    // Cloud DVR Actions
    fun toggleRecording(channel: Channel) {
        if (_isRecording.value) {
            _isRecording.value = false
            recordingJob?.cancel()
            val secs = _recordingSeconds.value
            val mins = (secs / 60).coerceAtLeast(1)
            val newRec = DvrRecording(
                id = "rec-${System.currentTimeMillis()}",
                channelId = channel.id,
                channelName = channel.name,
                programTitle = channel.currentProgram,
                category = channel.category,
                recordedDate = "Şimdi",
                durationText = "$mins dk",
                sizeMb = (secs * 0.8f).toInt() + 15,
                streamUrl = channel.streamUrl
            )
            _dvrRecordings.value = listOf(newRec) + _dvrRecordings.value
            _recordingSeconds.value = 0
            sendBroadcastNotification(
                title = "⏺️ Kayıt Kaydedildi: ${channel.name}",
                message = "'${channel.currentProgram}' yayını Bulut DVR kitaplığınıza başarıyla kaydedildi.",
                channelId = channel.id,
                priority = NotificationPriority.NORMAL,
                category = "DVR"
            )
        } else {
            _isRecording.value = true
            _recordingSeconds.value = 0
            recordingJob = viewModelScope.launch {
                while (_isRecording.value) {
                    delay(1000)
                    _recordingSeconds.value += 1
                }
            }
        }
    }

    fun deleteDvrRecording(id: String) {
        _dvrRecordings.value = _dvrRecordings.value.filter { it.id != id }
    }

    // Audio & Subtitle Actions
    fun setAudioTrack(track: String) {
        _selectedAudioTrack.value = track
    }

    fun setSubtitle(subtitle: String) {
        _selectedSubtitle.value = subtitle
    }

    // Screen Lock Action
    fun toggleScreenLock() {
        _isScreenLocked.value = !_isScreenLocked.value
    }

    // Diagnostics / Stats for Nerds Action
    fun toggleDiagnostics() {
        _showDiagnostics.value = !_showDiagnostics.value
    }

    // Quick Zap Bar Action
    fun toggleZapBar() {
        _showZapBar.value = !_showZapBar.value
    }
}
