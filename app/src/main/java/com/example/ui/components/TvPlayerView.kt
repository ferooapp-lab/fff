package com.example.ui.components

import android.media.MediaPlayer
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.Channel
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.TvAmber
import com.example.ui.theme.TvCyan
import com.example.ui.theme.TvEmerald
import com.example.ui.theme.TvLiveRed
import com.example.ui.theme.TvSkyBlue
import kotlinx.coroutines.delay

@Composable
fun TvPlayerView(
    channel: Channel?,
    channels: List<Channel>,
    isPlaying: Boolean,
    isMuted: Boolean,
    volume: Float,
    brightness: Float,
    isFullscreen: Boolean,
    aspectRatioMode: String,
    strings: AppStrings,
    quality: String = "1080p Full HD",
    sleepTimerRemainingSeconds: Int? = null,
    isReminderSet: Boolean = false,
    isScreenLocked: Boolean = false,
    isRecording: Boolean = false,
    recordingSeconds: Int = 0,
    showDiagnostics: Boolean = false,
    showZapBar: Boolean = false,
    onTogglePlay: () -> Unit,
    onToggleMute: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleFullscreen: () -> Unit,
    onToggleAspectRatio: () -> Unit,
    onToggleFavorite: (Channel) -> Unit,
    onSelectChannel: (Channel) -> Unit,
    onOpenQualityDialog: () -> Unit = {},
    onOpenSleepTimerDialog: () -> Unit = {},
    onToggleReminder: () -> Unit = {},
    onShareChannel: () -> Unit = {},
    onEnterPiP: () -> Unit = {},
    onToggleScreenLock: () -> Unit = {},
    onToggleRecording: () -> Unit = {},
    onOpenAudioSubtitleDialog: () -> Unit = {},
    onToggleDiagnostics: () -> Unit = {},
    onToggleZapBar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showControls by remember { mutableStateOf(true) }
    var showVolumeSlider by remember { mutableStateOf(false) }

    // Auto hide controls after 5 seconds
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(5000)
            showControls = false
        }
    }

    // Live pulsing dot animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val liveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "liveAlpha"
    )

    val playerModifier = if (isFullscreen) {
        Modifier.fillMaxSize()
    } else {
        Modifier
            .fillMaxWidth()
            .aspectRatio(if (aspectRatioMode == "16:9") 16f / 9f else 18f / 9f)
    }

    Card(
        modifier = modifier
            .then(playerModifier)
            .testTag("tv_player_view"),
        shape = if (isFullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showControls = !showControls
                }
        ) {
            // Video Player Surface
            if (channel != null) {
                val isLocalUri = remember(channel.streamUrl) {
                    channel.streamUrl.startsWith("file:") ||
                    channel.streamUrl.startsWith("android.resource:") ||
                    channel.streamUrl.startsWith("content:")
                }

                if (isLocalUri) {
                    var isVideoPrepared by remember(channel.id) { mutableStateOf(false) }
                    var hasPlaybackError by remember(channel.id) { mutableStateOf(false) }

                    AndroidView(
                        factory = { context ->
                            VideoView(context).apply {
                                try {
                                    val uri = Uri.parse(channel.streamUrl)
                                    setVideoURI(uri)
                                    setOnPreparedListener { mp ->
                                        try {
                                            isVideoPrepared = true
                                            mp.isLooping = true
                                            val vol = if (isMuted) 0f else volume
                                            mp.setVolume(vol, vol)
                                            if (isPlaying) {
                                                try { start() } catch (_: Exception) {}
                                            }
                                        } catch (_: Exception) {}
                                    }
                                    setOnErrorListener { _, _, _ ->
                                        hasPlaybackError = true
                                        true
                                    }
                                } catch (_: Exception) {
                                    hasPlaybackError = true
                                }
                            }
                        },
                        update = { videoView ->
                            try {
                                if (isVideoPrepared && !hasPlaybackError) {
                                    if (isPlaying) {
                                        if (!videoView.isPlaying) videoView.start()
                                    } else {
                                        if (videoView.isPlaying) videoView.pause()
                                    }
                                }
                            } catch (_: Exception) {}
                        },
                        onRelease = { videoView ->
                            try {
                                videoView.stopPlayback()
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // High-performance broadcast surface with live audio spectrum and dynamic TV graphics
                    LiveTvBroadcastSurface(
                        channel = channel,
                        isPlaying = isPlaying,
                        isMuted = isMuted,
                        volume = volume,
                        strings = strings,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.searchPlaceholder,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }

            // Data Saver Overlay (Audio-only mode)
            if (quality.contains("Veri Tasarrufu")) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.82f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = TvCyan,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = strings.dataSaver,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Yalnızca Ses Modu Aktif",
                            color = TvCyan,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Controls Overlay
            androidx.compose.animation.AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.85f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.92f)
                                )
                            )
                        )
                ) {
                    // Top Player Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // LIVE Pulse Tag
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TvLiveRed.copy(alpha = liveAlpha),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(Color.White, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = strings.live,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Pulsing REC Badge when recording
                            if (isRecording) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = TvLiveRed,
                                    modifier = Modifier.padding(end = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FiberManualRecord,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        val m = recordingSeconds / 60
                                        val s = recordingSeconds % 60
                                        Text(
                                            text = "REC %02d:%02d".format(m, s),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Channel Name & Category
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = channel?.name ?: "",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    // Live Viewers Badge
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color.Black.copy(alpha = 0.5f)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = null,
                                                tint = TvCyan,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "${(((channel?.bitrateMbps ?: 5.0f).toDouble()) * 4.8).toInt() + 12}K",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = channel?.category ?: "",
                                        color = TvSkyBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color.White.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = quality.substringBefore(" "),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Top Right Actions (Quality, Sleep Timer, PiP, Share, Aspect Ratio, Favorite)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Quality Selector Button
                            IconButton(
                                onClick = onOpenQualityDialog,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("player_quality_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HighQuality,
                                    contentDescription = "Kalite",
                                    tint = TvCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Sleep Timer Button
                            Box {
                                IconButton(
                                    onClick = onOpenSleepTimerDialog,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("player_sleep_timer_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bedtime,
                                        contentDescription = strings.sleepTimer,
                                        tint = if (sleepTimerRemainingSeconds != null) TvAmber else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                if (sleepTimerRemainingSeconds != null && sleepTimerRemainingSeconds > 0) {
                                    Surface(
                                        shape = CircleShape,
                                        color = TvAmber,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(14.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${sleepTimerRemainingSeconds / 60}",
                                                fontSize = 8.sp,
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // Picture-in-Picture (PiP) Button
                            IconButton(
                                onClick = onEnterPiP,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("player_pip_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureInPictureAlt,
                                    contentDescription = strings.pipMode,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Share Channel
                            IconButton(
                                onClick = onShareChannel,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("player_share_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Paylaş",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Audio and Subtitles Selector
                            IconButton(
                                onClick = onOpenAudioSubtitleDialog,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("player_audio_subtitle_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ClosedCaption,
                                    contentDescription = strings.audioAndSubtitles,
                                    tint = TvCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // DVR Recording Toggle
                            IconButton(
                                onClick = onToggleRecording,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("player_record_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FiberManualRecord,
                                    contentDescription = strings.recordLive,
                                    tint = if (isRecording) TvLiveRed else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Screen Lock Button
                            IconButton(
                                onClick = onToggleScreenLock,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("player_lock_button")
                            ) {
                                Icon(
                                    imageVector = if (isScreenLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = strings.screenLocked,
                                    tint = if (isScreenLocked) TvAmber else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Aspect Ratio
                            IconButton(
                                onClick = onToggleAspectRatio,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AspectRatio,
                                    contentDescription = "Aspect Ratio",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Favorite toggle
                            if (channel != null) {
                                IconButton(
                                    onClick = { onToggleFavorite(channel) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("player_fav_button")
                                ) {
                                    Icon(
                                        imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (channel.isFavorite) TvAmber else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Center Play/Pause button
                    Box(
                        modifier = Modifier.align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.65f),
                            modifier = Modifier
                                .size(56.dp)
                                .clickable { onTogglePlay() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    // Bottom Controls & EPG
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        // Quick Channel Switcher inside player
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            items(channels) { ch ->
                                val isSelected = ch.id == channel?.id
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) TvSkyBlue.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.6f),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, TvSkyBlue) else null,
                                    modifier = Modifier
                                        .clickable { onSelectChannel(ch) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        if (ch.isFavorite) {
                                            Icon(
                                                imageVector = Icons.Default.Favorite,
                                                contentDescription = null,
                                                tint = TvAmber,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = ch.name,
                                            color = if (isSelected) TvSkyBlue else Color.White.copy(alpha = 0.9f),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        // EPG Program Info (Şimdi & Sonraki)
                        if (channel != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "${strings.nowPlaying}: ",
                                        color = TvAmber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = channel.currentProgram,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${strings.upNext}: ${channel.nextProgram}",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = onToggleReminder,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .testTag("player_reminder_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isReminderSet) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                            contentDescription = strings.setReminder,
                                            tint = if (isReminderSet) TvAmber else Color.White.copy(alpha = 0.7f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            // EPG Progress Bar
                            LinearProgressIndicator(
                                progress = { channel.epgProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .padding(vertical = 1.dp),
                                color = TvSkyBlue,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                        }

                        // Bottom Actions Bar (Volume, Mute, Bitrate, Fullscreen)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onToggleMute,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isMuted || volume == 0f) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                        contentDescription = "Mute",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Volume Slider overlay toggle
                                IconButton(
                                    onClick = { showVolumeSlider = !showVolumeSlider },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text(
                                        text = "${(if (isMuted) 0 else (volume * 100).toInt())}%",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 10.sp
                                    )
                                }

                                if (showVolumeSlider) {
                                    Slider(
                                        value = volume,
                                        onValueChange = onVolumeChange,
                                        modifier = Modifier
                                            .width(80.dp)
                                            .height(24.dp),
                                        colors = SliderDefaults.colors(
                                            thumbColor = TvSkyBlue,
                                            activeTrackColor = TvSkyBlue,
                                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            }

                            // Bitrate & Fullscreen
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = "Bitrate",
                                        tint = TvCyan,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${channel?.bitrateMbps ?: 5.8} Mbps",
                                        color = TvCyan,
                                        fontSize = 10.sp
                                    )
                                }

                                IconButton(
                                    onClick = onToggleDiagnostics,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("player_diagnostics_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = strings.statsForNerds,
                                        tint = if (showDiagnostics) TvCyan else Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onToggleZapBar,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("player_zap_bar_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatListBulleted,
                                        contentDescription = strings.zapBar,
                                        tint = if (showZapBar) TvSkyBlue else Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onToggleFullscreen,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("fullscreen_toggle_button")
                                ) {
                                    Icon(
                                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                        contentDescription = "Fullscreen",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Screen Lock Floating Unlock Button
            if (isScreenLocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, TvAmber),
                        modifier = Modifier
                            .clickable { onToggleScreenLock() }
                            .testTag("unlock_screen_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = strings.tapToUnlock,
                                tint = TvAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.tapToUnlock,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TvAmber
                            )
                        }
                    }
                }
            }

            // Stream Diagnostics Overlay
            if (showDiagnostics) {
                StreamDiagnosticsOverlay(
                    channel = channel,
                    streamQuality = quality,
                    onClose = onToggleDiagnostics,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp)
                )
            }

            // Quick Zap Bar Overlay
            if (showZapBar) {
                QuickZapBar(
                    channels = channels,
                    currentChannel = channel,
                    onSelectChannel = { ch ->
                        onSelectChannel(ch)
                    },
                    onClose = onToggleZapBar,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun LiveTvBroadcastSurface(
    channel: Channel,
    isPlaying: Boolean,
    isMuted: Boolean,
    volume: Float,
    strings: AppStrings,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "broadcast")

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val scanSweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanSweep"
    )

    // Category-specific high-grade broadcast styling
    val (primaryHue, accentHue, bgTop, bgBottom) = remember(channel.category) {
        when (channel.category.lowercase()) {
            "spor" -> Quad(
                Color(0xFF00E676),
                Color(0xFF00B0FF),
                Color(0xFF04180F),
                Color(0xFF0A2B1D)
            )
            "haber" -> Quad(
                Color(0xFF2979FF),
                Color(0xFFFF9100),
                Color(0xFF061426),
                Color(0xFF0F2442)
            )
            "sinema" -> Quad(
                Color(0xFFFFAB00),
                Color(0xFFE040FB),
                Color(0xFF140822),
                Color(0xFF24103B)
            )
            "belgesel" -> Quad(
                Color(0xFF00E5FF),
                Color(0xFF76FF03),
                Color(0xFF021D20),
                Color(0xFF08373D)
            )
            else -> Quad(
                TvSkyBlue,
                TvCyan,
                Color(0xFF0A1424),
                Color(0xFF142440)
            )
        }
    }

    Box(
        modifier = modifier
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        bgBottom,
                        bgTop,
                        Color(0xFF030509)
                    )
                )
            )
    ) {
        // Dynamic live studio broadcast canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            if (width <= 0 || height <= 0) return@Canvas

            // 1. Studio scan lines
            val scanStep = 7.dp.toPx()
            var y = 0f
            while (y < height) {
                drawLine(
                    color = Color.White.copy(alpha = 0.02f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += scanStep
            }

            // 2. Animated scanning beam
            val beamX = scanSweep * width
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        primaryHue.copy(alpha = 0.035f),
                        Color.Transparent
                    ),
                    startX = beamX - 100.dp.toPx(),
                    endX = beamX + 100.dp.toPx()
                ),
                size = size
            )

            // 3. Audio spectrum frequency bars
            val barCount = 30
            val totalBarWidth = width * 0.72f
            val barWidth = (totalBarWidth / barCount) * 0.65f
            val barSpacing = (totalBarWidth / barCount) * 0.35f
            val startX = (width - totalBarWidth) / 2f
            val baselineY = height * 0.66f

            val effectiveAmp = if (!isPlaying) 0.06f else if (isMuted) 0.02f else (volume.coerceIn(0.15f, 1f))

            for (i in 0 until barCount) {
                val normalizedIndex = i.toFloat() / barCount
                val wave1 = kotlin.math.sin(wavePhase + i * 0.44).toFloat()
                val wave2 = kotlin.math.cos(wavePhase * 1.4 + i * 0.31).toFloat()
                val wave3 = kotlin.math.sin(wavePhase * 0.8 - i * 0.52).toFloat()
                val combined = (kotlin.math.abs(wave1 * 0.5f + wave2 * 0.32f + wave3 * 0.28f)).coerceIn(0.04f, 1f)

                val envelope = kotlin.math.sin(normalizedIndex * Math.PI.toFloat()).coerceIn(0.18f, 1f)
                val barHeight = (height * 0.30f * combined * envelope * effectiveAmp).coerceAtLeast(3.dp.toPx())

                val x = startX + i * (barWidth + barSpacing)
                val topY = baselineY - barHeight

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryHue.copy(alpha = if (isPlaying) 0.88f else 0.25f),
                            accentHue.copy(alpha = if (isPlaying) 0.5f else 0.12f)
                        ),
                        startY = topY,
                        endY = baselineY
                    ),
                    topLeft = Offset(x, topY),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )

                // Mirror reflection below baseline
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            accentHue.copy(alpha = if (isPlaying) 0.18f else 0.04f),
                            Color.Transparent
                        ),
                        startY = baselineY,
                        endY = baselineY + barHeight * 0.32f
                    ),
                    topLeft = Offset(x, baselineY + 2.dp.toPx()),
                    size = Size(barWidth, barHeight * 0.32f),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }
        }

        // Live Watermark and Channel Logo Badge (Top-Right)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.55f),
                border = androidx.compose.foundation.BorderStroke(1.dp, primaryHue.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(TvLiveRed.copy(alpha = if (isPlaying) pulseAlpha else 0.3f))
                    )
                    Text(
                        text = if (isPlaying) "CANLI" else "BEKLEMEDE",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.45f)
            ) {
                Text(
                    text = channel.resolution,
                    color = primaryHue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                )
            }
        }

        // Top-Left Watermark (Channel Name & Category)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = primaryHue.copy(alpha = 0.18f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = channel.name.take(2).uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = primaryHue
                    )
                }
            }

            Column {
                Text(
                    text = channel.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "${channel.category} • ${channel.bitrateMbps} Mbps",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            }
        }

        // Center Pause Overlay when paused
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TvSkyBlue.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = null,
                            tint = TvSkyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Yayın Duraklatıldı",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Lower-Third Live Program Banner
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = channel.currentProgram.ifEmpty { "Canlı Yayın Akışı" },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Sonraki: ${channel.nextProgram.ifEmpty { "Ana Haber Bülteni" }}",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SignalCellularAlt,
                        contentDescription = null,
                        tint = primaryHue,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Sinyal %99",
                        color = primaryHue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
