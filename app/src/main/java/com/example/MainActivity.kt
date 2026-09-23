package com.example

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Channel
import com.example.ui.components.AppUpdateDialog
import com.example.ui.components.AudioEqualizerDialog
import com.example.ui.components.AudioSubtitleDialog
import com.example.ui.components.DvrRecordingsDialog
import com.example.ui.components.NotificationCenterDialog
import com.example.ui.components.SleepTimerDialog
import com.example.ui.components.StreamQualityDialog
import com.example.ui.components.TopAppBarWithActions
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.ChatCommunityScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.WatchHistoryScreen
import com.example.ui.theme.CanliTvTheme
import com.example.ui.theme.TvSkyBlue
import com.example.ui.viewmodel.TvViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TvViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val isDynamicColorEnabled by viewModel.isDynamicColorEnabled.collectAsStateWithLifecycle()
            val isDarkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            CanliTvTheme(
                darkTheme = isDarkTheme,
                dynamicColor = isDynamicColorEnabled
            ) {
                MainAppContent(viewModel = viewModel, isDarkTheme = isDarkTheme)
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: TvViewModel,
    isDarkTheme: Boolean
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentPlayingChannel by viewModel.currentPlayingChannel.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()
    val volume by viewModel.volume.collectAsStateWithLifecycle()
    val brightness by viewModel.brightness.collectAsStateWithLifecycle()
    val isPlayerFullscreen by viewModel.isPlayerFullscreen.collectAsStateWithLifecycle()
    val aspectRatioMode by viewModel.aspectRatioMode.collectAsStateWithLifecycle()
    val isGridView by viewModel.isGridView.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val strings by viewModel.strings.collectAsStateWithLifecycle()
    val broadcastBanner by viewModel.inAppBroadcastBanner.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
    val activeChannels by viewModel.activeChannels.collectAsStateWithLifecycle()
    val allChannels by viewModel.allChannels.collectAsStateWithLifecycle()
    val filteredChannels by viewModel.filteredChannels.collectAsStateWithLifecycle()
    val watchHistory by viewModel.watchHistory.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val cacheMetrics by viewModel.cacheMetrics.collectAsStateWithLifecycle()
    val projectReport by viewModel.projectReport.collectAsStateWithLifecycle()
    val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsStateWithLifecycle()
    val updateInfo by viewModel.updateInfo.collectAsStateWithLifecycle()
    val streamQuality by viewModel.streamQuality.collectAsStateWithLifecycle()
    val sleepTimerRemainingSeconds by viewModel.sleepTimerRemainingSeconds.collectAsStateWithLifecycle()
    val scheduledReminders by viewModel.scheduledReminders.collectAsStateWithLifecycle()
    val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsStateWithLifecycle()
    val updateDownloadProgress by viewModel.updateDownloadProgress.collectAsStateWithLifecycle()
    val isScreenLocked by viewModel.isScreenLocked.collectAsStateWithLifecycle()
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val recordingSeconds by viewModel.recordingSeconds.collectAsStateWithLifecycle()
    val dvrRecordings by viewModel.dvrRecordings.collectAsStateWithLifecycle()
    val selectedAudioTrack by viewModel.selectedAudioTrack.collectAsStateWithLifecycle()
    val selectedSubtitle by viewModel.selectedSubtitle.collectAsStateWithLifecycle()
    val showDiagnostics by viewModel.showDiagnostics.collectAsStateWithLifecycle()
    val showZapBar by viewModel.showZapBar.collectAsStateWithLifecycle()
    val isDynamicColorEnabled by viewModel.isDynamicColorEnabled.collectAsStateWithLifecycle()
    val soundPreset by viewModel.selectedSoundPreset.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val communityPolls by viewModel.communityPolls.collectAsStateWithLifecycle()
    val chatFilter by viewModel.chatChannelFilter.collectAsStateWithLifecycle()
    val channelSortOrder by viewModel.channelSortOrder.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showQualityDialog by remember { mutableStateOf(false) }
    var showAudioSubtitleDialog by remember { mutableStateOf(false) }
    var showDvrDialog by remember { mutableStateOf(false) }
    var showEqualizerDialog by remember { mutableStateOf(false) }

    // Handle back press if fullscreen or on sub-tab
    BackHandler(enabled = isPlayerFullscreen || selectedTab != 0) {
        if (isPlayerFullscreen) {
            viewModel.toggleFullscreen()
        } else {
            viewModel.selectTab(0)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (!isPlayerFullscreen) {
                TopAppBarWithActions(
                    strings = strings,
                    searchQuery = searchQuery,
                    unreadCount = unreadCount,
                    isGridView = isGridView,
                    isDarkMode = isDarkTheme,
                    currentLanguage = currentLanguage,
                    hasUpdateAvailable = updateInfo.isUpdateAvailable,
                    sleepTimerRemainingSeconds = sleepTimerRemainingSeconds,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onToggleGridView = { viewModel.toggleGridView() },
                    onToggleTheme = {
                        viewModel.setThemeMode(if (isDarkTheme) "LIGHT" else "DARK")
                    },
                    onSelectLanguage = { viewModel.setLanguage(it) },
                    onOpenNotifications = { showNotificationsDialog = true },
                    onOpenAdmin = { viewModel.selectTab(4) },
                    onCheckUpdate = {
                        viewModel.checkForUpdates()
                        showUpdateDialog = true
                    },
                    onOpenSleepTimer = { showSleepTimerDialog = true },
                    onOpenRecordings = { showDvrDialog = true },
                    onOpenEqualizer = { showEqualizerDialog = true }
                )
            }
        },
        bottomBar = {
            if (!isPlayerFullscreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = {
                            viewModel.selectTab(0)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = strings.channels,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text(text = strings.channels, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TvSkyBlue,
                            selectedTextColor = TvSkyBlue,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = {
                            viewModel.selectTab(1)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = strings.favorites,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text(text = strings.favorites, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TvSkyBlue,
                            selectedTextColor = TvSkyBlue,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = {
                            viewModel.selectTab(2)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Forum,
                                contentDescription = strings.liveChat,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text(text = strings.liveChat, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TvSkyBlue,
                            selectedTextColor = TvSkyBlue,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                    )

                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = {
                            viewModel.selectTab(3)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = strings.history,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text(text = strings.history, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TvSkyBlue,
                            selectedTextColor = TvSkyBlue,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                    )

                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = {
                            viewModel.selectTab(4)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = strings.admin,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text(text = strings.admin, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TvSkyBlue,
                            selectedTextColor = TvSkyBlue,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isPlayerFullscreen) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
        ) {
            when (selectedTab) {
                0, 1 -> HomeScreen(
                    currentPlayingChannel = currentPlayingChannel,
                    channels = activeChannels,
                    filteredChannels = filteredChannels,
                    selectedCategory = selectedCategory,
                    isPlaying = isPlaying,
                    isMuted = isMuted,
                    volume = volume,
                    brightness = brightness,
                    isPlayerFullscreen = isPlayerFullscreen,
                    aspectRatioMode = aspectRatioMode,
                    isGridView = isGridView,
                    broadcastBanner = broadcastBanner,
                    strings = strings,
                    selectedTab = selectedTab,
                    streamQuality = streamQuality,
                    sleepTimerRemainingSeconds = sleepTimerRemainingSeconds,
                    scheduledReminders = scheduledReminders,
                    isScreenLocked = isScreenLocked,
                    isRecording = isRecording,
                    recordingSeconds = recordingSeconds,
                    showDiagnostics = showDiagnostics,
                    showZapBar = showZapBar,
                    channelSortOrder = channelSortOrder,
                    onSortOrderChange = { viewModel.setChannelSortOrder(it) },
                    onOpenChat = { viewModel.selectTab(2) },
                    onCategorySelected = { viewModel.selectCategory(it) },
                    onSelectChannel = { viewModel.selectChannel(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onTogglePlay = { viewModel.togglePlayPause() },
                    onToggleMute = { viewModel.toggleMute() },
                    onVolumeChange = { viewModel.setVolume(it) },
                    onToggleFullscreen = { viewModel.toggleFullscreen() },
                    onToggleAspectRatio = { viewModel.toggleAspectRatio() },
                    onDismissBanner = { viewModel.dismissBroadcastBanner() },
                    onOpenQualityDialog = { showQualityDialog = true },
                    onOpenSleepTimerDialog = { showSleepTimerDialog = true },
                    onToggleReminder = { ch -> viewModel.toggleProgramReminder(ch) },
                    onToggleScreenLock = { viewModel.toggleScreenLock() },
                    onToggleRecording = { currentPlayingChannel?.let { viewModel.toggleRecording(it) } },
                    onOpenAudioSubtitleDialog = { showAudioSubtitleDialog = true },
                    onToggleDiagnostics = { viewModel.toggleDiagnostics() },
                    onToggleZapBar = { viewModel.toggleZapBar() },
                    onShareChannel = { ch ->
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Canlı TV'de izliyorum: ${ch.name} (${ch.category})\nŞimdi: ${ch.currentProgram}\nAkış: ${ch.streamUrl}"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Kanalı Paylaş"))
                    },
                    onEnterPiP = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            (context as? Activity)?.enterPictureInPictureMode(
                                PictureInPictureParams.Builder()
                                    .setAspectRatio(Rational(16, 9))
                                    .build()
                            )
                        }
                    }
                )

                2 -> ChatCommunityScreen(
                    currentPlayingChannel = currentPlayingChannel,
                    chatMessages = chatMessages,
                    communityPolls = communityPolls,
                    chatFilter = chatFilter,
                    strings = strings,
                    onFilterChange = { viewModel.setChatChannelFilter(it) },
                    onSendMessage = { viewModel.sendChatMessage(it) },
                    onLikeMessage = { viewModel.likeChatMessage(it) },
                    onVotePoll = { pollId, opt -> viewModel.votePoll(pollId, opt) }
                )

                3 -> WatchHistoryScreen(
                    historyList = watchHistory,
                    channels = activeChannels,
                    strings = strings,
                    onSelectChannel = {
                        viewModel.selectChannel(it)
                        viewModel.selectTab(0)
                    },
                    onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
                    onClearHistory = { viewModel.clearWatchHistory() }
                )

                4 -> AdminPanelScreen(
                    channels = allChannels,
                    cacheMetrics = cacheMetrics,
                    projectReport = projectReport,
                    isAdminUnlocked = isAdminUnlocked,
                    strings = strings,
                    updateInfo = updateInfo,
                    onUnlockAdmin = { viewModel.unlockAdmin(it) },
                    onLockAdmin = { viewModel.lockAdmin() },
                    onSaveChannel = { ch, isNew -> viewModel.saveChannel(ch, isNew) },
                    onDeleteChannel = { viewModel.deleteChannel(it) },
                    onToggleActive = { viewModel.toggleChannelActive(it) },
                    onSendNotification = { title, msg, chId, prio, cat ->
                        viewModel.sendBroadcastNotification(title, msg, chId, prio, cat)
                    },
                    onFlushCache = { viewModel.flushCache() },
                    onPublishUpdate = { ver, notes, mandatory ->
                        viewModel.publishUpdateFromAdmin(ver, notes, mandatory)
                    },
                    onCheckUpdate = {
                        viewModel.checkForUpdates()
                        showUpdateDialog = true
                    },
                    isDynamicColorEnabled = isDynamicColorEnabled,
                    onToggleDynamicColor = { viewModel.toggleDynamicColor(it) }
                )
            }
        }

        // Notification Center Dialog
        if (showNotificationsDialog) {
            NotificationCenterDialog(
                notifications = notifications,
                strings = strings,
                onDismiss = { showNotificationsDialog = false },
                onMarkAllRead = { viewModel.markAllNotificationsRead() },
                onMarkRead = { viewModel.markNotificationRead(it) },
                onClearAll = { viewModel.clearNotifications() },
                onSelectChannel = { channelId ->
                    activeChannels.find { it.id == channelId }?.let {
                        viewModel.selectChannel(it)
                        viewModel.selectTab(0)
                    }
                }
            )
        }

        // App Update Dialog
        if (showUpdateDialog) {
            AppUpdateDialog(
                updateInfo = updateInfo,
                isChecking = isCheckingUpdate,
                downloadProgress = updateDownloadProgress,
                strings = strings,
                onDismiss = { showUpdateDialog = false },
                onStartDownload = { viewModel.startSimulatedUpdateDownload() }
            )
        }

        // Sleep Timer Dialog
        if (showSleepTimerDialog) {
            SleepTimerDialog(
                remainingSeconds = sleepTimerRemainingSeconds,
                strings = strings,
                onSelectMinutes = { minutes ->
                    viewModel.setSleepTimer(minutes)
                    showSleepTimerDialog = false
                },
                onCancelTimer = {
                    viewModel.cancelSleepTimer()
                    showSleepTimerDialog = false
                },
                onDismiss = { showSleepTimerDialog = false }
            )
        }

        // Stream Quality Selector Dialog
        if (showQualityDialog) {
            StreamQualityDialog(
                selectedQuality = streamQuality,
                strings = strings,
                onDismiss = { showQualityDialog = false },
                onSelectQuality = { quality ->
                    viewModel.setStreamQuality(quality)
                    showQualityDialog = false
                }
            )
        }

        // Audio and Subtitles Selector Dialog
        if (showAudioSubtitleDialog) {
            AudioSubtitleDialog(
                selectedAudio = selectedAudioTrack,
                selectedSubtitle = selectedSubtitle,
                strings = strings,
                onSelectAudio = {
                    viewModel.setAudioTrack(it)
                },
                onSelectSubtitle = {
                    viewModel.setSubtitle(it)
                },
                onDismiss = { showAudioSubtitleDialog = false }
            )
        }

        // Cloud DVR Recordings Dialog
        if (showDvrDialog) {
            DvrRecordingsDialog(
                recordings = dvrRecordings,
                isRecording = isRecording,
                recordingSeconds = recordingSeconds,
                strings = strings,
                onPlayRecording = { rec ->
                    activeChannels.find { it.id == rec.channelId }?.let {
                        viewModel.selectChannel(it)
                        viewModel.selectTab(0)
                        showDvrDialog = false
                    }
                },
                onDeleteRecording = { recId ->
                    viewModel.deleteDvrRecording(recId)
                },
                onDismiss = { showDvrDialog = false }
            )
        }

        // Audio Equalizer & Sound FX Dialog
        if (showEqualizerDialog) {
            AudioEqualizerDialog(
                selectedPreset = soundPreset,
                strings = strings,
                onPresetSelected = { preset ->
                    viewModel.setSoundPreset(preset)
                },
                onDismiss = { showEqualizerDialog = false }
            )
        }
    }
}
