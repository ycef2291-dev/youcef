package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.ClickaDarkBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ClickaViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ClickaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ClickaDarkBg
                ) {
                    ClickaApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ClickaApp(viewModel: ClickaViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val splashSeconds by viewModel.splashSeconds.collectAsStateWithLifecycle()
    val loggedInUser by viewModel.loggedInUser.collectAsStateWithLifecycle()

    // Auth states
    val isRegisterMode by viewModel.isRegisterMode.collectAsStateWithLifecycle()
    val authUsername by viewModel.authUsername.collectAsStateWithLifecycle()
    val authPassword by viewModel.authPassword.collectAsStateWithLifecycle()
    val authContact by viewModel.authContact.collectAsStateWithLifecycle()
    val authIsGmail by viewModel.authIsGmail.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()

    // Main navigation & tabs
    val currentTab by viewModel.currentBottomTab.collectAsStateWithLifecycle()
    val messagesCategory by viewModel.selectedMessagesCategory.collectAsStateWithLifecycle()
    val messagesList by viewModel.messagesList.collectAsStateWithLifecycle()

    // Search
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchFilter by viewModel.searchType.collectAsStateWithLifecycle()
    val searchUsersResult by viewModel.searchUsersResult.collectAsStateWithLifecycle()
    val searchRoomsResult by viewModel.searchRoomsResult.collectAsStateWithLifecycle()

    // World Rooms
    val roomsList by viewModel.allRooms.collectAsStateWithLifecycle()

    // Profile
    val tasks by viewModel.userTasks.collectAsStateWithLifecycle()
    val badges by viewModel.userBadges.collectAsStateWithLifecycle()

    // Live Room
    val activeRoom by viewModel.activeRoom.collectAsStateWithLifecycle()
    val micSeats by viewModel.micSeats.collectAsStateWithLifecycle()
    val roomMessages by viewModel.activeRoomMessages.collectAsStateWithLifecycle()
    val roomTaps by viewModel.roomTaps.collectAsStateWithLifecycle()
    val floatingHearts by viewModel.floatingHearts.collectAsStateWithLifecycle()
    val activeGiftAnimation by viewModel.activeGiftAnimation.collectAsStateWithLifecycle()

    // Dialogs state
    val showCreateRoomDialog by viewModel.showCreateRoomDialog.collectAsStateWithLifecycle()
    val showEditProfileDialog by viewModel.showEditProfileDialog.collectAsStateWithLifecycle()
    val showWalletDialog by viewModel.showWalletDialog.collectAsStateWithLifecycle()
    val showStoreDialog by viewModel.showStoreDialog.collectAsStateWithLifecycle()
    val showFlexyDialog by viewModel.showFlexyDialog.collectAsStateWithLifecycle()
    val selectedFlexyBundle by viewModel.selectedFlexyBundle.collectAsStateWithLifecycle()
    val pendingLockedRoom by viewModel.pendingLockedRoom.collectAsStateWithLifecycle()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showAboutDialog by viewModel.showAboutDialog.collectAsStateWithLifecycle()
    val showShareDialog by viewModel.showShareDialog.collectAsStateWithLifecycle()
    val showGiftSheet by viewModel.showGiftSheet.collectAsStateWithLifecycle()
    val showTvUrlDialog by viewModel.showTvUrlDialog.collectAsStateWithLifecycle()
    val showDailyRewardDialog by viewModel.showDailyRewardDialog.collectAsStateWithLifecycle()
    val roomErrorMessage by viewModel.roomErrorMessage.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val storeSnackbarMessage by viewModel.storeSnackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(storeSnackbarMessage) {
        storeSnackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = ClickaDarkBg
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                AppScreen.SPLASH -> {
                    SplashScreen(remainingSeconds = splashSeconds)
                }
                AppScreen.AUTH -> {
                    AuthScreen(
                        isRegisterMode = isRegisterMode,
                        onToggleMode = { viewModel.isRegisterMode.value = it },
                        username = authUsername,
                        onUsernameChange = { viewModel.authUsername.value = it },
                        password = authPassword,
                        onPasswordChange = { viewModel.authPassword.value = it },
                        contact = authContact,
                        onContactChange = { viewModel.authContact.value = it },
                        isGmail = authIsGmail,
                        onToggleIsGmail = { viewModel.authIsGmail.value = it },
                        errorMessage = authError,
                        isLoading = authLoading,
                        onSubmit = {
                            if (isRegisterMode) viewModel.register() else viewModel.login()
                        }
                    )
                }
                AppScreen.MAIN -> {
                    MainAppScreen(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.setBottomTab(it) },
                        user = loggedInUser,
                        messagesCategory = messagesCategory,
                        onSelectMessagesCategory = { viewModel.setMessagesCategory(it) },
                        messagesList = messagesList,
                        onSendMessage = { text, groupTitle -> viewModel.sendAppMessage(text, groupTitle) },
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.searchQuery.value = it },
                        searchFilter = searchFilter,
                        onSearchFilterChange = { viewModel.searchType.value = it },
                        searchUsersResult = searchUsersResult,
                        searchRoomsResult = searchRoomsResult,
                        roomsList = roomsList,
                        onEnterRoom = { viewModel.enterRoom(it) },
                        onCreateRoomClick = {
                            viewModel.roomErrorMessage.value = null
                            viewModel.showCreateRoomDialog.value = true
                        },
                        tasks = tasks,
                        badges = badges,
                        onClaimTask = { viewModel.claimTask(it) },
                        onStoreClick = { viewModel.showStoreDialog.value = true },
                        onSendFriendRequest = { viewModel.sendFriendRequest(it) },
                        onEditProfileClick = { viewModel.showEditProfileDialog.value = true },
                        onSettingsClick = { viewModel.showSettingsDialog.value = true },
                        onWalletClick = { viewModel.showWalletDialog.value = true },
                        onAboutClick = { viewModel.showAboutDialog.value = true },
                        onShareClick = { viewModel.showShareDialog.value = true },
                        onSwitchAccountClick = { viewModel.switchAccount() }
                    )
                }
                AppScreen.LIVE_ROOM -> {
                    LiveRoomScreen(
                        room = activeRoom,
                        currentUser = loggedInUser,
                        micSeats = micSeats,
                        messages = roomMessages,
                        tapCount = roomTaps,
                        floatingHearts = floatingHearts,
                        onLeaveRoom = { viewModel.leaveRoom() },
                        onChangeYoutubeUrlClick = { viewModel.showTvUrlDialog.value = true },
                        onTakeOrReleaseMic = { viewModel.takeOrReleaseMic(it) },
                        onToggleMuteMic = { viewModel.toggleMuteMic(it) },
                        onSendMessage = { viewModel.sendRoomMessage(it) },
                        onOpenGifts = { viewModel.showGiftSheet.value = true },
                        onTapRoom = { viewModel.tapRoom() },
                        onToggleTvPower = { isTvOn ->
                            activeRoom?.let { viewModel.toggleRoomTvPower(it.id, isTvOn) }
                        },
                        activeGiftAnimation = activeGiftAnimation,
                        onDismissGiftAnimation = { viewModel.dismissActiveGiftAnimation() }
                    )
                }
            }
        }
    }

    // Dialogs & Modals
    if (showCreateRoomDialog) {
        CreateRoomDialog(
            errorMessage = roomErrorMessage,
            onDismiss = {
                viewModel.showCreateRoomDialog.value = false
                viewModel.roomErrorMessage.value = null
            },
            onCreate = { name, imageUrl ->
                viewModel.createRoom(name, imageUrl)
            }
        )
    }

    if (showEditProfileDialog && loggedInUser != null) {
        EditProfileDialog(
            user = loggedInUser!!,
            onDismiss = { viewModel.showEditProfileDialog.value = false },
            onSave = { name, bio, avatar ->
                viewModel.updateProfile(name, bio, avatar)
            }
        )
    }

    // Wallet Dialog (shows coins and Flexy bundles)
    if (showWalletDialog && loggedInUser != null) {
        WalletDialog(
            user = loggedInUser!!,
            bundles = viewModel.flexyBundles,
            onSelectBundle = { bundle ->
                viewModel.selectFlexyBundle(bundle)
            },
            onDismiss = { viewModel.showWalletDialog.value = false }
        )
    }

    // Flexy Recharge Dialog (+213793474663)
    if (showFlexyDialog && selectedFlexyBundle != null) {
        FlexyRechargeDialog(
            bundle = selectedFlexyBundle!!,
            targetPhoneNumber = "+213793474663",
            onDismiss = { viewModel.showFlexyDialog.value = false },
            onConfirm = { senderPhone ->
                viewModel.confirmFlexyRecharge(senderPhone)
            }
        )
    }

    // Clicka Store Dialog (متجر كليكا)
    if (showStoreDialog && loggedInUser != null) {
        StoreDialog(
            user = loggedInUser!!,
            onDismiss = { viewModel.showStoreDialog.value = false },
            onBuySpecialUserId = { spId -> viewModel.buySpecialUserId(spId) },
            onBuySpecialRoomId = { spId -> viewModel.buySpecialRoomId(spId) },
            onBuyAvatarFrame = { frameType, price, title -> viewModel.buyAvatarFrame(frameType, price, title) },
            onBuyVip = { viewModel.buyVip() },
            onBuyRoomPin = { pin -> viewModel.buyRoomPinLock(pin) }
        )
    }

    // Enter Room PIN Dialog
    if (pendingLockedRoom != null) {
        EnterRoomPinDialog(
            room = pendingLockedRoom!!,
            onDismiss = { viewModel.pendingLockedRoom.value = null },
            onSubmitPin = { pin -> viewModel.verifyRoomPinAndEnter(pin) }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentLanguage = appLanguage,
            user = loggedInUser,
            onLanguageChange = { viewModel.setLanguage(it) },
            onPrivacyChange = { allowMessages, allowRequests ->
                viewModel.updatePrivacySettings(allowMessages, allowRequests)
            },
            onDismiss = { viewModel.showSettingsDialog.value = false }
        )
    }

    if (showDailyRewardDialog && loggedInUser != null) {
        DailyRewardDialog(
            user = loggedInUser!!,
            onClaim = { viewModel.claimDailyReward() },
            onDismiss = { viewModel.showDailyRewardDialog.value = false }
        )
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { viewModel.showAboutDialog.value = false })
    }

    if (showShareDialog) {
        ShareDialog(onDismiss = { viewModel.showShareDialog.value = false })
    }

    if (showTvUrlDialog && activeRoom != null) {
        TvUrlDialog(
            currentUrl = activeRoom!!.youtubeUrl,
            onDismiss = { viewModel.showTvUrlDialog.value = false },
            onSave = { newUrl ->
                viewModel.updateTvYoutubeUrl(newUrl)
            }
        )
    }

    if (showGiftSheet && loggedInUser != null) {
        GiftsBottomSheet(
            gifts = viewModel.availableGifts,
            userCoins = loggedInUser!!.walletCoins,
            isVip = loggedInUser!!.isVip,
            onSendGift = { gift ->
                viewModel.sendGift(gift)
            },
            onDismiss = { viewModel.showGiftSheet.value = false }
        )
    }
}
