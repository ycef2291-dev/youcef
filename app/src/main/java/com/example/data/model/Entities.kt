package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uniqueId: String,
    val username: String,
    val password: String,
    val contact: String, // email or phone
    val contactType: String, // "GMAIL" or "PHONE"
    val displayName: String,
    val avatarUrl: String,
    val bio: String = "أهلاً بكم في حسابي على كليكا! 🎙️✨",
    val level: Int = 1,
    val currentXp: Int = 20,
    val maxXp: Int = 100,
    val walletCoins: Int = 2500,
    val walletDiamonds: Int = 120,
    val isLoggedIn: Boolean = false,
    val isVip: Boolean = false,
    val vipExpireTimestamp: Long = 0,
    val avatarFrame: String = "", // "crown", "rainbow", "butterfly", "kiss", or ""
    val isAdmin: Boolean = false,
    val specialIdExpiry: Long = 0,
    val originalId: String = "",
    val allowPrivateMessages: Boolean = true,
    val allowFriendRequests: Boolean = true,
    val friendsList: String = "", // Comma-separated user IDs
    val lastDailyRewardDate: String = "", // "yyyy-MM-dd"
    val dailyRewardDayOfWeek: Int = 0 // 1 to 7
)

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomIdString: String,
    val name: String,
    val imageUrl: String,
    val ownerId: Long,
    val ownerName: String,
    val youtubeUrl: String = "https://www.youtube.com/watch?v=5qap5aO4i9A",
    val isLive: Boolean = true,
    val viewerCount: Int = 142,
    val tapCount: Int = 1250,
    val category: String = "موسيقى ودردشة",
    val isVip: Boolean = false,
    val isLocked: Boolean = false,
    val pinCode: String = "", // 4-digit PIN lock
    val specialIdExpiry: Long = 0,
    val hasCustomYoutubeTv: Boolean = true,
    val isTvOn: Boolean = false
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // "private", "admin", "group"
    val senderId: Long,
    val senderName: String,
    val senderAvatar: String,
    val recipientId: Long = 0,
    val groupTitle: String = "",
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "room_messages")
data class RoomMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomId: Long,
    val senderId: Long,
    val senderName: String,
    val senderAvatar: String,
    val content: String,
    val isGift: Boolean = false,
    val giftName: String? = null,
    val giftIcon: String? = null,
    val giftId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val description: String,
    val rewardXp: Int,
    val currentProgress: Int,
    val maxProgress: Int,
    val isClaimed: Boolean = false
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val description: String,
    val iconName: String,
    val colorHex: Long,
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null
)

data class MicSeatState(
    val slotIndex: Int,
    val userId: Long? = null,
    val userName: String? = null,
    val userAvatar: String? = null,
    val isMuted: Boolean = false,
    val isSpeaking: Boolean = false,
    val isVip: Boolean = false,
    val isAdmin: Boolean = false,
    val avatarFrame: String = ""
)

data class GiftItem(
    val id: String,
    val name: String,
    val emoji: String,
    val coinPrice: Int,
    val xpReward: Int
)

data class StoreItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "ID_USER", "ID_ROOM", "FRAME", "VIP", "PIN_LOCK", "YOUTUBE_TV"
    val originalPrice: Int,
    val emoji: String,
    val metadataValue: String = ""
)
