package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.data.model.ChatMessage
import com.example.data.model.CommunityPoll
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.TvAmber
import com.example.ui.theme.TvCyan
import com.example.ui.theme.TvEmerald
import com.example.ui.theme.TvLiveRed
import com.example.ui.theme.TvPurple
import com.example.ui.theme.TvSkyBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private data class FloatingEmoji(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val initialXOffset: Float,
    val initialTime: Long = System.currentTimeMillis()
)

@Composable
fun ChatCommunityScreen(
    currentPlayingChannel: Channel?,
    chatMessages: List<ChatMessage>,
    communityPolls: List<CommunityPoll>,
    chatFilter: String,
    strings: AppStrings,
    onFilterChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onLikeMessage: (Long) -> Unit,
    onVotePoll: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val floatingEmojis = remember { mutableStateListOf<FloatingEmoji>() }

    // Auto scroll to bottom when new message arrives
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    // Pulse animation for online badge
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    fun triggerEmoji(emoji: String) {
        val randomX = (-60..60).random().toFloat()
        val item = FloatingEmoji(emoji = emoji, initialXOffset = randomX)
        floatingEmojis.add(item)
        scope.launch {
            delay(2200)
            floatingEmojis.remove(item)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("chat_community_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: Live status & room selector
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(TvSkyBlue, TvCyan)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = strings.chatCommunity,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = currentPlayingChannel?.name ?: "Tüm Kanallar",
                                    fontSize = 12.sp,
                                    color = TvSkyBlue
                                )
                            }
                        }

                        // Pulsing Live viewers count
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = TvEmerald.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TvEmerald.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(TvEmerald.copy(alpha = pulseAlpha))
                                )
                                Text(
                                    text = "1.4k ${strings.onlineViewers}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TvEmerald
                                )
                            }
                        }
                    }

                    // Room Selector Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        item {
                            RoomFilterChip(
                                title = "🌐 Genel Topluluk",
                                isSelected = chatFilter == "all",
                                onClick = { onFilterChange("all") }
                            )
                        }
                        if (currentPlayingChannel != null) {
                            item {
                                RoomFilterChip(
                                    title = "📺 ${currentPlayingChannel.name}",
                                    isSelected = chatFilter == currentPlayingChannel.id,
                                    onClick = { onFilterChange(currentPlayingChannel.id) }
                                )
                            }
                        }
                        item {
                            RoomFilterChip(
                                title = "⚽ Spor & Maç Odası",
                                isSelected = chatFilter == "trtspor",
                                onClick = { onFilterChange("trtspor") }
                            )
                        }
                    }
                }
            }

            // Message List with Pinned Poll at Top
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Interactive Community Poll
                if (communityPolls.isNotEmpty()) {
                    val activePoll = communityPolls.first()
                    item(key = activePoll.id) {
                        PollInteractiveCard(
                            poll = activePoll,
                            strings = strings,
                            onVote = { optionIndex -> onVotePoll(activePoll.id, optionIndex) }
                        )
                    }
                }

                items(chatMessages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        onLike = { onLikeMessage(message.id) }
                    )
                }
            }

            // Quick Floating Reaction Shortcuts Bar
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val reactions = listOf("❤️", "🔥", "👏", "⚽", "📺", "🎉", "💯")
                    reactions.forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    triggerEmoji(emoji)
                                    onSendMessage(emoji)
                                }
                                .padding(6.dp)
                        )
                    }
                }
            }

            // Bottom Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = {
                            Text(
                                text = strings.typeMessage,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        },
                        singleLine = true,
                        maxLines = 1,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TvSkyBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedContainerColor = DarkCard,
                            unfocusedContainerColor = DarkCard,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (messageInput.isNotBlank()) {
                                    onSendMessage(messageInput)
                                    messageInput = ""
                                }
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field")
                    )

                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                onSendMessage(messageInput)
                                messageInput = ""
                            }
                        },
                        enabled = messageInput.isNotBlank(),
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (messageInput.isNotBlank()) TvSkyBlue else Color.White.copy(alpha = 0.12f)
                            )
                            .testTag("send_chat_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = strings.sendMessage,
                            tint = if (messageInput.isNotBlank()) Color.Black else Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Floating particle emojis that float up
        floatingEmojis.forEach { item ->
            FloatingEmojiParticle(item = item)
        }
    }
}

