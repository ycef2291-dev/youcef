package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.BottomTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    user: UserEntity?,
    // Messages
    messagesCategory: String,
    onSelectMessagesCategory: (String) -> Unit,
    messagesList: List<MessageEntity>,
    onSendMessage: (String, String) -> Unit,
    // Search
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchFilter: String,
    onSearchFilterChange: (String) -> Unit,
    searchUsersResult: List<UserEntity>,
    searchRoomsResult: List<RoomEntity>,
    // World Rooms
    roomsList: List<RoomEntity>,
    onEnterRoom: (Long) -> Unit,
    onCreateRoomClick: () -> Unit,
    // Profile
    tasks: List<TaskEntity>,
    badges: List<BadgeEntity>,
    onClaimTask: (TaskEntity) -> Unit,
    // Store
    onStoreClick: () -> Unit,
    // Friend Requests
    onSendFriendRequest: (UserEntity) -> Unit = {},
    // Top Bar 3-Dots Menu Callbacks
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onWalletClick: () -> Unit,
    onAboutClick: () -> Unit,
    onShareClick: () -> Unit,
    onSwitchAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "كليكا",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = ClickaLiveRed,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "LIVE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Quick Store Icon (no coin counter here, coins are strictly in Wallet and Gifts)
                    IconButton(
                        onClick = onStoreClick,
                        modifier = Modifier.testTag("top_bar_store_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "متجر كليكا",
                            tint = ClickaGold
                        )
                    }

                    // 3-Dots Menu Button (3 نقط في الاعلى)
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.testTag("top_app_bar_3dots_menu")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "قائمة الخيارات",
                                tint = Color.White
                            )
                        }

                        // Overflow Dropdown Menu with all requested items
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier
                                .background(ClickaCardBg)
                                .testTag("overflow_dropdown_menu")
                        ) {
                            // 1. تعديل الحساب (Edit Account)
                            DropdownMenuItem(
                                text = { Text("تعديل الحساب", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = ClickaPrimary) },
                                onClick = {
                                    menuExpanded = false
                                    onEditProfileClick()
                                }
                            )

                            // 2. إعدادات التطبيق (App Settings)
                            DropdownMenuItem(
                                text = { Text("إعدادات التطبيق", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = ClickaTertiary) },
                                onClick = {
                                    menuExpanded = false
                                    onSettingsClick()
                                }
                            )

                            // 3. محفظتي (My Wallet)
                            DropdownMenuItem(
                                text = { Text("محفظتي", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = ClickaGold) },
                                onClick = {
                                    menuExpanded = false
                                    onWalletClick()
                                }
                            )

                            // 4. متجر كليكا (Clicka Store)
                            DropdownMenuItem(
                                text = { Text("متجر كليكا 🛍️", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = ClickaGold) },
                                onClick = {
                                    menuExpanded = false
                                    onStoreClick()
                                }
                            )

                            // 5. حول التطبيق (About App)
                            DropdownMenuItem(
                                text = { Text("حول التطبيق", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = ClickaTextSecondary) },
                                onClick = {
                                    menuExpanded = false
                                    onAboutClick()
                                }
                            )

                            // 6. شارك مع الأصدقاء (Share with Friends)
                            DropdownMenuItem(
                                text = { Text("شارك مع الأصدقاء", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = ClickaSecondary) },
                                onClick = {
                                    menuExpanded = false
                                    onShareClick()
                                }
                            )

                            HorizontalDivider(color = ClickaDivider)

                            // 7. تغيير الحساب (Switch Account) / تسجيل الخروج
                            DropdownMenuItem(
                                text = { Text("تغيير الحساب", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold) },
                                leadingIcon = { Icon(Icons.Default.SwitchAccount, contentDescription = null, tint = Color(0xFFFF5252)) },
                                onClick = {
                                    menuExpanded = false
                                    onSwitchAccountClick()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ClickaDarkBg,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = ClickaCardBg,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // 1. الرسائل (Messages)
                NavigationBarItem(
                    selected = currentTab == BottomTab.MESSAGES,
                    onClick = { onTabSelected(BottomTab.MESSAGES) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == BottomTab.MESSAGES) Icons.Default.Chat else Icons.Outlined.Chat,
                            contentDescription = "الرسائل"
                        )
                    },
                    label = { Text("الرسائل", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = ClickaSecondary,
                        indicatorColor = ClickaSecondary,
                        unselectedIconColor = ClickaTextSecondary,
                        unselectedTextColor = ClickaTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_messages")
                )

                // 2. البحث عن الأصدقاء (Search)
                NavigationBarItem(
                    selected = currentTab == BottomTab.SEARCH,
                    onClick = { onTabSelected(BottomTab.SEARCH) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == BottomTab.SEARCH) Icons.Default.PersonSearch else Icons.Outlined.PersonSearch,
                            contentDescription = "البحث"
                        )
                    },
                    label = { Text("البحث", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = ClickaPrimary,
                        indicatorColor = ClickaPrimary,
                        unselectedIconColor = ClickaTextSecondary,
                        unselectedTextColor = ClickaTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_search")
                )

                // 3. حول العالم (Around the World / Active Rooms)
                NavigationBarItem(
                    selected = currentTab == BottomTab.WORLD_ROOMS,
                    onClick = { onTabSelected(BottomTab.WORLD_ROOMS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == BottomTab.WORLD_ROOMS) Icons.Default.Public else Icons.Outlined.Public,
                            contentDescription = "حول العالم"
                        )
                    },
                    label = { Text("حول العالم", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = ClickaTertiary,
                        indicatorColor = ClickaTertiary,
                        unselectedIconColor = ClickaTextSecondary,
                        unselectedTextColor = ClickaTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_world")
                )

                // 4. حسابي (My Profile)
                NavigationBarItem(
                    selected = currentTab == BottomTab.PROFILE,
                    onClick = { onTabSelected(BottomTab.PROFILE) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == BottomTab.PROFILE) Icons.Default.AccountCircle else Icons.Outlined.AccountCircle,
                            contentDescription = "حسابي"
                        )
                    },
                    label = { Text("حسابي", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = ClickaGold,
                        indicatorColor = ClickaGold,
                        unselectedIconColor = ClickaTextSecondary,
                        unselectedTextColor = ClickaTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_profile")
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                BottomTab.MESSAGES -> MessagesTab(
                    selectedCategory = messagesCategory,
                    onSelectCategory = onSelectMessagesCategory,
                    messages = messagesList,
                    onSendMessage = onSendMessage
                )
                BottomTab.SEARCH -> SearchTab(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    filterType = searchFilter,
                    onFilterChange = onSearchFilterChange,
                    usersResult = searchUsersResult,
                    roomsResult = searchRoomsResult,
                    currentUser = user,
                    onEnterRoom = onEnterRoom,
                    onMessageUser = {
                        onTabSelected(BottomTab.MESSAGES)
                    },
                    onSendFriendRequest = onSendFriendRequest
                )
                BottomTab.WORLD_ROOMS -> WorldRoomsTab(
                    rooms = roomsList,
                    onEnterRoom = onEnterRoom,
                    onCreateRoomClick = onCreateRoomClick
                )
                BottomTab.PROFILE -> ProfileTab(
                    user = user,
                    tasks = tasks,
                    badges = badges,
                    onClaimTask = onClaimTask,
                    onEditProfileClick = onEditProfileClick,
                    onWalletClick = onWalletClick,
                    onStoreClick = onStoreClick
                )
            }
        }
    }
}
