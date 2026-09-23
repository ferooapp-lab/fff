package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TvDao {
    // Channels
    @Query("SELECT * FROM channels ORDER BY sortOrder ASC, name ASC")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE isActive = 1 ORDER BY sortOrder ASC, name ASC")
    fun getActiveChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE isFavorite = 1 AND isActive = 1 ORDER BY sortOrder ASC")
    fun getFavoriteChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE category = :category AND isActive = 1 ORDER BY sortOrder ASC")
    fun getChannelsByCategory(category: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE id = :id LIMIT 1")
    fun getChannelById(id: String): Flow<ChannelEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)

    @Update
    suspend fun updateChannel(channel: ChannelEntity)

    @Query("DELETE FROM channels WHERE id = :id")
    suspend fun deleteChannelById(id: String)

    @Query("UPDATE channels SET isFavorite = :isFav WHERE id = :channelId")
    suspend fun setFavorite(channelId: String, isFav: Boolean)

    @Query("UPDATE channels SET isActive = :isActive WHERE id = :channelId")
    suspend fun setActiveStatus(channelId: String, isActive: Boolean)

    @Query("SELECT COUNT(*) FROM channels")
    suspend fun getChannelsCount(): Int

    // Watch History
    @Query("SELECT * FROM watch_history ORDER BY timestamp DESC LIMIT 100")
    fun getWatchHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchHistory(item: HistoryEntity)

    @Query("DELETE FROM watch_history WHERE id = :id")
    suspend fun deleteHistoryItem(id: Long)

    @Query("DELETE FROM watch_history")
    suspend fun clearWatchHistory()

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC LIMIT 50")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(item: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    // Live Chat Messages
    @Query("SELECT * FROM chat_messages WHERE channelId = :channelId OR channelId = 'all' ORDER BY timestamp ASC LIMIT 200")
    fun getChatMessages(channelId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC LIMIT 200")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessages(messages: List<ChatMessageEntity>)

    @Query("UPDATE chat_messages SET likesCount = likesCount + 1, isLikedByUser = 1 WHERE id = :id")
    suspend fun likeChatMessage(id: Long)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteChatMessage(id: Long)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()

    // Community Polls
    @Query("SELECT * FROM community_polls WHERE channelId = :channelId OR channelId = 'all' ORDER BY isActive DESC LIMIT 10")
    fun getPolls(channelId: String): Flow<List<CommunityPollEntity>>

    @Query("SELECT * FROM community_polls ORDER BY isActive DESC LIMIT 10")
    fun getAllPolls(): Flow<List<CommunityPollEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoll(poll: CommunityPollEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolls(polls: List<CommunityPollEntity>)

    @Query("UPDATE community_polls SET votes1 = votes1 + 1, userVotedOption = 1 WHERE id = :pollId")
    suspend fun votePollOption1(pollId: String)

    @Query("UPDATE community_polls SET votes2 = votes2 + 1, userVotedOption = 2 WHERE id = :pollId")
    suspend fun votePollOption2(pollId: String)

    @Query("UPDATE community_polls SET votes3 = votes3 + 1, userVotedOption = 3 WHERE id = :pollId")
    suspend fun votePollOption3(pollId: String)
}
