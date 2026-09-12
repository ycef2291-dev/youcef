package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.ClickaRepository
import com.example.ui.components.ActiveGiftAnimation
import com.example.util.GiftSoundPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

enum class AppScreen {
    SPLASH,
    AUTH,
    MAIN,
    LIVE_ROOM
}

enum class BottomTab {
    MESSAGES,
    SEARCH,
    WORLD_ROOMS,
    PROFILE
}

data class FloatingHeart(
    val id: Long,
    val xOffsetFactor: Float, // -1f to 1f
    val colorHex: Long,
    val sizeDp: Int
)

data class FlexyBundle(
    val dzdAmount: Int,
    val coinsAmount: Int,
    val title: String,
    val isPopular: Boolean = false
)

class ClickaViewModel(application: Application) : AndroidViewModel(application) {
    val repository = ClickaRepository(application)

    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _splashSeconds = MutableStateFlow(5)
    val splashSeconds: StateFlow<Int> = _splashSeconds.asStateFlow()

    val loggedInUser: StateFlow<UserEntity?> = repository.loggedInUser.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _currentBottomTab = MutableStateFlow(BottomTab.WORLD_ROOMS)
    val currentBottomTab: StateFlow<BottomTab> = _currentBottomTab.asStateFlow()

    // App Language ("ar" for Arabic, "fr" for French)
    val appLanguage = MutableStateFlow("ar")

    // Auth Form State
    var isRegisterMode = MutableStateFlow(false)
    var authUsername = MutableStateFlow("")
    var authPassword = MutableStateFlow("")
    var authContact = MutableStateFlow("") // email or phone
    var authIsGmail = MutableStateFlow(true)
    var authError = MutableStateFlow<String?>(null)
    var authLoading = MutableStateFlow(false)

    // Active Live Room State
    private val _activeRoomId = MutableStateFlow<Long?>(null)
    val activeRoomId: StateFlow<Long?> = _activeRoomId.asStateFlow()

