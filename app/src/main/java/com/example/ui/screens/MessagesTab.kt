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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MessageEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessagesTab(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    messages: List<MessageEntity>,
    onSendMessage: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var draftText by remember { mutableStateOf("") }
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
            .testTag("messages_tab")
    ) {
        // Sub-tabs: خاص, إدارة, مجموعة
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .background(ClickaCardBg, RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            val tabs = listOf(
                Triple("private", "رسائل الخاص", Icons.Default.ChatBubbleOutline),
                Triple("admin", "رسائل الإدارة", Icons.Default.Shield),
                Triple("group", "رسائل المجموعة", Icons.Default.Groups)
            )

            tabs.forEach { (categoryKey, title, icon) ->
                val isSelected = selectedCategory == categoryKey
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) ClickaPrimary else Color.Transparent)
                        .clickable { onSelectCategory(categoryKey) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else ClickaTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else ClickaTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Messages Feed
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = when (selectedCategory) {
                            "admin" -> Icons.Default.NotificationsNone
                            "group" -> Icons.Default.GroupAdd
                            else -> Icons.Default.Forum
                        },
                        contentDescription = null,
                        tint = ClickaTextTertiary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when (selectedCategory) {
                            "admin" -> "لا توجد رسائل جديدة من الإدارة حالياً"
                            "group" -> "لا توجد رسائل في المجموعات، كن أول من يرسل!"
                            else -> "صندوق الرسائل الخاصة فارغ"
                        },
                        color = ClickaTextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    MessageItemCard(msg = msg)
                }
            }
        }

        // Send Message Bar for private & group
        if (selectedCategory != "admin") {
            Surface(
                color = ClickaCardBg,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = draftText,
                        onValueChange = { draftText = it },
                        placeholder = {
                            Text(
                                if (selectedCategory == "group") "اكتب رسالة في المجموعة..."
                                else "اكتب رسالة خاصة...",
                                fontSize = 13.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (draftText.isNotBlank()) {
                                    onSendMessage(draftText, if (selectedCategory == "group") "مجتمع كليكا العام" else "")
                                    draftText = ""
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
                            .testTag("message_input_field")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (draftText.isNotBlank()) {
                                onSendMessage(draftText, if (selectedCategory == "group") "مجتمع كليكا العام" else "")
                                draftText = ""
                            }
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = ClickaPrimary),
                        modifier = Modifier.testTag("send_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "إرسال",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItemCard(msg: MessageEntity) {
    val dateStr = remember(msg.timestamp) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date(msg.timestamp))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = ClickaCardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar
            AsyncImage(
                model = msg.senderAvatar,
                contentDescription = msg.senderName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ClickaCardElevated)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = msg.senderName,
                        color = if (msg.category == "admin") ClickaGold else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = dateStr,
                        color = ClickaTextTertiary,
                        fontSize = 11.sp
                    )
                }

                if (msg.groupTitle.isNotBlank()) {
                    Text(
                        text = "في: ${msg.groupTitle}",
                        color = ClickaTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = msg.content,
                    color = ClickaTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
