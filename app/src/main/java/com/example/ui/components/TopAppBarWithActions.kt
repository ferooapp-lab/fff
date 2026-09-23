package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.TvLiveRed
import com.example.ui.theme.TvSkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithActions(
    strings: AppStrings,
    searchQuery: String,
    unreadCount: Int,
    isGridView: Boolean,
    isDarkMode: Boolean,
    currentLanguage: AppLanguage,
    hasUpdateAvailable: Boolean = false,
    sleepTimerRemainingSeconds: Int? = null,
    onSearchQueryChange: (String) -> Unit,
    onToggleGridView: () -> Unit,
    onToggleTheme: () -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenAdmin: () -> Unit,
    onCheckUpdate: () -> Unit = {},
    onOpenSleepTimer: () -> Unit = {},
    onOpenRecordings: () -> Unit = {},
    onOpenEqualizer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showSearchField by remember { mutableStateOf(false) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showSearchField = !showSearchField }
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TvLiveRed,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = "TV",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = strings.appName,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Actions Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Search Toggle
                IconButton(
                    onClick = { showSearchField = !showSearchField },
                    modifier = Modifier.testTag("top_bar_search_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (showSearchField || searchQuery.isNotBlank()) TvSkyBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Grid / List Toggle
                IconButton(
                    onClick = onToggleGridView,
                    modifier = Modifier.testTag("top_bar_grid_toggle")
                ) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                        contentDescription = "Toggle Grid/List",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Theme Mode Switch
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.testTag("top_bar_theme_btn")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Language Switcher Dropdown
                Box {
                    IconButton(
                        onClick = { showLanguageMenu = true },
                        modifier = Modifier.testTag("top_bar_language_btn")
                    ) {
                        Text(
                            text = currentLanguage.flag,
                            fontSize = 18.sp
                        )
                    }

                    DropdownMenu(
                        expanded = showLanguageMenu,
                        onDismissRequest = { showLanguageMenu = false }
                    ) {
                        AppLanguage.values().forEach { lang ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = lang.flag, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = lang.displayName,
                                            fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal,
                                            color = if (lang == currentLanguage) TvSkyBlue else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                onClick = {
                                    onSelectLanguage(lang)
                                    showLanguageMenu = false
                                }
                            )
                        }
                    }
                }

                // Cloud DVR Recordings Button
                IconButton(
                    onClick = onOpenRecordings,
                    modifier = Modifier.testTag("top_bar_dvr_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = strings.recordings,
                        tint = TvSkyBlue
                    )
                }

                // Audio Equalizer FX Button
                IconButton(
                    onClick = onOpenEqualizer,
                    modifier = Modifier.testTag("top_bar_equalizer_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = strings.soundFx,
                        tint = TvSkyBlue
                    )
                }

                // Notifications Bell with Badge
                IconButton(
                    onClick = onOpenNotifications,
                    modifier = Modifier.testTag("top_bar_notifications_btn")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(
                                    containerColor = TvLiveRed,
                                    contentColor = Color.White
                                ) {
                                    Text(text = if (unreadCount > 9) "9+" else unreadCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = strings.notifications,
                            tint = if (unreadCount > 0) TvSkyBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // App Update Button with Badge
                IconButton(
                    onClick = onCheckUpdate,
                    modifier = Modifier.testTag("top_bar_update_btn")
                ) {
                    BadgedBox(
                        badge = {
                            if (hasUpdateAvailable) {
                                Badge(
                                    containerColor = TvLiveRed,
                                    contentColor = Color.White
                                ) {
                                    Text("!")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = strings.checkForUpdates,
                            tint = if (hasUpdateAvailable) TvLiveRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Admin Panel Access
                IconButton(
                    onClick = onOpenAdmin,
                    modifier = Modifier.testTag("top_bar_admin_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = strings.admin,
                        tint = TvSkyBlue
                    )
                }
            }
        }

        // Expandable Search Bar
        AnimatedVisibility(visible = showSearchField) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = strings.searchPlaceholder,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TvSkyBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("search_text_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TvSkyBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                )
            }
        }
    }
}
