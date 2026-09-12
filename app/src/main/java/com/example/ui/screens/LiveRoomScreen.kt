package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.model.MicSeatState
import com.example.data.model.RoomEntity
import com.example.data.model.RoomMessageEntity
import com.example.data.model.UserEntity
import com.example.ui.components.ActiveGiftAnimation
import com.example.ui.components.DecoratedAvatar
import com.example.ui.components.TikTokGiftOverlay
import com.example.ui.theme.*
import com.example.ui.viewmodel.FloatingHeart
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveRoomScreen(
    room: RoomEntity?,
    currentUser: UserEntity?,
    micSeats: List<MicSeatState>,
    messages: List<RoomMessageEntity>,
    tapCount: Int,
    floatingHearts: List<FloatingHeart>,
    onLeaveRoom: () -> Unit,
    onChangeYoutubeUrlClick: () -> Unit,
    onTakeOrReleaseMic: (Int) -> Unit,
    onToggleMuteMic: (Int) -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenGifts: () -> Unit,
    onTapRoom: () -> Unit,
    onToggleTvPower: (Boolean) -> Unit = {},
    activeGiftAnimation: ActiveGiftAnimation? = null,
    onDismissGiftAnimation: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (room == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ClickaPrimary)
        }
        return
    }

    var chatInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto scroll chat to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Tap pulse animation
    val heartPulseTransition = rememberInfiniteTransition(label = "heart_pulse")
    val heartScale by heartPulseTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ClickaDarkBg)
            .testTag("live_room_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .navigationBarsPadding()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    })
                }
        ) {
            // 1. TOP HEADER: Room Name, Room Photo, Room ID, Leave Button
            Surface(
                color = ClickaCardBg,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Room Photo
                    AsyncImage(
                        model = room.imageUrl,
                        contentDescription = room.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, ClickaPrimary, RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Room Name & ID
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = room.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = ClickaLiveRed,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "LIVE",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            if (room.isVip) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    color = ClickaGold,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "VIP 👑",
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            if (room.isLocked) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "غرفة مقفولة",
                                    tint = ClickaGold,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // ID formatted strictly in English
                            Text(
                                text = "ID: ${room.roomIdString}",
                                color = ClickaTertiary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• ${room.viewerCount} مشاهد",
                                color = ClickaTextTertiary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Leave Room Button
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onLeaveRoom()
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF2C192E)),
                        modifier = Modifier.testTag("leave_room_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "خروج من الغرفة",
                            tint = Color.White
                        )
                    }
                }
            }

            val isOwnerOrAdmin = (currentUser?.id == room.ownerId || currentUser?.isAdmin == true)

            // 2. TV SECTION (إذا أشعلها صاحب الغرفة أو الأدمن يشاهد الجميع في الغرفة)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF100D1A)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (room.isTvOn) Color(0xFF00E676) else ClickaTertiary.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Column {
                    // TV Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF191326))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (room.isTvOn) Icons.Default.Tv else Icons.Default.TvOff,
                                contentDescription = null,
                                tint = if (room.isTvOn) Color(0xFF00E676) else Color(0xFFA099B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تلفاز كليكا المباشر",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = if (room.isTvOn) Color(0x3300E676) else Color(0x33A099B8),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (room.isTvOn) "🔴 مباشر للجميع" else "⚪ مغلق",
                                    color = if (room.isTvOn) Color(0xFF00E676) else Color(0xFFA099B8),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Owner / Admin TV Controls
                        if (isOwnerOrAdmin) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Power Toggle Button
                                Surface(
                                    color = if (room.isTvOn) Color(0xFFD32F2F) else Color(0xFF00C853),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable { onToggleTvPower(!room.isTvOn) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PowerSettingsNew,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (room.isTvOn) "إطفاء" else "تشغيل للجميع",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Change Link button
                                Surface(
                                    color = ClickaPrimary,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable { onChangeYoutubeUrlClick() }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Link,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "الرابط",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // TV Screen / YouTube Player
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        if (room.isTvOn) {
                            YouTubeEmbeddedPlayer(
                                youtubeUrl = room.youtubeUrl,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TvOff,
                                    contentDescription = null,
                                    tint = Color(0xFF6B6584),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "شاشة التلفاز مطفأة حالياً",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                if (isOwnerOrAdmin) {
                                    Text(
                                        text = "أنت المشرف / صاحب الغرفة، انقر لتشغيل الشاشة لجميع المتواجدين",
                                        color = Color(0xFFA099B8),
                                        fontSize = 11.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        color = Color(0xFF00C853),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.clickable { onToggleTvPower(true) }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "تشغيل الشاشة الآن للجميع ▶️",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "عندما يشعل صاحب الغرفة أو الأدمن الشاشة ستظهر المشاهدة فوراً للجميع",
                                        color = Color(0xFF756F8E),
                                        fontSize = 11.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. 4 MICROPHONES SECTION (تحت التلفاز يوجد 4 ميكروفون)
            Surface(
                color = ClickaCardBg,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = ClickaSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الميكروفونات الصوتية (4 مقاعد)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "اضغط على المقعد للتحدث 🎙️",
                            color = ClickaTextTertiary,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        micSeats.forEach { seat ->
                            MicSeatItem(
                                seat = seat,
                                isCurrentUser = seat.userId == currentUser?.id,
                                onClick = { onTakeOrReleaseMic(seat.slotIndex) },
                                onToggleMute = { onToggleMuteMic(seat.slotIndex) }
                            )
                        }
                    }
                }
            }

            // 4. ROOM LIVE CHAT SECTION (تحت الميكروفون يوجد رسائل الدردشة)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Room welcome notice
                item {
                    Surface(
                        color = Color(0xFF1E1730).copy(alpha = 0.7f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = ClickaGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "مرحباً بكم في ${room.name}! يرجى احترام القوانين والاستمتاع بالمحتوى.",
                                color = ClickaTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    RoomChatMessageItem(msg = msg)
                }
            }

            // 5. BOTTOM BAR: Chat input, Gift Icon, Tapping Button (التكبيس)
            Surface(
                color = ClickaCardBg,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chat text field
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = { Text("دردش مع أعضاء الغرفة...", fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (chatInput.isNotBlank()) {
                                    onSendMessage(chatInput)
                                    chatInput = ""
                                }
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ClickaPrimary,
                            unfocusedBorderColor = ClickaDivider,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("room_chat_input")
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Chat Button
                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                onSendMessage(chatInput)
                                chatInput = ""
                            }
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = ClickaPrimary),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "إرسال",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Gift Icon (أيقونة الهدايا)
                    Surface(
                        color = Color(0xFF381559),
                        shape = CircleShape,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .size(42.dp)
                            .clickable {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                onOpenGifts()
                            }
                            .testTag("room_gift_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🎁", fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Tapping Button (التكبيس في الغرفة)
                    Surface(
                        color = ClickaSecondary,
                        shape = CircleShape,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .size(44.dp)
                            .scale(heartScale)
                            .clickable { onTapRoom() }
                            .testTag("room_tap_button")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "❤️", fontSize = 16.sp)
                            Text(
                                text = "$tapCount",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Floating Hearts Animation overlay when users tap (التكبيس)
        floatingHearts.forEach { heart ->
            KeyedFloatingHeart(heart = heart)
        }

        // Full-Screen TikTok Gift Animation Overlay & Audio
        TikTokGiftOverlay(
            gift = activeGiftAnimation,
            onDismiss = onDismissGiftAnimation
        )
    }
}

@Composable
fun MicSeatItem(
    seat: MicSeatState,
    isCurrentUser: Boolean,
    onClick: () -> Unit,
    onToggleMute: () -> Unit
) {
    // Speaking wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "wave_${seat.slotIndex}")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (seat.isSpeaking) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(54.dp)
        ) {
            // Animated Speaking Ring
            if (seat.isSpeaking && !seat.isMuted) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .scale(waveScale)
                        .background(ClickaTertiary.copy(alpha = 0.35f), CircleShape)
                )
            }

            if (seat.userId != null) {
                // Occupied seat with full avatar decoration (frame, VIP, Admin badge)
                DecoratedAvatar(
                    avatarUrl = seat.userAvatar ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100",
                    displayName = seat.userName ?: "",
                    avatarFrame = seat.avatarFrame,
                    isVip = seat.isVip,
                    isAdmin = seat.isAdmin,
                    size = 38.dp
                )

                // Mute status badge
                Surface(
                    color = if (seat.isMuted) Color(0xFFD32F2F) else ClickaSuccess,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clickable(enabled = isCurrentUser) { onToggleMute() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (seat.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            } else {
                // Empty seat
                Surface(
                    color = Color(0xFF221A33),
                    shape = CircleShape,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "صعود للمايك",
                            tint = ClickaTextTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = seat.userName ?: "مايك ${seat.slotIndex + 1}",
            color = if (seat.userId != null) Color.White else ClickaTextTertiary,
            fontSize = 11.sp,
            fontWeight = if (seat.userId != null) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun RoomChatMessageItem(msg: RoomMessageEntity) {
    if (msg.isGift) {
        // Luxury Gift Card Banner in Chat
        Surface(
            color = Color(0xFF3B1238),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ClickaGold.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = msg.giftIcon ?: "🎁", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${msg.senderName} أرسل ${msg.giftName} 👑",
                        color = ClickaGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "دعم مميز للمضيف والغرفة!",
                        color = Color(0xFFFFD8E4),
                        fontSize = 10.sp
                    )
                }
            }
        }
    } else {
        // Regular chat message
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = msg.senderAvatar,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = Color(0xFF1E182F),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(
                        text = "${msg.senderName}: ",
                        color = ClickaTertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = msg.content,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun KeyedFloatingHeart(heart: FloatingHeart) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(heart.id) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1100, easing = FastOutSlowInEasing)
        )
    }

    if (animProgress.value >= 1f) {
        return
    }

    val yOffset = -animProgress.value * 350f
    val xOffset = heart.xOffsetFactor * 80f * animProgress.value
    val alpha = ((1f - animProgress.value) * 1.5f).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 70.dp, end = 24.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Text(
            text = when ((heart.id % 5).toInt()) {
                0 -> "❤️"
                1 -> "💖"
                2 -> "✨"
                3 -> "🔥"
                else -> "💜"
            },
            fontSize = heart.sizeDp.sp,
            modifier = Modifier
                .offset(x = xOffset.dp, y = yOffset.dp)
                .scale(0.7f + animProgress.value * 0.6f)
                .alpha(alpha)
        )
    }
}

/**
 * YouTube Player using Android WebView embedded iframe.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeEmbeddedPlayer(
    youtubeUrl: String,
    modifier: Modifier = Modifier
) {
    val videoId = remember(youtubeUrl) { extractYouTubeVideoId(youtubeUrl) ?: "5qap5aO4i9A" }

    val htmlContent = remember(videoId) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; background: #000; }
                body, html { width: 100%; height: 100%; overflow: hidden; background: #000; }
                iframe { width: 100%; height: 100%; border: 0; }
            </style>
        </head>
        <body>
            <iframe 
                src="https://www.youtube-nocookie.com/embed/$videoId?autoplay=1&playsinline=1&controls=1&enablejsapi=1&rel=0" 
                frameborder="0" 
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                allowfullscreen>
            </iframe>
        </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        factory = { context ->
            try {
                WebView(context).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    try {
                        setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                    } catch (_: Exception) {}
                    setBackgroundColor(android.graphics.Color.BLACK)
                    tag = videoId
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        cacheMode = WebSettings.LOAD_NO_CACHE
                    }
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    loadDataWithBaseURL("https://www.youtube-nocookie.com", htmlContent, "text/html", "UTF-8", null)
                }
            } catch (e: Exception) {
                android.widget.TextView(context).apply {
                    text = "▶ شاشة البث المباشر (YouTube Player)"
                    setTextColor(android.graphics.Color.WHITE)
                    gravity = android.view.Gravity.CENTER
                    setBackgroundColor(android.graphics.Color.BLACK)
                }
            }
        },
        update = { view ->
            if (view is WebView) {
                if (view.tag != videoId) {
                    view.tag = videoId
                    view.loadDataWithBaseURL("https://www.youtube-nocookie.com", htmlContent, "text/html", "UTF-8", null)
                }
            }
        },
        onRelease = { view ->
            if (view is WebView) {
                try {
                    view.stopLoading()
                    view.loadUrl("about:blank")
                    view.destroy()
                } catch (_: Exception) {}
            }
        },
        modifier = modifier
    )
}

fun extractYouTubeVideoId(url: String): String? {
    if (url.isBlank()) return null
    val patterns = listOf(
        "(?:v=|/v/|youtu\\.be/|/embed/|/live/)([a-zA-Z0-9_-]{11})",
        "^([a-zA-Z0-9_-]{11})$"
    )
    for (patternStr in patterns) {
        val pattern = Pattern.compile(patternStr)
        val matcher = pattern.matcher(url)
        if (matcher.find()) {
            return matcher.group(1)
        }
    }
    return null
}