    val activeRoom: StateFlow<RoomEntity?> = _activeRoomId.flatMapLatest { id ->
        if (id != null) repository.getRoom(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeRoomMessages: StateFlow<List<RoomMessageEntity>> = _activeRoomId.flatMapLatest { id ->
        if (id != null) repository.getRoomMessages(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4 Mic seats in the room
    private val _micSeats = MutableStateFlow(
        listOf(
            MicSeatState(slotIndex = 0, userName = "المضيف", isMuted = false, isSpeaking = true),
            MicSeatState(slotIndex = 1, isMuted = false, isSpeaking = false),
            MicSeatState(slotIndex = 2, isMuted = false, isSpeaking = false),
            MicSeatState(slotIndex = 3, isMuted = false, isSpeaking = false)
        )
    )
    val micSeats: StateFlow<List<MicSeatState>> = _micSeats.asStateFlow()

    // Floating hearts for tapping
    private val _floatingHearts = MutableStateFlow<List<FloatingHeart>>(emptyList())
    val floatingHearts: StateFlow<List<FloatingHeart>> = _floatingHearts.asStateFlow()

    private val _roomTaps = MutableStateFlow(0)
    val roomTaps: StateFlow<Int> = _roomTaps.asStateFlow()

    // Active full-screen TikTok gift animation & sound
    val activeGiftAnimation = MutableStateFlow<ActiveGiftAnimation?>(null)

    fun dismissActiveGiftAnimation() {
        activeGiftAnimation.value = null
    }

    // All active rooms for "حول العالم"
    val allRooms: StateFlow<List<RoomEntity>> = repository.allRooms.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Messages Tab State
    private val _selectedMessagesCategory = MutableStateFlow("private") // "private", "admin", "group"
    val selectedMessagesCategory: StateFlow<String> = _selectedMessagesCategory.asStateFlow()

    val messagesList: StateFlow<List<MessageEntity>> = _selectedMessagesCategory.flatMapLatest { category ->
        repository.getMessagesByCategory(category)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search Tab State
    val searchQuery = MutableStateFlow("")
    val searchType = MutableStateFlow("ALL") // "ALL", "FRIENDS", "ROOMS"

    val searchUsersResult: StateFlow<List<UserEntity>> = searchQuery.flatMapLatest { q ->
        repository.searchUsers(q)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchRoomsResult: StateFlow<List<RoomEntity>> = searchQuery.flatMapLatest { q ->
        repository.searchRooms(q)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Profile tasks and badges
    val userTasks: StateFlow<List<TaskEntity>> = loggedInUser.flatMapLatest { user ->
        if (user != null) repository.getUserTasks(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userBadges: StateFlow<List<BadgeEntity>> = loggedInUser.flatMapLatest { user ->
        if (user != null) repository.getUserBadges(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Available TikTok-style Gifts
    val availableGifts = listOf(
        GiftItem("gift_rose", "وردة كليكا", "🌹", 1, 1),
        GiftItem("gift_finger_heart", "قلب الأصابع", "🫰", 5, 2),
        GiftItem("gift_teddy", "دبدوب كليكا", "🧸", 10, 5),
        GiftItem("gift_perfume", "عطر المشاهير", "🧴", 20, 8),
        GiftItem("gift_shades", "نظارة كول", "🕶️", 99, 25),
        GiftItem("gift_hat", "قبعة الساحر", "🎩", 199, 50),
        GiftItem("gift_swan", "البجعة البيضاء", "🦢", 499, 120),
        GiftItem("gift_money_gun", "مسدس الأموال", "💸", 1000, 250),
        GiftItem("gift_train", "قطار كليكا السريع", "🚅", 1999, 500),
        GiftItem("gift_sports_car", "سيارة فيراري", "🏎️", 2999, 750),
        GiftItem("gift_jet", "طائرة خاصة نفاثة", "✈️", 4999, 1250),
        GiftItem("gift_yacht", "يخت الملوك", "🛥️", 9999, 2500),
        GiftItem("gift_falcon", "الصقر الملكي", "🦅", 19999, 5000),
        GiftItem("gift_lion", "أسد تيك توك الملكي", "🦁", 29999, 8000),
        GiftItem("gift_galaxy", "المجرّة الكونية", "🌌", 34999, 10000)
    )

    // Flexy Pricing Bundles (DZD -> Coins)
    val flexyBundles = listOf(
        FlexyBundle(100, 100, "100 دينار مقابل 100 عملة"),
        FlexyBundle(500, 600, "500 دينار مقابل 600 عملة", isPopular = true),
        FlexyBundle(1000, 1200, "1000 دينار مقابل 1200 عملة"),
        FlexyBundle(2000, 2400, "2000 دينار مقابل 2400 عملة")
    )
    val flexyPhoneNumber = "+213793474663"

    // Dialog Visibilities
    val showCreateRoomDialog = MutableStateFlow(false)
    val showEditProfileDialog = MutableStateFlow(false)
    val showWalletDialog = MutableStateFlow(false)
    val showSettingsDialog = MutableStateFlow(false)
    val showAboutDialog = MutableStateFlow(false)
    val showShareDialog = MutableStateFlow(false)
    val showGiftSheet = MutableStateFlow(false)
    val showTvUrlDialog = MutableStateFlow(false)
    val showStoreDialog = MutableStateFlow(false)
    val showFlexyDialog = MutableStateFlow(false)
    val showDailyRewardDialog = MutableStateFlow(false)
    val selectedFlexyBundle = MutableStateFlow<FlexyBundle?>(null)
    val pendingLockedRoom = MutableStateFlow<RoomEntity?>(null)
    val storeSnackbarMessage = MutableStateFlow<String?>(null)
    val roomErrorMessage = MutableStateFlow<String?>(null)

    private var speakingSimulationJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
            startSplashCountdown()
        }
    }

    private fun startSplashCountdown() {
        viewModelScope.launch {
            for (sec in 5 downTo 1) {
                _splashSeconds.value = sec
                delay(1000)
            }
            // Check if user is already logged in
            val user = repository.getLoggedInUserSync()
            if (user != null) {
                _currentScreen.value = AppScreen.MAIN
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                if (user.lastDailyRewardDate != today) {
                    showDailyRewardDialog.value = true
                }
            } else {
                _currentScreen.value = AppScreen.AUTH
            }
        }
    }

    fun setAppLanguage(lang: String) {
        appLanguage.value = lang
    }

    fun setBottomTab(tab: BottomTab) {
        _currentBottomTab.value = tab
    }

    fun setMessagesCategory(category: String) {
        _selectedMessagesCategory.value = category
    }

    // Auth actions
    fun register() {
        viewModelScope.launch {
            authLoading.value = true
            authError.value = null
            val result = repository.registerUser(
                username = authUsername.value,
                password = authPassword.value,
                contact = authContact.value,
                isGmail = authIsGmail.value
            )
            authLoading.value = false
            when (result) {
                is ClickaRepository.AuthResult.Success -> {
                    authError.value = null
                    _currentScreen.value = AppScreen.MAIN
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    if (result.user.lastDailyRewardDate != today) {
                        showDailyRewardDialog.value = true
                    }
                }
                is ClickaRepository.AuthResult.Error -> {
                    authError.value = result.message
                }
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            authLoading.value = true
            authError.value = null
            val result = repository.loginUser(
                usernameOrContact = authUsername.value,
                password = authPassword.value
            )
            authLoading.value = false
            when (result) {
                is ClickaRepository.AuthResult.Success -> {
                    authError.value = null
                    _currentScreen.value = AppScreen.MAIN
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    if (result.user.lastDailyRewardDate != today) {
                        showDailyRewardDialog.value = true
                    }
                }
                is ClickaRepository.AuthResult.Error -> {
                    authError.value = result.message
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _activeRoomId.value = null
            _currentScreen.value = AppScreen.AUTH
        }
    }

    fun switchAccount() {
        viewModelScope.launch {
            repository.logout()
            _activeRoomId.value = null
            authUsername.value = ""
            authPassword.value = ""
            authContact.value = ""
            authError.value = null
            isRegisterMode.value = false
            _currentScreen.value = AppScreen.AUTH
        }
    }

    // Room actions
    fun enterRoom(roomId: Long) {
        viewModelScope.launch {
            val room = allRooms.value.find { it.id == roomId }
            val currentUser = loggedInUser.value

            // Check if room is locked and user is neither owner nor admin
            if (room?.isLocked == true && room.pinCode.isNotBlank() && room.ownerId != currentUser?.id && currentUser?.isAdmin != true) {
                pendingLockedRoom.value = room
                return@launch
            }

            _activeRoomId.value = roomId
            _currentScreen.value = AppScreen.LIVE_ROOM
            startMicSpeakingSimulation()
        }
    }

    fun submitRoomPin(pinEntered: String): Boolean {
        val room = pendingLockedRoom.value ?: return false
        if (room.pinCode == pinEntered) {
            _activeRoomId.value = room.id
            _currentScreen.value = AppScreen.LIVE_ROOM
            pendingLockedRoom.value = null
            startMicSpeakingSimulation()
            return true
        }
        return false
    }

    fun leaveRoom() {
        speakingSimulationJob?.cancel()
        _activeRoomId.value = null
        _currentScreen.value = AppScreen.MAIN
    }

    fun createRoom(name: String, imageUrl: String) {
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            val result = repository.createRoom(
                userId = user.id,
                userName = user.displayName,
                roomName = name,
                roomImageUrl = imageUrl
            )
            when (result) {
                is ClickaRepository.CreateRoomResult.Success -> {
                    showCreateRoomDialog.value = false
                    roomErrorMessage.value = null
                    enterRoom(result.room.id)
                }
                is ClickaRepository.CreateRoomResult.Error -> {
                    roomErrorMessage.value = result.message
                }
            }
        }
    }

    fun updateTvYoutubeUrl(url: String) {
        val roomId = _activeRoomId.value ?: return
        viewModelScope.launch {
            repository.updateRoomYoutubeUrl(roomId, url)
            showTvUrlDialog.value = false
        }
    }

    // Microphones
    fun takeOrReleaseMic(slotIndex: Int) {
        val user = loggedInUser.value ?: return
        val currentList = _micSeats.value.toMutableList()
        val seat = currentList[slotIndex]

        if (seat.userId == user.id) {
            // Already on seat -> release
            currentList[slotIndex] = seat.copy(
                userId = null,
                userName = null,
                userAvatar = null,
                isSpeaking = false,
                isVip = false,
                isAdmin = false,
                avatarFrame = ""
            )
        } else if (seat.userId == null) {
            // Empty seat -> take it!
            currentList[slotIndex] = seat.copy(
                userId = user.id,
                userName = user.displayName,
                userAvatar = user.avatarUrl,
                isMuted = false,
                isSpeaking = true,
                isVip = user.isVip,
                isAdmin = user.isAdmin,
                avatarFrame = user.avatarFrame
            )
        } else {
            // Seat occupied: If the occupant is the admin "يوسف" / isAdmin, NOBODY can remove them!
            if (seat.isAdmin) {
                storeSnackbarMessage.value = "لا يمكن تنزيل مدير التطبيق من الميكروفون!"
                return
            }
            // Admin can seat any user or reclaim seat
            if (user.isAdmin) {
                currentList[slotIndex] = seat.copy(
                    userId = user.id,
                    userName = user.displayName,
                    userAvatar = user.avatarUrl,
                    isMuted = false,
                    isSpeaking = true,
                    isVip = user.isVip,
                    isAdmin = user.isAdmin,
                    avatarFrame = user.avatarFrame
                )
            }
        }
        _micSeats.value = currentList
    }

    fun toggleMuteMic(slotIndex: Int) {
        val currentList = _micSeats.value.toMutableList()
        val seat = currentList[slotIndex]
        currentList[slotIndex] = seat.copy(isMuted = !seat.isMuted, isSpeaking = if (!seat.isMuted) false else seat.isSpeaking)
        _micSeats.value = currentList
    }

    private fun startMicSpeakingSimulation() {
        speakingSimulationJob?.cancel()
        speakingSimulationJob = viewModelScope.launch {
            while (true) {
                delay(1200)
                val current = _micSeats.value.map { seat ->
                    if (seat.userId != null && !seat.isMuted) {
                        seat.copy(isSpeaking = Random.nextBoolean())
                    } else {
                        seat.copy(isSpeaking = false)
                    }
                }
                _micSeats.value = current
            }
        }
    }

    // Tapping (التكبيس) in the room
    fun tapRoom() {
        val roomId = _activeRoomId.value ?: return
        val user = loggedInUser.value ?: return
        _roomTaps.value += 1

        val heartColors = listOf(
            0xFFFF1744, // Red
            0xFFFF4081, // Pink
            0xFFE040FB, // Purple
            0xFFFFD700, // Gold
            0xFF00E5FF  // Cyan
        )
        val newHeart = FloatingHeart(
            id = System.currentTimeMillis() + Random.nextLong(1000),
            xOffsetFactor = Random.nextFloat() * 2f - 1f,
            colorHex = heartColors.random(),
            sizeDp = Random.nextInt(26, 42)
        )
        _floatingHearts.value = (_floatingHearts.value + newHeart).takeLast(18)

        // Automatically remove the heart after 1200ms so it never stays hanging
        viewModelScope.launch {
            delay(1200)
            _floatingHearts.value = _floatingHearts.value.filter { it.id != newHeart.id }
        }

        viewModelScope.launch {
            repository.tapRoom(roomId, user.id)
        }
    }

    // Chat and Messages
    fun sendRoomMessage(content: String) {
        val roomId = _activeRoomId.value ?: return
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            repository.sendRoomChatMessage(
                roomId = roomId,
                senderId = user.id,
                senderName = user.displayName,
                senderAvatar = user.avatarUrl,
                content = content
            )
        }
    }

    fun sendGift(gift: GiftItem) {
        val roomId = _activeRoomId.value ?: return
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            val success = repository.sendRoomGift(roomId, user, gift)
            if (success) {
                showGiftSheet.value = false
                // Trigger full-screen TikTok animation & audio effect
                activeGiftAnimation.value = ActiveGiftAnimation(
                    senderName = user.displayName,
                    senderAvatar = user.avatarUrl,
                    giftName = gift.name,
                    giftIcon = gift.emoji,
                    giftId = gift.id,
                    comboCount = 1
                )
                GiftSoundPlayer.playGiftSound(gift.id)
            } else {
                storeSnackbarMessage.value = "رصيدك من العملات غير كافٍ! يرجى شحن المحفظة."
            }
        }
    }

    fun toggleRoomTvPower(roomId: Long, isTvOn: Boolean) {
        viewModelScope.launch {
            repository.updateRoomTvPower(roomId, isTvOn)
        }
    }

    fun sendAppMessage(content: String, groupTitle: String = "") {
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            repository.sendMessage(
                category = _selectedMessagesCategory.value,
                senderId = user.id,
                senderName = user.displayName,
                senderAvatar = user.avatarUrl,
                content = content,
                groupTitle = groupTitle
            )
        }
    }

    fun claimTask(task: TaskEntity) {
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            repository.claimTask(user, task)
        }
    }

    fun updateProfile(displayName: String, bio: String, avatarUrl: String) {
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            repository.updateUserProfile(
                user.copy(
                    displayName = displayName.trim(),
                    bio = bio.trim(),
                    avatarUrl = avatarUrl
                )
            )
            showEditProfileDialog.value = false
        }
    }

    // ==========================================
    // STORE ACTIONS (متجر كليكا)
    // ==========================================

    fun confirmFlexyRecharge(bundle: FlexyBundle, senderPhone: String) {
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            repository.rechargeFlexyCoins(user, bundle.coinsAmount, bundle.dzdAmount, senderPhone)
            showFlexyDialog.value = false
            storeSnackbarMessage.value = "تم شحن ${bundle.coinsAmount} عملة بنجاح إلى محفظتك! 🪙"
        }
    }

    fun buySpecialUserId(specialId: String) {
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            val result = repository.buySpecialUserId(user, specialId)
            when (result) {
                is ClickaRepository.StoreResult.Success -> {
                    storeSnackbarMessage.value = result.message
                }
                is ClickaRepository.StoreResult.Error -> {
                    storeSnackbarMessage.value = result.message
                }
            }
        }
    }

    fun buySpecialRoomId(specialId: String) {
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            val userRoom = repository.getUserRoom(user.id)
            if (userRoom == null) {
                storeSnackbarMessage.value = "يجب عليك إنشاء غرفة أولاً لشراء آيدي مميز لها!"
                return@launch
            }
            val result = repository.buySpecialRoomId(user, userRoom, specialId)
            when (result) {
                is ClickaRepository.StoreResult.Success -> {
                    storeSnackbarMessage.value = result.message
                }
                is ClickaRepository.StoreResult.Error -> {
                    storeSnackbarMessage.value = result.message
                }
            }
        }
    }

    fun buyAvatarFrame(frameType: String, basePrice: Int, title: String) {
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            val result = repository.buyAvatarFrame(user, frameType, basePrice, title)
            when (result) {
                is ClickaRepository.StoreResult.Success -> {
                    storeSnackbarMessage.value = result.message
                }
                is ClickaRepository.StoreResult.Error -> {
                    storeSnackbarMessage.value = result.message
                }
            }
        }
    }

    fun buyVip() {
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            val result = repository.buyVip(user)
            when (result) {
                is ClickaRepository.StoreResult.Success -> {
                    storeSnackbarMessage.value = result.message
                }
                is ClickaRepository.StoreResult.Error -> {
                    storeSnackbarMessage.value = result.message
                }
            }
        }
    }

    fun buyRoomPin(pinCode: String) {
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            val userRoom = repository.getUserRoom(user.id)
            if (userRoom == null) {
                storeSnackbarMessage.value = "يجب عليك إنشاء غرفة أولاً لتعيين قفل سري لها!"
                return@launch
            }
            val result = repository.buyRoomPinLock(user, userRoom, pinCode)
            when (result) {
                is ClickaRepository.StoreResult.Success -> {
                    storeSnackbarMessage.value = result.message
                }
                is ClickaRepository.StoreResult.Error -> {
                    storeSnackbarMessage.value = result.message
                }
            }
        }
    }

