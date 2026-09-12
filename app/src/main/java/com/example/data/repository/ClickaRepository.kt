package com.example.data.repository

import android.content.Context
import com.example.data.database.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class ClickaRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.appDao()

    val loggedInUser: Flow<UserEntity?> = dao.getLoggedInUser()
    val allRooms: Flow<List<RoomEntity>> = dao.getAllRooms()

    suspend fun getLoggedInUserSync(): UserEntity? = withContext(Dispatchers.IO) {
        dao.getLoggedInUserSync()
    }

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        // Delete any fake CK- rooms if present
        dao.deleteFakeRooms()

        // Seed account: Youcef / ID 55555 / 123123321321 / Rich funds / Admin rights
        var adminUser = dao.getUserByUsername("Youcef")
        if (adminUser == null) {
            adminUser = dao.getUserByUsername("يوسف")
        }
        if (adminUser == null) {
            adminUser = dao.getUserByUniqueId("55555")
        }

        if (adminUser == null) {
            val userYoussef = UserEntity(
                uniqueId = "55555",
                originalId = "55555",
                username = "Youcef",
                password = "123123321321",
                contact = "+213793474663",
                contactType = "PHONE",
                displayName = "Youcef",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                bio = "الحساب الرسمي لمدير تطبيق كليكا 🎙️✨",
                level = 10,
                currentXp = 500,
                maxXp = 1000,
                walletCoins = 5000000,
                walletDiamonds = 1000000,
                isLoggedIn = false,
                isAdmin = true,
                isVip = true,
                avatarFrame = "crown"
            )
            val youssefId = dao.insertUser(userYoussef)

            // Seed Room for Youcef
            val youssefRoom = RoomEntity(
                roomIdString = "55555",
                name = "غرفة Youcef الملكية 🎙️",
                imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
                ownerId = youssefId,
                ownerName = "Youcef",
                youtubeUrl = "https://www.youtube.com/watch?v=5qap5aO4i9A",
                isLive = true,
                isTvOn = true,
                viewerCount = 38,
                tapCount = 1420,
                category = "حوار وموسيقى",
                isVip = true,
                hasCustomYoutubeTv = true
            )
            dao.insertRoom(youssefRoom)
        } else {
            // Update existing user with updated Youcef credentials & rich balance
            val updated = adminUser.copy(
                uniqueId = "55555",
                originalId = "55555",
                username = "Youcef",
                displayName = "Youcef",
                password = "123123321321",
                walletCoins = adminUser.walletCoins.coerceAtLeast(5000000),
                walletDiamonds = adminUser.walletDiamonds.coerceAtLeast(1000000),
                isAdmin = true,
                isVip = true
            )
            dao.updateUser(updated)
        }

        // Seed Admin / Welcome Messages if message table is empty
        val existingRooms = dao.getRoomByIdSync(1)
        if (existingRooms == null) {
            // Seed Admin Messages
            dao.insertMessage(
                MessageEntity(
                    category = "admin",
                    senderId = 0,
                    senderName = "إدارة كليكا 🌟",
                    senderAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200",
                    content = "مرحباً بك في تطبيق كليكا! الشحن بالدينار الجزائري متوفر عبر فليكسي والمتجر متاح في صفحة حسابي.",
                    timestamp = System.currentTimeMillis() - 3600000,
                    isRead = false
                )
            )
            dao.insertMessage(
                MessageEntity(
                    category = "admin",
                    senderId = 0,
                    senderName = "إدارة كليكا الرسمية 🛡️",
                    senderAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200",
                    content = "نصيحة أمان: لا تشارك كلمة المرور الخاصة بحسابك مع أي شخص. فريق الإدارة لن يطلبها أبداً.",
                    timestamp = System.currentTimeMillis() - 86400000,
                    isRead = true
                )
            )

            // Seed Group Messages
            dao.insertMessage(
                MessageEntity(
                    category = "group",
                    senderId = 999,
                    senderName = "أحمد فهد",
                    senderAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                    groupTitle = "مجتمع كليكا الذهبي 🌟",
                    content = "السلام عليكم جميعاً! البث المباشر سيبدأ الليلة في تمام التاسعة 🎙️",
                    timestamp = System.currentTimeMillis() - 1800000,
                    isRead = false
                )
            )

            // Seed Private Messages
            dao.insertMessage(
                MessageEntity(
                    category = "private",
                    senderId = 999,
                    senderName = "أمين الجزائري",
                    senderAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200",
                    content = "أهلاً بك يا غالي! تشرفت بوجودك في روم كليكا اليوم 🌸",
                    timestamp = System.currentTimeMillis() - 600000,
                    isRead = false
                )
            )
        }
    }

    sealed class AuthResult {
        data class Success(val user: UserEntity) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }

    suspend fun registerUser(
        username: String,
        password: String,
        contact: String,
        isGmail: Boolean
    ): AuthResult = withContext(Dispatchers.IO) {
        val trimmedUsername = username.trim()
        val trimmedContact = contact.trim()
        val trimmedPassword = password.trim()

        if (trimmedUsername.isBlank()) return@withContext AuthResult.Error("يرجى إدخال اسم المستخدم")
        if (trimmedPassword.length < 4) return@withContext AuthResult.Error("كلمة السر يجب أن لا تقل عن 4 أحرف")
        if (trimmedContact.isBlank()) {
            return@withContext AuthResult.Error(if (isGmail) "يرجى إدخال بريد الجيميل" else "يرجى إدخال رقم الهاتف")
        }

        // Validate format
        if (isGmail && (!trimmedContact.contains("@") || !trimmedContact.contains("."))) {
            return@withContext AuthResult.Error("يرجى إدخال بريد جيميل صالح (example@gmail.com)")
        }
        if (!isGmail && trimmedContact.length < 8) {
            return@withContext AuthResult.Error("يرجى إدخال رقم هاتف صالح")
        }

        // 1. Check if username already exists
        val existingUsername = dao.getUserByUsername(trimmedUsername)
        if (existingUsername != null) {
            return@withContext AuthResult.Error("اسم المستخدم موجود داخل التطبيق مسبقاً، يرجى اختيار اسم آخر")
        }

        // 2. Check if contact (email or phone) already exists
        val existingContact = dao.getUserByContact(trimmedContact)
        if (existingContact != null) {
            return@withContext AuthResult.Error(
                if (isGmail) "لا يمكن إنشاء حساب آخر بنفس الجيميل، البريد مسجل مسبقاً"
                else "لا يمكن إنشاء حساب آخر بنفس رقم الهاتف، الرقم مسجل مسبقاً"
            )
        }

        // Logout all previous accounts
        dao.logoutAllUsers()

        // Sequential 5-digit user ID based on creation order
        val userCount = dao.getUserCount()
        val sequentialId = String.format(Locale.US, "%05d", 10001 + userCount)
        val avatarPresets = listOf(
            "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200",
            "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200",
            "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=200"
        )
        val randomAvatar = avatarPresets.random()

        val newUser = UserEntity(
            uniqueId = sequentialId,
            originalId = sequentialId,
            username = trimmedUsername,
            password = trimmedPassword,
            contact = trimmedContact,
            contactType = if (isGmail) "GMAIL" else "PHONE",
            displayName = trimmedUsername,
            avatarUrl = randomAvatar,
            level = 1,
            currentXp = 25,
            maxXp = 100,
            walletCoins = 3000,
            walletDiamonds = 100,
            isLoggedIn = true,
            isAdmin = false,
            allowPrivateMessages = true,
            allowFriendRequests = true
        )

        val newId = dao.insertUser(newUser)
        val createdUser = newUser.copy(id = newId)

        // Seed Tasks for new user
        val initialTasks = listOf(
            TaskEntity(
                userId = newId,
                title = "تسجيل الدخول اليومي",
                description = "افتح التطبيق يومياً لتحصل على نقاط خبرة",
                rewardXp = 25,
                currentProgress = 1,
                maxProgress = 1,
                isClaimed = false
            ),
            TaskEntity(
                userId = newId,
                title = "التكبيس في الغرفة",
                description = "كبس 30 مرة في أي غرفة نشطة لدعم المضيف",
                rewardXp = 30,
                currentProgress = 0,
                maxProgress = 30,
                isClaimed = false
            ),
            TaskEntity(
                userId = newId,
                title = "إرسال هدية للمضيف",
                description = "أرسل أي هدية داخل الغرفة لمساندة الأصدقاء",
                rewardXp = 40,
                currentProgress = 0,
                maxProgress = 1,
                isClaimed = false
            ),
            TaskEntity(
                userId = newId,
                title = "الصعود على الميكروفون",
                description = "تحدث على المايك لمدة دقيقة في أي غرفة صوتية",
                rewardXp = 35,
                currentProgress = 0,
                maxProgress = 1,
                isClaimed = false
            ),
            TaskEntity(
                userId = newId,
                title = "مشاهدة التلفاز مع الأصدقاء",
                description = "استمتع بمشاهدة مقطع يوتيوب على شاشة الغرفة",
                rewardXp = 20,
                currentProgress = 0,
                maxProgress = 1,
                isClaimed = false
            )
        )
        dao.insertTasks(initialTasks)

        // Seed Badges for new user
        val initialBadges = listOf(
            BadgeEntity(
                userId = newId,
                title = "عضو جديد في كليكا 🌟",
                description = "انضم إلى عائلة كليكا المميزة",
                iconName = "star",
                colorHex = 0xFFFFD700,
                isUnlocked = true,
                unlockedDate = "اليوم"
            ),
            BadgeEntity(
                userId = newId,
                title = "وسام الميكروفون الذهبي 🎙️",
                description = "شارك في المحادثات الصوتية بالرومات",
                iconName = "mic",
                colorHex = 0xFFFF7043,
                isUnlocked = false
            ),
            BadgeEntity(
                userId = newId,
                title = "وسام الداعم الملكي 👑",
                description = "أرسل هدايا في الغرف التفاعلية",
                iconName = "crown",
                colorHex = 0xFFBA68C8,
                isUnlocked = false
            ),
            BadgeEntity(
                userId = newId,
                title = "وسام صانع الرومات 🏰",
                description = "أنشئ غرفتك الخاصة وادعُ أصدقاءك",
                iconName = "castle",
                colorHex = 0xFF4CAF50,
                isUnlocked = false
            ),
            BadgeEntity(
                userId = newId,
                title = "وسام النجم الصاعد ⭐",
                description = "ارتقِ للمستوى 5 في كليكا",
                iconName = "trophy",
                colorHex = 0xFF00E5FF,
                isUnlocked = false
            )
        )
        dao.insertBadges(initialBadges)

        AuthResult.Success(createdUser)
    }

    suspend fun loginUser(usernameOrContact: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val trimmedIdentifier = usernameOrContact.trim()
        val trimmedPassword = password.trim()

        if (trimmedIdentifier.isBlank() || trimmedPassword.isBlank()) {
            return@withContext AuthResult.Error("يرجى إدخال الاسم وكلمة السر")
        }

        // Try username, contact, or uniqueId
        var user = dao.getUserByUsername(trimmedIdentifier)
        if (user == null) {
            user = dao.getUserByContact(trimmedIdentifier)
        }
        if (user == null) {
            user = dao.getUserByUniqueId(trimmedIdentifier)
        }

        // Check for special manager account creation if not yet initialized
        val isManagerLogin = (trimmedIdentifier.equals("Youcef", ignoreCase = true) ||
                trimmedIdentifier.equals("Youssef", ignoreCase = true) ||
                trimmedIdentifier == "يوسف" ||
                trimmedIdentifier == "55555")
        if (user == null && isManagerLogin && trimmedPassword == "123123321321") {
            initializeSeedDataIfNeeded()
            user = dao.getUserByUsername("Youcef") ?: dao.getUserByUniqueId("55555") ?: dao.getUserByUsername("يوسف")
        }

        if (user == null) {
            return@withContext AuthResult.Error("اسم المستخدم أو كلمة السر غير صحيحة")
        }

        if (user.password != trimmedPassword) {
            return@withContext AuthResult.Error("كلمة السر غير صحيحة، يرجى المحاولة مجدداً")
        }

        dao.logoutAllUsers()
        dao.setLoggedInUser(user.id)
        AuthResult.Success(user.copy(isLoggedIn = true))
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        dao.logoutAllUsers()
    }

    // Room operations
    sealed class CreateRoomResult {
        data class Success(val room: RoomEntity) : CreateRoomResult()
        data class Error(val message: String, val existingRoom: RoomEntity? = null) : CreateRoomResult()
    }

    suspend fun createRoom(
        userId: Long,
        userName: String,
        roomName: String,
        roomImageUrl: String
    ): CreateRoomResult = withContext(Dispatchers.IO) {
        val trimmedName = roomName.trim()
        if (trimmedName.isBlank()) {
            return@withContext CreateRoomResult.Error("يرجى كتابة اسم الغرفة")
        }

        // Constraint: User can only create ONE room
        val existingRoom = dao.getRoomByOwnerId(userId)
        if (existingRoom != null) {
            return@withContext CreateRoomResult.Error(
                "يمكنك إنشاء غرفة واحدة فقط! لديك غرفة سابقة بعنوان: ${existingRoom.name}",
                existingRoom
            )
        }

        val user = dao.getUserById(userId)
        val roomCount = dao.getRoomCount()
        val sequentialRoomId = String.format(Locale.US, "%05d", 20001 + roomCount)
        val defaultImage = if (roomImageUrl.isNotBlank()) roomImageUrl else "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=400"

        val room = RoomEntity(
            roomIdString = sequentialRoomId,
            name = trimmedName,
            imageUrl = defaultImage,
            ownerId = userId,
            ownerName = userName,
            youtubeUrl = "https://www.youtube.com/watch?v=5qap5aO4i9A",
            isLive = true,
            viewerCount = 1,
            tapCount = 0,
            category = "غرفتي الخاصة",
            isVip = user?.isVip ?: false
        )
        val insertedId = dao.insertRoom(room)

        CreateRoomResult.Success(room.copy(id = insertedId))
    }

    suspend fun getUserRoom(userId: Long): RoomEntity? = withContext(Dispatchers.IO) {
        dao.getRoomByOwnerId(userId)
    }

    fun getRoom(roomId: Long): Flow<RoomEntity?> = dao.getRoomById(roomId)

    suspend fun updateRoomYoutubeUrl(roomId: Long, url: String) = withContext(Dispatchers.IO) {
        dao.updateRoomYoutubeUrl(roomId, url)
    }

    suspend fun tapRoom(roomId: Long, userId: Long) = withContext(Dispatchers.IO) {
        dao.incrementRoomTap(roomId)
    }

    // Messages
    fun getMessagesByCategory(category: String): Flow<List<MessageEntity>> =
        dao.getMessagesByCategory(category)

    suspend fun sendMessage(
        category: String,
        senderId: Long,
        senderName: String,
        senderAvatar: String,
        content: String,
        groupTitle: String = ""
    ) = withContext(Dispatchers.IO) {
        dao.insertMessage(
            MessageEntity(
                category = category,
                senderId = senderId,
                senderName = senderName,
                senderAvatar = senderAvatar,
                content = content,
                groupTitle = groupTitle,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // Room Chat & Gifts
    fun getRoomMessages(roomId: Long): Flow<List<RoomMessageEntity>> =
        dao.getRoomMessages(roomId)

    suspend fun sendRoomChatMessage(
        roomId: Long,
        senderId: Long,
        senderName: String,
        senderAvatar: String,
        content: String
    ) = withContext(Dispatchers.IO) {
        dao.insertRoomMessage(
            RoomMessageEntity(
                roomId = roomId,
                senderId = senderId,
                senderName = senderName,
                senderAvatar = senderAvatar,
                content = content
            )
        )
    }

    suspend fun sendRoomGift(
        roomId: Long,
        sender: UserEntity,
        gift: GiftItem
    ): Boolean = withContext(Dispatchers.IO) {
        // Apply 20% discount if user is VIP
        val actualPrice = if (sender.isVip) (gift.coinPrice * 0.80).toInt() else gift.coinPrice
        if (sender.walletCoins < actualPrice) {
            return@withContext false
        }

        // Deduct coins & add XP
        val updatedCoins = sender.walletCoins - actualPrice
        var newXp = sender.currentXp + gift.xpReward
        var newLevel = sender.level
        var newMaxXp = sender.maxXp

        while (newXp >= newMaxXp) {
            newXp -= newMaxXp
            newLevel += 1
            newMaxXp += 50
        }

        val updatedUser = sender.copy(
            walletCoins = updatedCoins,
            currentXp = newXp,
            level = newLevel,
            maxXp = newMaxXp
        )
        dao.updateUser(updatedUser)

        val discountText = if (sender.isVip) " (خصم VIP 20%)" else ""
        dao.insertRoomMessage(
            RoomMessageEntity(
                roomId = roomId,
                senderId = sender.id,
                senderName = sender.displayName,
                senderAvatar = sender.avatarUrl,
                content = "أرسل ${gift.emoji} ${gift.name} للمضيف بقيمة $actualPrice عملة!$discountText",
                isGift = true,
                giftName = gift.name,
                giftIcon = gift.emoji,
                giftId = gift.id
            )
        )
        true
    }

    suspend fun updateRoomTvPower(roomId: Long, isTvOn: Boolean) = withContext(Dispatchers.IO) {
        dao.updateRoomTvPower(roomId, isTvOn)
    }

    // Tasks and Level
    fun getUserTasks(userId: Long): Flow<List<TaskEntity>> = dao.getTasksForUser(userId)
    fun getUserBadges(userId: Long): Flow<List<BadgeEntity>> = dao.getBadgesForUser(userId)

    suspend fun claimTask(user: UserEntity, task: TaskEntity) = withContext(Dispatchers.IO) {
        if (task.isClaimed) return@withContext

        val updatedTask = task.copy(isClaimed = true)
        dao.updateTask(updatedTask)

        var newXp = user.currentXp + task.rewardXp
        var newLevel = user.level
        var newMaxXp = user.maxXp

        while (newXp >= newMaxXp) {
            newXp -= newMaxXp
            newLevel += 1
            newMaxXp += 50
        }

        dao.updateUser(user.copy(level = newLevel, currentXp = newXp, maxXp = newMaxXp))
    }

    suspend fun updateUserProfile(user: UserEntity) = withContext(Dispatchers.IO) {
        dao.updateUser(user)
    }

    fun searchUsers(query: String): Flow<List<UserEntity>> = dao.searchUsers(query)
    fun searchRooms(query: String): Flow<List<RoomEntity>> = dao.searchRooms(query)

    // ==========================================
    // STORE & FLEXY RECHARGE (متجر وشحن كليكا)
    // ==========================================

    sealed class StoreResult {
        data class Success(val message: String, val updatedUser: UserEntity) : StoreResult()
        data class Error(val message: String) : StoreResult()
    }

    suspend fun rechargeFlexyCoins(
        user: UserEntity,
        coinsToAdd: Int,
        dzdPrice: Int,
        senderPhone: String
    ): UserEntity = withContext(Dispatchers.IO) {
        val updatedUser = user.copy(walletCoins = user.walletCoins + coinsToAdd)
        dao.updateUser(updatedUser)

        // Send confirmation admin notification
        dao.insertMessage(
            MessageEntity(
                category = "admin",
                senderId = 0,
                senderName = "إدارة كليكا • شحن فليكسي 📱",
                senderAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200",
                content = "تم بنجاح تأكيد شحن $coinsToAdd عملة مقابل $dzdPrice د.ج عبر فليكسي (الرقم: $senderPhone). رصيدك الجديد جاهز في المحفظة!",
                timestamp = System.currentTimeMillis()
            )
        )
        updatedUser
    }

    suspend fun buySpecialUserId(user: UserEntity, specialId: String): StoreResult = withContext(Dispatchers.IO) {
        val basePrice = 2000
        val finalPrice = if (user.isVip) (basePrice * 0.80).toInt() else basePrice

        if (user.walletCoins < finalPrice) {
            return@withContext StoreResult.Error("رصيد العملات غير كافٍ! تحتاج $finalPrice عملة.")
        }

        val oneWeekMillis = 7L * 24 * 60 * 60 * 1000
        val updated = user.copy(
            walletCoins = user.walletCoins - finalPrice,
            uniqueId = specialId,
            specialIdExpiry = System.currentTimeMillis() + oneWeekMillis,
            originalId = if (user.originalId.isNotBlank()) user.originalId else user.uniqueId
        )
        dao.updateUser(updated)
        StoreResult.Success("تم تفعيل الآيدي المميز $specialId لمدة أسبوع بنجاح! 🎉", updated)
    }

    suspend fun buySpecialRoomId(user: UserEntity, room: RoomEntity, specialId: String): StoreResult = withContext(Dispatchers.IO) {
        val basePrice = 2000
        val finalPrice = if (user.isVip) (basePrice * 0.80).toInt() else basePrice

        if (user.walletCoins < finalPrice) {
            return@withContext StoreResult.Error("رصيد العملات غير كافٍ! تحتاج $finalPrice عملة.")
        }

        val oneWeekMillis = 7L * 24 * 60 * 60 * 1000
        val updatedUser = user.copy(walletCoins = user.walletCoins - finalPrice)
        val updatedRoom = room.copy(
            roomIdString = specialId,
            specialIdExpiry = System.currentTimeMillis() + oneWeekMillis
        )
        dao.updateUser(updatedUser)
        dao.updateRoom(updatedRoom)
        StoreResult.Success("تم تفعيل آيدي الغرفة المميز $specialId لغرفتك لمدة أسبوع! 🎙️", updatedUser)
    }

    suspend fun buyAvatarFrame(user: UserEntity, frameType: String, basePrice: Int, frameTitle: String): StoreResult = withContext(Dispatchers.IO) {
        val finalPrice = if (user.isVip) (basePrice * 0.80).toInt() else basePrice

        if (user.walletCoins < finalPrice) {
            return@withContext StoreResult.Error("رصيد العملات غير كافٍ! سعر الزينة: $finalPrice عملة.")
        }

        val updated = user.copy(
            walletCoins = user.walletCoins - finalPrice,
            avatarFrame = frameType
        )
        dao.updateUser(updated)
        StoreResult.Success("تم شراء وتفعيل زينة $frameTitle على صورتك الشخصية بنجاح! ✨", updated)
    }

    suspend fun buyVip(user: UserEntity): StoreResult = withContext(Dispatchers.IO) {
        val basePrice = 19999
        if (user.walletCoins < basePrice) {
            return@withContext StoreResult.Error("رصيد العملات غير كافٍ لشراء VIP! السعر: $basePrice عملة.")
        }

        val oneWeekMillis = 7L * 24 * 60 * 60 * 1000
        val updated = user.copy(
            walletCoins = user.walletCoins - basePrice,
            isVip = true,
            vipExpireTimestamp = System.currentTimeMillis() + oneWeekMillis
        )
        dao.updateUser(updated)

        // Update user's room to also have VIP if they own one
        val userRoom = dao.getRoomByOwnerId(user.id)
        if (userRoom != null) {
            dao.updateRoom(userRoom.copy(isVip = true))
        }

        StoreResult.Success("مبروك! تم تفعيل اشتراك VIP الملكي لمدة أسبوع مع خصم 20% على جميع المشتريات وشارة VIP! 👑", updated)
    }

    suspend fun buyRoomPinLock(user: UserEntity, room: RoomEntity, pinCode: String): StoreResult = withContext(Dispatchers.IO) {
        val basePrice = 2000
        val finalPrice = if (user.isVip) (basePrice * 0.80).toInt() else basePrice

        if (user.walletCoins < finalPrice) {
            return@withContext StoreResult.Error("رصيد العملات غير كافٍ! سعر قفل الغرفة: $finalPrice عملة.")
        }

        if (pinCode.length != 4 || !pinCode.all { it.isDigit() }) {
            return@withContext StoreResult.Error("رمز القفل يجب أن يتكون من 4 أرقام تماماً!")
        }

        val updatedUser = user.copy(walletCoins = user.walletCoins - finalPrice)
        val updatedRoom = room.copy(isLocked = true, pinCode = pinCode)
        dao.updateUser(updatedUser)
        dao.updateRoom(updatedRoom)

        StoreResult.Success("تم قفل الغرفة برمز PIN المكون من 4 أرقام ($pinCode) بنجاح! 🔒", updatedUser)
    }

    suspend fun unlockOrRemoveRoomPin(room: RoomEntity): RoomEntity = withContext(Dispatchers.IO) {
        val updated = room.copy(isLocked = false, pinCode = "")
        dao.updateRoom(updated)
        updated
    }

    suspend fun claimDailyReward(userId: Long): Pair<Boolean, Int> = withContext(Dispatchers.IO) {
        val user = dao.getUserById(userId) ?: return@withContext Pair(false, 0)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (user.lastDailyRewardDate == today) {
            return@withContext Pair(false, user.walletCoins)
        }
        val nextDay = if (user.dailyRewardDayOfWeek >= 7) 1 else user.dailyRewardDayOfWeek + 1
        dao.claimDailyReward(userId = userId, coins = 5, date = today, dayOfWeek = nextDay)
        Pair(true, user.walletCoins + 5)
    }

    suspend fun updatePrivacySettings(
        userId: Long,
        allowPrivateMessages: Boolean,
        allowFriendRequests: Boolean
    ) = withContext(Dispatchers.IO) {
        dao.updatePrivacySettings(userId, allowPrivateMessages, allowFriendRequests)
    }

    suspend fun addFriend(userId: Long, friendId: String) = withContext(Dispatchers.IO) {
        dao.addFriend(userId, friendId)
    }
}
