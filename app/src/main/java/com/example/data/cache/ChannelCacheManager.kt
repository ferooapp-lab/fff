package com.example.data.cache

import com.example.data.model.Channel
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class ChannelCacheManager {
    private val memoryCache = ConcurrentHashMap<String, Channel>()
    private val categoryCache = ConcurrentHashMap<String, List<Channel>>()
    private val hitCount = AtomicInteger(0)
    private val missCount = AtomicInteger(0)
    private var lastUpdatedTime: Long = System.currentTimeMillis()

    fun get(channelId: String): Channel? {
        val cached = memoryCache[channelId]
        if (cached != null) {
            hitCount.incrementAndGet()
        } else {
            missCount.incrementAndGet()
        }
        return cached
    }

    fun put(channel: Channel) {
        memoryCache[channel.id] = channel
        lastUpdatedTime = System.currentTimeMillis()
    }

    fun putAll(channels: List<Channel>) {
        memoryCache.clear()
        categoryCache.clear()
        channels.forEach { memoryCache[it.id] = it }
        lastUpdatedTime = System.currentTimeMillis()
    }

    fun getByCategory(category: String): List<Channel>? {
        val cached = categoryCache[category]
        if (cached != null) {
            hitCount.incrementAndGet()
            return cached
        }
        val filtered = memoryCache.values.filter {
            if (category == "all" || category == "Tümü") true else it.category.equals(category, ignoreCase = true)
        }
        if (filtered.isNotEmpty()) {
            categoryCache[category] = filtered
            hitCount.incrementAndGet()
            return filtered
        }
        missCount.incrementAndGet()
        return null
    }

    fun getAll(): List<Channel> {
        if (memoryCache.isNotEmpty()) {
            hitCount.incrementAndGet()
            return memoryCache.values.toList()
        }
        missCount.incrementAndGet()
        return emptyList()
    }

    fun clear() {
        memoryCache.clear()
        categoryCache.clear()
        lastUpdatedTime = System.currentTimeMillis()
    }

    fun getMetrics(): CacheMetrics {
        val hits = hitCount.get()
        val misses = missCount.get()
        val total = hits + misses
        val ratio = if (total > 0) (hits.toFloat() / total * 100f) else 100f
        val approxSizeBytes = memoryCache.size * 512L
        return CacheMetrics(
            itemCount = memoryCache.size,
            hitCount = hits,
            missCount = misses,
            hitRatePercent = String.format(java.util.Locale.US, "%.1f", ratio).toFloat(),
            approxSizeKb = (approxSizeBytes / 1024L).toInt(),
            lastUpdated = lastUpdatedTime
        )
    }
}

data class CacheMetrics(
    val itemCount: Int,
    val hitCount: Int,
    val missCount: Int,
    val hitRatePercent: Float,
    val approxSizeKb: Int,
    val lastUpdated: Long
)
