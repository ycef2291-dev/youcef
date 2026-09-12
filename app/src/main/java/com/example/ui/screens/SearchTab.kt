package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.RoomEntity
import com.example.data.model.UserEntity
import com.example.ui.components.DecoratedAvatar
import com.example.ui.theme.*

@Composable
fun SearchTab(
    query: String,
    onQueryChange: (String) -> Unit,
    filterType: String,
    onFilterChange: (String) -> Unit,
    usersResult: List<UserEntity>,
    roomsResult: List<RoomEntity>,
    currentUser: UserEntity? = null,
    onEnterRoom: (Long) -> Unit,
    onMessageUser: (UserEntity) -> Unit,
    onSendFriendRequest: (UserEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClickaDarkBg)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            }
            .testTag("search_tab")
    ) {
        // Search bar
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("ابحث عن صديق أو غرفة بالاسم أو (ID)...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = ClickaPrimary)
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = {
                        onQueryChange("")
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "مسح", tint = ClickaTextSecondary)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            ),
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ClickaPrimary,
                unfocusedBorderColor = ClickaDivider,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("search_input_field")
        )

        // Filter chips (الكل, أصدقاء, غرف)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("ALL" to "الكل 🌐", "FRIENDS" to "أصدقاء 👥", "ROOMS" to "غرف نشطة 🎙️")
            filters.forEach { (code, title) ->
                val isSelected = filterType == code
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterChange(code) },
                    label = { Text(title, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ClickaPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = ClickaCardBg,
                        labelColor = ClickaTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Results List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Rooms Section (if ALL or ROOMS)
            if (filterType == "ALL" || filterType == "ROOMS") {
                if (roomsResult.isNotEmpty()) {
                    item {
                        Text(
                            text = "الغرف (${roomsResult.size})",
                            color = ClickaTertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(roomsResult, key = { "room_${it.id}" }) { room ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ClickaCardBg),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEnterRoom(room.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = room.imageUrl,
                                    contentDescription = room.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = room.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "المضيف: ${room.ownerName} • ID: ${room.roomIdString}",
                                        color = ClickaTextSecondary,
                                        fontSize = 12.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🔴 مباشر • ", color = ClickaLiveRed, fontSize = 11.sp)
                                        Text(text = "${room.viewerCount} مشاهد", color = ClickaTextTertiary, fontSize = 11.sp)
                                    }
                                }
                                Button(
                                    onClick = { onEnterRoom(room.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ClickaPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("دخول", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Friends / Users Section (if ALL or FRIENDS)
            if (filterType == "ALL" || filterType == "FRIENDS") {
                if (usersResult.isNotEmpty()) {
                    item {
                        Text(
                            text = "المستخدمين والأصدقاء (${usersResult.size})",
                            color = ClickaSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(usersResult, key = { "user_${it.id}" }) { user ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ClickaCardBg),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DecoratedAvatar(
                                    avatarUrl = user.avatarUrl,
                                    displayName = user.displayName,
                                    avatarFrame = user.avatarFrame,
                                    isVip = user.isVip,
                                    isAdmin = user.isAdmin,
                                    size = 46.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.displayName,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = ClickaPrimary.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "Lv.${user.level}",
                                                color = ClickaTertiary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "ID: ${user.uniqueId}",
                                        color = ClickaTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }

                                val isMe = currentUser?.id == user.id
                                val isFriend = currentUser?.friendsList?.split(",")?.mapNotNull { it.trim().toLongOrNull() }?.contains(user.id) == true

                                if (isMe) {
                                    Surface(
                                        color = Color(0xFF261C3B),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("(أنت)", color = ClickaTextSecondary, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                } else if (isFriend) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Surface(
                                            color = Color(0xFF1B3820),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("صديق ✅", color = Color(0xFF81C784), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedButton(
                                            onClick = { onMessageUser(user) },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ClickaSecondary),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("مراسلة", fontSize = 11.sp)
                                        }
                                    }
                                } else {
                                    // Not friend
                                    Column(horizontalAlignment = Alignment.End) {
                                        if (!user.allowPrivateMessages) {
                                            // Private messages locked unless friends!
                                            Text(
                                                text = "🔒 الخاص مقفل",
                                                color = Color(0xFFFFAB91),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            if (user.allowFriendRequests) {
                                                Button(
                                                    onClick = { onSendFriendRequest(user) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = ClickaPrimary),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("طلب صداقة", fontSize = 10.sp)
                                                }
                                            } else {
                                                Text(
                                                    text = "🚫 طلبات الصداقة مقفلة",
                                                    color = ClickaTextTertiary,
                                                    fontSize = 9.sp
                                                )
                                            }
                                        } else {
                                            // Private messages allowed
                                            OutlinedButton(
                                                onClick = { onMessageUser(user) },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ClickaSecondary),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("مراسلة", fontSize = 11.sp)
                                            }
                                            if (user.allowFriendRequests) {
                                                TextButton(
                                                    onClick = { onSendFriendRequest(user) },
                                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text("+ طلب صداقة", fontSize = 10.sp, color = ClickaTertiary)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (roomsResult.isEmpty() && usersResult.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = ClickaTextTertiary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "لم يتم العثور على نتائج تطابق: $query",
                                color = ClickaTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
