package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppNotification
import com.example.data.model.Channel
import com.example.data.model.ChannelSortOrder
import com.example.ui.components.CategoryChipsRow
import com.example.ui.components.ChannelCard
import com.example.ui.components.InAppBroadcastBanner
import com.example.ui.components.TvPlayerView
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.TvAmber
import com.example.ui.theme.TvEmerald
import com.example.ui.theme.TvPurple
import com.example.ui.theme.TvSkyBlue

@Composable
fun HomeScreen(
    currentPlayingChannel: Channel?,
    channels: List<Channel>,
    filteredChannels: List<Channel>,
    selectedCategory: String,
    isPlaying: Boolean,
    isMuted: Boolean,
    volume: Float,
    brightness: Float,
    isPlayerFullscreen: Boolean,
    aspectRatioMode: String,
    isGridView: Boolean,
    broadcastBanner: AppNotification?,
    strings: AppStrings,
    selectedTab: Int,
    streamQuality: String = "1080p Full HD",
    sleepTimerRemainingSeconds: Int? = null,
    scheduledReminders: Set<String> = emptySet(),
    isScreenLocked: Boolean = false,
    isRecording: Boolean = false,
    recordingSeconds: Int = 0,
    showDiagnostics: Boolean = false,
    showZapBar: Boolean = false,
    onCategorySelected: (String) -> Unit,
    onSelectChannel: (Channel) -> Unit,
    onToggleFavorite: (Channel) -> Unit,
    onTogglePlay: () -> Unit,
    onToggleMute: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleFullscreen: () -> Unit,
    onToggleAspectRatio: () -> Unit,
    onDismissBanner: () -> Unit,
    onOpenQualityDialog: () -> Unit = {},
    onOpenSleepTimerDialog: () -> Unit = {},
    onToggleReminder: (Channel) -> Unit = {},
    onShareChannel: (Channel) -> Unit = {},
    onEnterPiP: () -> Unit = {},
    onToggleScreenLock: () -> Unit = {},
    onToggleRecording: () -> Unit = {},
    onOpenAudioSubtitleDialog: () -> Unit = {},
    onToggleDiagnostics: () -> Unit = {},
    onToggleZapBar: () -> Unit = {},
    channelSortOrder: ChannelSortOrder = ChannelSortOrder.POPULAR,
    onSortOrderChange: (ChannelSortOrder) -> Unit = {},
    onOpenChat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isReminderSetForCurrent = currentPlayingChannel != null && scheduledReminders.contains(currentPlayingChannel.id)

    if (isPlayerFullscreen) {
        // Fullscreen player view fills entire viewport
        TvPlayerView(
            channel = currentPlayingChannel,
            channels = channels,
            isPlaying = isPlaying,
            isMuted = isMuted,
            volume = volume,
            brightness = brightness,
            isFullscreen = true,
            aspectRatioMode = aspectRatioMode,
            strings = strings,
            quality = streamQuality,
            sleepTimerRemainingSeconds = sleepTimerRemainingSeconds,
            isReminderSet = isReminderSetForCurrent,
            isScreenLocked = isScreenLocked,
            isRecording = isRecording,
            recordingSeconds = recordingSeconds,
            showDiagnostics = showDiagnostics,
            showZapBar = showZapBar,
            onTogglePlay = onTogglePlay,
            onToggleMute = onToggleMute,
            onVolumeChange = onVolumeChange,
            onToggleFullscreen = onToggleFullscreen,
            onToggleAspectRatio = onToggleAspectRatio,
            onToggleFavorite = onToggleFavorite,
            onSelectChannel = onSelectChannel,
            onOpenQualityDialog = onOpenQualityDialog,
            onOpenSleepTimerDialog = onOpenSleepTimerDialog,
            onToggleReminder = { currentPlayingChannel?.let { onToggleReminder(it) } },
            onShareChannel = { currentPlayingChannel?.let { onShareChannel(it) } },
            onEnterPiP = onEnterPiP,
            onToggleScreenLock = onToggleScreenLock,
            onToggleRecording = onToggleRecording,
            onOpenAudioSubtitleDialog = onOpenAudioSubtitleDialog,
            onToggleDiagnostics = onToggleDiagnostics,
            onToggleZapBar = onToggleZapBar,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .testTag("home_screen")
        ) {
            // Live Video Player at top
            TvPlayerView(
                channel = currentPlayingChannel,
                channels = channels,
                isPlaying = isPlaying,
                isMuted = isMuted,
                volume = volume,
                brightness = brightness,
                isFullscreen = false,
                aspectRatioMode = aspectRatioMode,
                strings = strings,
                quality = streamQuality,
                sleepTimerRemainingSeconds = sleepTimerRemainingSeconds,
                isReminderSet = isReminderSetForCurrent,
                isScreenLocked = isScreenLocked,
                isRecording = isRecording,
                recordingSeconds = recordingSeconds,
                showDiagnostics = showDiagnostics,
                showZapBar = showZapBar,
                onTogglePlay = onTogglePlay,
                onToggleMute = onToggleMute,
                onVolumeChange = onVolumeChange,
                onToggleFullscreen = onToggleFullscreen,
                onToggleAspectRatio = onToggleAspectRatio,
                onToggleFavorite = onToggleFavorite,
                onSelectChannel = onSelectChannel,
                onOpenQualityDialog = onOpenQualityDialog,
                onOpenSleepTimerDialog = onOpenSleepTimerDialog,
                onToggleReminder = { currentPlayingChannel?.let { onToggleReminder(it) } },
                onShareChannel = { currentPlayingChannel?.let { onShareChannel(it) } },
                onEnterPiP = onEnterPiP,
                onToggleScreenLock = onToggleScreenLock,
                onToggleRecording = onToggleRecording,
                onOpenAudioSubtitleDialog = onOpenAudioSubtitleDialog,
                onToggleDiagnostics = onToggleDiagnostics,
                onToggleZapBar = onToggleZapBar,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // In-App Instant Broadcast Notification Banner (Floating alert)
            InAppBroadcastBanner(
                notification = broadcastBanner,
                strings = strings,
                onDismiss = onDismissBanner,
                onWatchChannel = { channelId ->
                    channels.find { it.id == channelId }?.let { onSelectChannel(it) }
                }
            )

            // Category Chips Row (hidden if in Favorites tab)
            if (selectedTab != 1) {
                CategoryChipsRow(
                    selectedCategory = selectedCategory,
                    strings = strings,
                    onCategorySelected = onCategorySelected,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = TvAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.favorites,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Quick Sort Order Chips & Community Chat Shortcut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(ChannelSortOrder.values()) { order ->
                        val isSelected = order == channelSortOrder
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) TvSkyBlue.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) TvSkyBlue else Color.White.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.clickable { onSortOrderChange(order) }
                        ) {
                            Text(
                                text = order.titleTr,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TvSkyBlue else Color.White.copy(alpha = 0.75f),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Chat Shortcut Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = TvEmerald.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TvEmerald.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .clickable { onOpenChat() }
                        .testTag("home_open_chat_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = strings.liveChat,
                            tint = TvEmerald,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = strings.liveChat,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TvEmerald
                        )
                    }
                }
            }

            // Channel Count & Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredChannels.size} ${strings.channelsCount}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = strings.secureStream,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TvSkyBlue
                )
            }

            // Channels List or Grid
            if (filteredChannels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Default.Favorite else Icons.Default.Tv,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (selectedTab == 1) strings.noFavorites else strings.searchPlaceholder,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (selectedTab == 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = strings.addFavoriteHint,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredChannels) { ch ->
                        ChannelCard(
                            channel = ch,
                            isSelected = ch.id == currentPlayingChannel?.id,
                            isGridView = true,
                            onSelect = { onSelectChannel(ch) },
                            onToggleFavorite = { onToggleFavorite(ch) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredChannels) { ch ->
                        ChannelCard(
                            channel = ch,
                            isSelected = ch.id == currentPlayingChannel?.id,
                            isGridView = false,
                            onSelect = { onSelectChannel(ch) },
                            onToggleFavorite = { onToggleFavorite(ch) }
                        )
                    }
                }
            }
        }
    }
}