    fun buyRoomPinLock(pinCode: String) = buyRoomPin(pinCode)

    fun selectFlexyBundle(bundle: FlexyBundle) {
        selectedFlexyBundle.value = bundle
        showFlexyDialog.value = true
    }

    fun confirmFlexyRecharge(senderPhone: String) {
        val bundle = selectedFlexyBundle.value ?: return
        confirmFlexyRecharge(bundle, senderPhone)
    }

    fun verifyRoomPinAndEnter(pin: String): Boolean {
        return submitRoomPin(pin)
    }

    fun setLanguage(lang: String) {
        appLanguage.value = lang
    }

    fun dismissSnackbar() {
        storeSnackbarMessage.value = null
    }

    fun claimDailyReward() {
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            val (success, newBalance) = repository.claimDailyReward(user.id)
            if (success) {
                storeSnackbarMessage.value = "مبروك! تم استلام 5 عملات هدية يومية 🎉 رصيدك الجديد: $newBalance عملة"
                showDailyRewardDialog.value = false
            } else {
                storeSnackbarMessage.value = "لقد استلمت هدية اليوم مسبقاً! عُد غداً لاستلام 5 عملات إضافية 🎁"
            }
        }
    }

    fun updatePrivacySettings(allowPrivateMessages: Boolean, allowFriendRequests: Boolean) {
        val user = loggedInUser.value ?: return
        viewModelScope.launch {
            repository.updatePrivacySettings(user.id, allowPrivateMessages, allowFriendRequests)
            storeSnackbarMessage.value = "تم تحديث إعدادات الخصوصية بنجاح 🔒"
        }
    }

    fun sendFriendRequest(targetUser: UserEntity) {
        val user = loggedInUser.value ?: return
        if (!targetUser.allowFriendRequests) {
            storeSnackbarMessage.value = "عذراً، هذا المستخدم قام بقفل استقبال طلبات الصداقة 🔒"
            return
        }
        val friends = user.friendsList.split(",").filter { it.isNotBlank() }
        if (friends.contains(targetUser.uniqueId)) {
            storeSnackbarMessage.value = "أنت وهذا المستخدم أصدقاء بالفعل! 🤝"
            return
        }
        viewModelScope.launch {
            repository.addFriend(user.id, targetUser.uniqueId)
            repository.addFriend(targetUser.id, user.uniqueId)
            storeSnackbarMessage.value = "تمت إضافة ${targetUser.displayName} إلى قائمة الأصدقاء بنجاح! 🤝✨"
        }
    }
}