@Composable
private fun RoomFilterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) TvSkyBlue.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) TvSkyBlue else Color.White.copy(alpha = 0.15f)
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) TvSkyBlue else Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun PollInteractiveCard(
    poll: CommunityPoll,
    strings: AppStrings,
    onVote: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TvPurple.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = TvPurple.copy(alpha = 0.2f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.HowToVote,
                            contentDescription = null,
                            tint = TvPurple,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = strings.livePoll,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TvPurple
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${poll.totalVotes} ${strings.totalVotes}",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = poll.question,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 1
            PollOptionRow(
                optionText = poll.option1,
                votes = poll.votes1,
                percent = poll.percent1,
                isSelected = poll.userVotedOption == 1,
                hasVoted = poll.userVotedOption != 0,
                color = TvSkyBlue,
                onClick = { if (poll.userVotedOption == 0) onVote(1) }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Option 2
            PollOptionRow(
                optionText = poll.option2,
                votes = poll.votes2,
                percent = poll.percent2,
                isSelected = poll.userVotedOption == 2,
                hasVoted = poll.userVotedOption != 0,
                color = TvAmber,
                onClick = { if (poll.userVotedOption == 0) onVote(2) }
            )

            if (poll.option3.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                PollOptionRow(
                    optionText = poll.option3,
                    votes = poll.votes3,
                    percent = poll.percent3,
                    isSelected = poll.userVotedOption == 3,
                    hasVoted = poll.userVotedOption != 0,
                    color = TvEmerald,
                    onClick = { if (poll.userVotedOption == 0) onVote(3) }
                )
            }
        }
    }
}

@Composable
private fun PollOptionRow(
    optionText: String,
    votes: Int,
    percent: Float,
    isSelected: Boolean,
    hasVoted: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) color.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) color else Color.White.copy(alpha = 0.12f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !hasVoted) { onClick() }
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = optionText,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) color else Color.White
                )
                if (hasVoted) {
                    Text(
                        text = "%${(percent * 100).toInt()} ($votes)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }

            if (hasVoted) {
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { percent },
                    color = color,
                    trackColor = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessage,
    onLike: () -> Unit
) {
    val timeFormatted = remember(message.timestamp) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
    }

    val bubbleBg = if (message.isPinned) {
        Brush.horizontalGradient(listOf(Color(0xFF1E2846), Color(0xFF101932)))
    } else if (message.isUserMessage) {
        Brush.horizontalGradient(listOf(Color(0xFF0F3057), Color(0xFF0D2544)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF161E2E), Color(0xFF121824)))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .background(bubbleBg, RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (message.isPinned) TvAmber.copy(alpha = 0.4f) else if (message.isUserMessage) TvSkyBlue.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f),
                RoundedCornerShape(14.dp)
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Pinned indicator
            if (message.isPinned) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = TvAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Sabitlenmiş Topluluk Mesajı",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvAmber
                    )
                }
            }

            // Sender info & Role
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Avatar
                    Surface(
                        shape = CircleShape,
                        color = Color(message.avatarColorHex).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(message.avatarColorHex).copy(alpha = 0.6f)),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = message.senderName.take(1).uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(message.avatarColorHex)
                            )
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = message.senderName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // Role Badge
                            val (roleColor, roleIcon) = when (message.senderRole) {
                                "Moderatör" -> Pair(TvEmerald, Icons.Default.Verified)
                                "VIP" -> Pair(TvAmber, Icons.Default.Star)
                                else -> Pair(TvSkyBlue, Icons.Default.Person)
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = roleColor.copy(alpha = 0.18f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = roleIcon,
                                        contentDescription = null,
                                        tint = roleColor,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = message.senderRole,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = roleColor
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = timeFormatted,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.45f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Content text
            Text(
                text = message.message,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.95f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom like bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (message.isLikedByUser) TvLiveRed.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.05f),
                    modifier = Modifier.clickable { onLike() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (message.isLikedByUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Beğen",
                            tint = if (message.isLikedByUser) TvLiveRed else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(13.dp)
                        )
                        if (message.likesCount > 0) {
                            Text(
                                text = message.likesCount.toString(),
                                fontSize = 11.sp,
                                color = if (message.isLikedByUser) TvLiveRed else Color.White.copy(alpha = 0.6f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingEmojiParticle(item: FloatingEmoji) {
    val animY = remember { Animatable(0f) }
    val animAlpha = remember { Animatable(1f) }

    LaunchedEffect(item.id) {
        animY.animateTo(
            targetValue = -350f,
            animationSpec = tween(1800, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(item.id) {
        delay(800)
        animAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = item.emoji,
            fontSize = 32.sp,
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = item.initialXOffset.toInt(),
                        y = animY.value.toInt()
                    )
                }
                .alpha(animAlpha.value)
        )
    }
}
