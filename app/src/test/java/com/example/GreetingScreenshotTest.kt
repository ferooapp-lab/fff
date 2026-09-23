package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.Channel
import com.example.ui.components.ChannelCard
import com.example.ui.theme.CanliTvTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun channel_card_screenshot() {
    val sample = Channel(
        id = "trt1",
        name = "TRT 1 HD",
        category = "Ulusal",
        streamUrl = "https://example.com/test.mp4",
        currentProgram = "Gönül Dağı",
        nextProgram = "Ana Haber Bülteni",
        epgProgress = 0.65f,
        isFavorite = true
    )

    composeTestRule.setContent {
      CanliTvTheme(darkTheme = true) {
        ChannelCard(
            channel = sample,
            isSelected = true,
            isGridView = false,
            onSelect = {},
            onToggleFavorite = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/channel_card.png")
  }
}
