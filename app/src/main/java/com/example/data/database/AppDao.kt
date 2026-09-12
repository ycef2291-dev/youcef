package com.example.data.database

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // User Queries
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getLoggedInUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUserSync(): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE contact = :contact LIMIT 1")
    suspend fun getUserByContact(contact: String): UserEntity?

    @Query("SELECT * FROM users WHERE uniqueId = :uniqueId LIMIT 1")
    suspend fun getUserByUniqueId(uniqueId: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR uniqueId LIKE '%' || :query || '%' OR displayName LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    @Query("UPDATE users SET isLoggedIn = 1 WHERE id = :userId")
    suspend fun setLoggedInUser(userId: Long)

    // Room Queries
    @Query("SELECT * FROM rooms ORDER BY id DESC")
    fun getAllRooms(): Flow<List<RoomEntity>>

    @Query("SELECT * FROM rooms WHERE id = :roomId LIMIT 1")
    fun getRoomById(roomId: Long): Flow<RoomEntity?>

    @Query("SELECT * FROM rooms WHERE id = :roomId LIMIT 1")
    suspend fun getRoomByIdSync(roomId: Long): RoomEntity?

    @Query("SELECT * FROM rooms WHERE ownerId = :ownerId LIMIT 1")
    suspend fun getRoomByOwnerId(ownerId: Long): RoomEntity?

    @Query("SELECT * FROM rooms WHERE name LIKE '%' || :query || '%' OR roomIdString LIKE '%' || :query || '%'")
    fun searchRooms(query: String): Flow<List<RoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity): Long

    @Update
    suspend fun updateRoom(room: RoomEntity)

    @Query("UPDATE rooms SET tapCount = tapCount + 1 WHERE id = :roomId")
    suspend fun incrementRoomTap(roomId: Long)

    @Query("UPDATE rooms SET youtubeUrl = :youtubeUrl WHERE id = :roomId")
    suspend fun updateRoomYoutubeUrl(roomId: Long, youtubeUrl: String)

    @Query("UPDATE rooms SET isTvOn = :isTvOn WHERE id = :roomId")
    suspend fun updateRoomTvPower(roomId: Long, isTvOn: Boolean)

    // Message Queries
    @Query("SELECT * FROM messages WHERE category = :category ORDER BY timestamp DESC")
    fun getMessagesByCategory(category: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    // Room Message Queries
    @Query("SELECT * FROM room_messages WHERE roomId = :roomId ORDER BY timestamp ASC")
    fun getRoomMessages(roomId: Long): Flow<List<RoomMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoomMessage(roomMessage: RoomMessageEntity): Long

    // Task Queries
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY id ASC")
    fun getTasksForUser(userId: Long): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    // Badge Queries
    @Query("SELECT * FROM badges WHERE userId = :userId ORDER BY id ASC")
    fun getBadgesForUser(userId: Long): Flow<List<BadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<BadgeEntity>)

    @Update
    suspend fun updateBadge(badge: BadgeEntity)

    // Sequential IDs & Counts
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT COUNT(*) FROM rooms")
    suspend fun getRoomCount(): Int

    @Query("DELETE FROM rooms WHERE roomIdString LIKE 'CK-%'")
    suspend fun deleteFakeRooms()

    @Query("UPDATE users SET allowPrivateMessages = :allowPrivateMessages, allowFriendRequests = :allowFriendRequests WHERE id = :userId")
    suspend fun updatePrivacySettings(userId: Long, allowPrivateMessages: Boolean, allowFriendRequests: Boolean)

    @Query("UPDATE users SET walletCoins = walletCoins + :coins, lastDailyRewardDate = :date, dailyRewardDayOfWeek = :dayOfWeek WHERE id = :userId")
    suspend fun claimDailyReward(userId: Long, coins: Int, date: String, dayOfWeek: Int)

    @Query("UPDATE users SET friendsList = CASE WHEN friendsList = '' THEN :friendId ELSE friendsList || ',' || :friendId END WHERE id = :userId")
    suspend fun addFriend(userId: Long, friendId: String)
}
