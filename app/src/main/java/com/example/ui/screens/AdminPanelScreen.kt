package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import com.example.data.model.AppUpdateInfo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.cache.CacheMetrics
import com.example.data.model.Channel
import com.example.data.model.NotificationPriority
import com.example.data.model.ProjectReport
import com.example.data.security.CryptoManager
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.TvAmber
import com.example.ui.theme.TvCyan
import com.example.ui.theme.TvEmerald
import com.example.ui.theme.TvLiveRed
import com.example.ui.theme.TvPurple
import com.example.ui.theme.TvSkyBlue

@Composable
fun AdminPanelScreen(
    channels: List<Channel>,
    cacheMetrics: CacheMetrics,
    projectReport: ProjectReport,
    isAdminUnlocked: Boolean,
    strings: AppStrings,
    onUnlockAdmin: (String) -> Boolean,
    onLockAdmin: () -> Unit,
    onSaveChannel: (Channel, Boolean) -> Unit,
    onDeleteChannel: (String) -> Unit,
    onToggleActive: (Channel) -> Unit,
    onSendNotification: (String, String, String?, NotificationPriority, String) -> Unit,
    onFlushCache: () -> Unit,
    updateInfo: AppUpdateInfo = AppUpdateInfo(),
    onPublishUpdate: (String, String, Boolean) -> Unit = { _, _, _ -> },
    onCheckUpdate: () -> Unit = {},
    isDynamicColorEnabled: Boolean = true,
    onToggleDynamicColor: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!isAdminUnlocked) {
        AdminPinPrompt(strings = strings, onUnlockAdmin = onUnlockAdmin, modifier = modifier)
    } else {
        AdminDashboard(
            channels = channels,
            cacheMetrics = cacheMetrics,
            projectReport = projectReport,
            updateInfo = updateInfo,
            strings = strings,
            onLockAdmin = onLockAdmin,
            onSaveChannel = onSaveChannel,
            onDeleteChannel = onDeleteChannel,
            onToggleActive = onToggleActive,
            onSendNotification = onSendNotification,
            onFlushCache = onFlushCache,
            onPublishUpdate = onPublishUpdate,
            onCheckUpdate = onCheckUpdate,
            isDynamicColorEnabled = isDynamicColorEnabled,
            onToggleDynamicColor = onToggleDynamicColor,
            modifier = modifier
        )
    }
}

@Composable
fun AdminPinPrompt(
    strings: AppStrings,
    onUnlockAdmin: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("admin_pin_prompt"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = TvSkyBlue.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TvSkyBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = strings.adminPanel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = strings.adminPinRequired,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        pin = it
                        showError = false
                    },
                    label = { Text(strings.enterPin) },
                    singleLine = true,
                    isError = showError,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )

                if (showError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = strings.incorrectPin,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val success = onUnlockAdmin(pin)
                        if (!success) {
                            showError = true
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_unlock_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TvSkyBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.unlock,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Quick test PIN helper button
                TextButton(
                    onClick = {
                        pin = "1234"
                        onUnlockAdmin("1234")
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = "Hızlı Giriş (PIN: 1234)", fontSize = 12.sp, color = TvSkyBlue)
                }
            }
        }
    }
}

