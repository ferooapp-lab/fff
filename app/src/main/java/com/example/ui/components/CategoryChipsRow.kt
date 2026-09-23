package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.TvSkyBlue

data class CategoryItem(
    val id: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun CategoryChipsRow(
    selectedCategory: String,
    strings: AppStrings,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        CategoryItem("all", strings.allCategories, Icons.Default.GridView),
        CategoryItem("trend", strings.trending, Icons.Default.Whatshot),
        CategoryItem("4k", "4K Ultra HD", Icons.Default.HighQuality),
        CategoryItem("Ulusal", strings.national, Icons.Default.Tv),
        CategoryItem("Haber", strings.news, Icons.Default.Newspaper),
        CategoryItem("Spor", strings.sports, Icons.Default.SportsSoccer),
        CategoryItem("Belgesel", strings.documentary, Icons.Default.MenuBook),
        CategoryItem("Sinema", strings.cinema, Icons.Default.Movie),
        CategoryItem("Çocuk", strings.kids, Icons.Default.ChildCare),
        CategoryItem("Müzik", strings.music, Icons.Default.MusicNote),
        CategoryItem("Eğlence", strings.entertainment, Icons.Default.Videocam)
    )

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_chips_row"),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { item ->
            val isSelected = selectedCategory.equals(item.id, ignoreCase = true) ||
                    (item.id == "all" && (selectedCategory == "all" || selectedCategory == "Tümü"))

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(item.id) },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) TvSkyBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) TvSkyBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    selectedBorderColor = TvSkyBlue
                )
            )
        }
    }
}
