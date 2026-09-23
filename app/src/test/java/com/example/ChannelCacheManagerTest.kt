package com.example

import com.example.data.cache.ChannelCacheManager
import com.example.data.model.Channel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChannelCacheManagerTest {

    private lateinit var cache: ChannelCacheManager

    @Before
    fun setup() {
        cache = ChannelCacheManager()
    }

    @Test
    fun testCachePutAndGet() {
        val sampleChannel = Channel(
            id = "test_ch_1",
            name = "Test Kanalı",
            category = "Haber",
            streamUrl = "https://example.com/test.m3u8"
        )

        cache.put(sampleChannel)
        val retrieved = cache.get("test_ch_1")

        assertNotNull(retrieved)
        assertEquals("Test Kanalı", retrieved?.name)
    }

    @Test
    fun testCacheClear() {
        val sampleChannel = Channel(
            id = "test_ch_2",
            name = "Spor TV",
            category = "Spor",
            streamUrl = "https://example.com/sport.m3u8"
        )

        cache.put(sampleChannel)
        cache.clear()

        val retrieved = cache.get("test_ch_2")
        assertNull(retrieved)
    }

    @Test
    fun testCacheMetrics() {
        val sampleChannel = Channel(
            id = "test_ch_3",
            name = "TRT 1",
            category = "Ulusal",
            streamUrl = "https://example.com/trt1.m3u8"
        )

        cache.put(sampleChannel)
        cache.get("test_ch_3") // Hit
        cache.get("non_existent") // Miss

        val metrics = cache.getMetrics()
        assertEquals(1, metrics.hitCount)
        assertEquals(1, metrics.missCount)
        assertEquals(50f, metrics.hitRatePercent, 0.1f)
        assertTrue(metrics.itemCount >= 1)
    }
}