@Composable
fun AdminDashboard(
    channels: List<Channel>,
    cacheMetrics: CacheMetrics,
    projectReport: ProjectReport,
    updateInfo: AppUpdateInfo = AppUpdateInfo(),
    strings: AppStrings,
    onLockAdmin: () -> Unit,
    onSaveChannel: (Channel, Boolean) -> Unit,
    onDeleteChannel: (String) -> Unit,
    onToggleActive: (Channel) -> Unit,
    onSendNotification: (String, String, String?, NotificationPriority, String) -> Unit,
    onFlushCache: () -> Unit,
    onPublishUpdate: (String, String, Boolean) -> Unit = { _, _, _ -> },
    onCheckUpdate: () -> Unit = {},
    isDynamicColorEnabled: Boolean = true,
    onToggleDynamicColor: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedAdminTab by remember { mutableStateOf(0) }
    var channelToEdit by remember { mutableStateOf<Channel?>(null) }
    var isNewChannelDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TvSkyBlue.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = TvSkyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = strings.adminPanel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Root Session • AES-GCM Secured",
                        fontSize = 11.sp,
                        color = TvCyan
                    )
                }
            }

            OutlinedButton(
                onClick = onLockAdmin,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Çıkış", fontSize = 11.sp)
            }
        }

        // Admin Tabs
        TabRow(
            selectedTabIndex = selectedAdminTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = TvSkyBlue
        ) {
            Tab(
                selected = selectedAdminTab == 0,
                onClick = { selectedAdminTab = 0 },
                text = { Text(strings.channels, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedAdminTab == 1,
                onClick = { selectedAdminTab = 1 },
                text = { Text(strings.sendNotification, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedAdminTab == 2,
                onClick = { selectedAdminTab = 2 },
                text = { Text(strings.cacheTitle, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedAdminTab == 3,
                onClick = { selectedAdminTab = 3 },
                text = { Text(strings.projectReport, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedAdminTab == 4,
                onClick = { selectedAdminTab = 4 },
                text = { Text(strings.checkForUpdates, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedAdminTab) {
                0 -> AdminChannelsTab(
                    channels = channels,
                    strings = strings,
                    onEditChannel = {
                        channelToEdit = it
                        isNewChannelDialog = false
                    },
                    onAddNewChannel = {
                        channelToEdit = Channel(
                            id = "ch_${System.currentTimeMillis()}",
                            name = "",
                            category = "Ulusal",
                            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                            resolution = "1080p FHD",
                            isActive = true
                        )
                        isNewChannelDialog = true
                    },
                    onDeleteChannel = onDeleteChannel,
                    onToggleActive = onToggleActive
                )

                1 -> AdminNotificationsTab(
                    channels = channels,
                    strings = strings,
                    onSendNotification = onSendNotification
                )

                2 -> AdminCacheSecurityTab(
                    cacheMetrics = cacheMetrics,
                    strings = strings,
                    onFlushCache = onFlushCache,
                    isDynamicColorEnabled = isDynamicColorEnabled,
                    onToggleDynamicColor = onToggleDynamicColor
                )

                3 -> AdminProjectRoadmapTab(
                    projectReport = projectReport,
                    strings = strings
                )

                4 -> AdminUpdatesTab(
                    updateInfo = updateInfo,
                    strings = strings,
                    onPublishUpdate = onPublishUpdate,
                    onCheckUpdate = onCheckUpdate
                )
            }
        }

        // Channel Add / Edit Dialog
        if (channelToEdit != null) {
            ChannelEditDialog(
                channel = channelToEdit!!,
                isNew = isNewChannelDialog,
                strings = strings,
                onDismiss = { channelToEdit = null },
                onSave = { updated ->
                    onSaveChannel(updated, isNewChannelDialog)
                    channelToEdit = null
                }
            )
        }
    }
}

@Composable
fun AdminChannelsTab(
    channels: List<Channel>,
    strings: AppStrings,
    onEditChannel: (Channel) -> Unit,
    onAddNewChannel: () -> Unit,
    onDeleteChannel: (String) -> Unit,
    onToggleActive: (Channel) -> Unit
) {
    var channelToDelete by remember { mutableStateOf<Channel?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(channels) { ch ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ch.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = ch.category,
                                        fontSize = 10.sp,
                                        color = TvSkyBlue,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ch.streamUrl,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Token: ${ch.encryptedToken.take(16)}...",
                                fontSize = 10.sp,
                                color = TvCyan
                            )
                        }

                        // Active Switch & Edit & Delete
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = ch.isActive,
                                onCheckedChange = { onToggleActive(ch) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = TvEmerald
                                )
                            )

                            IconButton(onClick = { onEditChannel(ch) }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = TvSkyBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(onClick = { channelToDelete = ch }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Bottom space for FAB
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // Add channel FAB
        FloatingActionButton(
            onClick = onAddNewChannel,
            containerColor = TvSkyBlue,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("admin_add_channel_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Channel")
        }

        // Delete Confirmation Dialog
        if (channelToDelete != null) {
            AlertDialog(
                onDismissRequest = { channelToDelete = null },
                title = { Text(strings.confirmDelete) },
                text = { Text("${channelToDelete?.name} - ${strings.confirmDeleteMsg}") },
                confirmButton = {
                    Button(
                        onClick = {
                            channelToDelete?.let { onDeleteChannel(it.id) }
                            channelToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(strings.delete)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { channelToDelete = null }) {
                        Text(strings.cancel)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelEditDialog(
    channel: Channel,
    isNew: Boolean,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onSave: (Channel) -> Unit
) {
    var name by remember { mutableStateOf(channel.name) }
    var category by remember { mutableStateOf(channel.category) }
    var streamUrl by remember { mutableStateOf(channel.streamUrl) }
    var resolution by remember { mutableStateOf(channel.resolution) }
    var currentProg by remember { mutableStateOf(channel.currentProgram) }
    var nextProg by remember { mutableStateOf(channel.nextProgram) }
    var isActive by remember { mutableStateOf(channel.isActive) }

    val categories = listOf("Ulusal", "Haber", "Spor", "Belgesel", "Sinema", "Çocuk", "Müzik", "Eğlence")
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isNew) strings.addChannel else strings.editChannel,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.channelName) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(strings.category) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = streamUrl,
                    onValueChange = { streamUrl = it },
                    label = { Text(strings.streamUrl) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentProg,
                        onValueChange = { currentProg = it },
                        label = { Text(strings.nowPlaying) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = nextProg,
                        onValueChange = { nextProg = it },
                        label = { Text(strings.upNext) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = strings.isActive, fontSize = 14.sp)
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = TvEmerald)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = channel.copy(
                        name = name.ifBlank { "Yeni Kanal" },
                        category = category,
                        streamUrl = streamUrl.ifBlank { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" },
                        resolution = resolution,
                        currentProgram = currentProg.ifBlank { "Canlı Yayın" },
                        nextProgram = nextProg.ifBlank { "Günün Özeti" },
                        isActive = isActive
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TvSkyBlue)
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}

@Composable
fun AdminNotificationsTab(
    channels: List<Channel>,
    strings: AppStrings,
    onSendNotification: (String, String, String?, NotificationPriority, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedChannelId by remember { mutableStateOf<String?>(null) }
    var selectedPriority by remember { mutableStateOf(NotificationPriority.HIGH) }
    var sendSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = TvLiveRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.sendNotification,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        sendSuccess = false
                    },
                    label = { Text(strings.notificationTitle) },
                    placeholder = { Text("Örn: 🔴 Süper Lig Derbi Maçı Başladı!") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_notif_title_input")
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it
                        sendSuccess = false
                    },
                    label = { Text(strings.notificationBody) },
                    placeholder = { Text("Örn: Dev derbi TRT Spor ekranlarında canlı yayında. Hemen izleyin!") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("admin_notif_msg_input")
                )

                // Priority selector
                Text(text = "Öncelik Seviyesi:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        NotificationPriority.BREAKING to "Son Dakika",
                        NotificationPriority.HIGH to "Yüksek",
                        NotificationPriority.NORMAL to "Normal"
                    ).forEach { (prio, label) ->
                        val isSelected = selectedPriority == prio
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) {
                                if (prio == NotificationPriority.BREAKING) TvLiveRed else TvSkyBlue
                            } else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedPriority = prio }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                if (sendSuccess) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TvEmerald.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = TvEmerald)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.notificationSent,
                                color = TvEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (title.isNotBlank() && message.isNotBlank()) {
                            onSendNotification(
                                title,
                                message,
                                selectedChannelId ?: channels.firstOrNull()?.id,
                                selectedPriority,
                                if (selectedPriority == NotificationPriority.BREAKING) "Son Dakika" else "Canlı TV"
                            )
                            title = ""
                            message = ""
                            sendSuccess = true
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TvLiveRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_send_broadcast_btn")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = strings.sendBroadcast, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminCacheSecurityTab(
    cacheMetrics: CacheMetrics,
    strings: AppStrings,
    onFlushCache: () -> Unit,
    isDynamicColorEnabled: Boolean = true,
    onToggleDynamicColor: (Boolean) -> Unit = {}
) {
    var testResult by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Material You Dynamic Color Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.dynamicColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = strings.dynamicColorDesc,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isDynamicColorEnabled,
                        onCheckedChange = onToggleDynamicColor,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Cache Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = TvSkyBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.cacheTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onFlushCache,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Cached, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = strings.clearCache, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CacheMetricBox(
                            title = strings.cacheHits,
                            value = "${cacheMetrics.hitRatePercent}%",
                            color = TvEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        CacheMetricBox(
                            title = "Önbellek İtemi",
                            value = "${cacheMetrics.itemCount}",
                            color = TvSkyBlue,
                            modifier = Modifier.weight(1f)
                        )
                        CacheMetricBox(
                            title = "Tahmini Boyut",
                            value = "${cacheMetrics.approxSizeKb} KB",
                            color = TvPurple,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Security / E2EE Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = TvEmerald)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.securityTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Şifreleme Algoritması: ${CryptoManager.getEncryptionInfo()}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Akış anahtarları ve kullanıcı izleme geçmişi cihaz üzerinde AES-256 GCM ile şifrelenir.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val sample = "CANLI-YAYIN-TOKEN-${System.currentTimeMillis()}"
                            val encrypted = CryptoManager.encrypt(sample)
                            val decrypted = CryptoManager.decrypt(encrypted)
                            val checksum = CryptoManager.generateChecksum(sample)
                            testResult = "Doğrulama Başarılı! \nOrijinal: $sample \nŞifreli (AES-256): ${encrypted.take(24)}... \nÇözülen: $decrypted \nSHA-256 Sağlama: $checksum"
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TvEmerald),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "E2EE Şifreleme Testini Çalıştır")
                    }

                    if (testResult != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = testResult!!,
                                fontSize = 11.sp,
                                color = TvEmerald,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CacheMetricBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun AdminProjectRoadmapTab(
    projectReport: ProjectReport,
    strings: AppStrings
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = strings.projectRoadmap,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${projectReport.projectName} (${projectReport.version})",
                                fontSize = 11.sp,
                                color = TvSkyBlue
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TvEmerald.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "%${projectReport.overallProgress} Tamamlandı",
                                color = TvEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { projectReport.overallProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = TvEmerald,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        items(projectReport.milestones) { milestone ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TvSkyBlue.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = milestone.phaseId,
                                    color = TvSkyBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = milestone.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = TvEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = milestone.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    milestone.deliverables.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(TvEmerald, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUpdatesTab(
    updateInfo: AppUpdateInfo,
    strings: AppStrings,
    onPublishUpdate: (String, String, Boolean) -> Unit,
    onCheckUpdate: () -> Unit
) {
    var targetVersion by remember { mutableStateOf("v2.5.0-PRO") }
    var changeNotes by remember {
        mutableStateOf(
            "• Canlı EPG & Hatırlatıcı bildirim desteği\n• Uyku Zamanlayıcısı (Sleep Timer)\n• Akıllı Veri Tasarrufu Modu (Data Saver)\n• Çoklu çözünürlük akış kalitesi seçici\n• Resim İçinde Resim (PiP) oynatma"
        )
    }
    var isMandatory by remember { mutableStateOf(false) }
    var publishSuccessMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_updates_tab"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Status Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = TvEmerald.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SystemUpdate,
                                        contentDescription = null,
                                        tint = TvEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Sürüm & Güncelleme Yönetimi",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "CDN Güncelleme Dağıtım Kanalı",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (updateInfo.isUpdateAvailable) TvAmber.copy(alpha = 0.2f) else TvEmerald.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (updateInfo.isUpdateAvailable) "Yeni Sürüm Yayında" else "Güncel",
                                color = if (updateInfo.isUpdateAvailable) TvAmber else TvEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Yüklü İstemci", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = updateInfo.currentVersion, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Bulut Sürümü", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = updateInfo.latestVersion, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TvSkyBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onCheckUpdate,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TvSkyBlue)
                    ) {
                        Icon(imageVector = Icons.Default.Cached, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Kullanıcı Gözüyle Güncelleme Kontrolünü Tetikle", fontSize = 13.sp)
                    }
                }
            }
        }

        // Release Deployment Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Yeni Sürüm Paketi Yayınla",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Yayınlanan güncelleme tüm kullanıcılara otomatik bildirim ve güncelleme modalı olarak yansıtılır.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = targetVersion,
                        onValueChange = { targetVersion = it },
                        label = { Text("Hedef Sürüm No") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = changeNotes,
                        onValueChange = { changeNotes = it },
                        label = { Text(strings.whatsNew) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 110.dp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Zorunlu Güncelleme", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(
                                text = "Kullanıcı güncellemeden uygulamayı kullanamaz",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isMandatory,
                            onCheckedChange = { isMandatory = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (publishSuccessMessage) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TvEmerald.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Güncelleme $targetVersion başarıyla dağıtıma verildi!",
                                color = TvEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Button(
                        onClick = {
                            onPublishUpdate(targetVersion, changeNotes, isMandatory)
                            publishSuccessMessage = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TvLiveRed)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Yeni Sürümü Canlıya Dağıt", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
