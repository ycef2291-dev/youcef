package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.BadgeEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity
import com.example.ui.components.DecoratedAvatar
import com.example.ui.theme.*

@Composable
fun ProfileTab(
    user: UserEntity?,
    tasks: List<TaskEntity>,
    badges: List<BadgeEntity>,
    onClaimTask: (TaskEntity) -> Unit,
    onEditProfileClick: () -> Unit,
    onWalletClick: () -> Unit,
    onStoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (user == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ClickaPrimary)
        }
        return
    }

    val progressFraction = (user.currentXp.toFloat() / user.maxXp.toFloat()).coerceIn(0.02f, 1.0f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "level_circle_progress"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ClickaDarkBg)
            .padding(horizontal = 16.dp)
            .testTag("profile_tab"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Info Header with Circular Level Progress Ring
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ClickaCardBg),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar with Circular Level Progress Ring & Decorations (crown, rainbow, butterfly, kiss, VIP)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(136.dp)
                    ) {
                        // Background track and progress arc
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 6.dp.toPx()
                            // Track
                            drawCircle(
                                color = Color(0xFF261D3B),
                                style = Stroke(width = strokeWidth)
                            )
                            // Animated Progress Arc
                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        ClickaPrimary,
                                        ClickaSecondary,
                                        ClickaTertiary,
                                        ClickaGold,
                                        ClickaPrimary
                                    )
                                ),
                                startAngle = -90f,
                                sweepAngle = 360f * animatedProgress,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        // Decorated Avatar Image
                        DecoratedAvatar(
                            avatarUrl = user.avatarUrl,
                            displayName = user.displayName,
                            avatarFrame = user.avatarFrame,
                            isVip = user.isVip,
                            isAdmin = user.isAdmin,
                            size = 100.dp,
                            modifier = Modifier.testTag("profile_avatar_image")
                        )

                        // Floating Level Badge on Bottom Right of Avatar
                        Surface(
                            color = ClickaGold,
                            shape = CircleShape,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-2).dp, y = (-2).dp)
                        ) {
                            Text(
                                text = "Lv.${user.level}",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // User Name & ID (strictly ID in English, never in Arabic)
                    Text(
                        text = user.displayName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("profile_display_name")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = Color(0xFF1E1730),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            // strictly ID: ... without Arabic "ايدي"
                            Text(
                                text = "ID: ${user.uniqueId}",
                                color = ClickaTertiary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• @${user.username}",
                                color = ClickaTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = user.bio,
                        color = ClickaTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Level Details Under Avatar
                    Surface(
                        color = Color(0xFF160F26),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MilitaryTech,
                                        contentDescription = null,
                                        tint = ClickaGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ليفل الحساب: المستوى ${user.level}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Text(
                                    text = "${user.currentXp} / ${user.maxXp} XP",
                                    color = ClickaTertiary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                color = ClickaSecondary,
                                trackColor = Color(0xFF2E2447),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "أكمل المهام اليومية في الأسفل لملء الدائرة والصعود إلى المستوى التالي!",
                                color = ClickaTextTertiary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons: Wallet (no coin number shown here as requested), Store, and Edit Profile
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Wallet Button: Coins are ONLY shown in Wallet & Gift dialogs
                        OutlinedButton(
                            onClick = onWalletClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ClickaGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "محفظتي 💼", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // Store Button (متجر كليكا)
                        Button(
                            onClick = onStoreClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ClickaGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "متجر كليكا 🛍️", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // Edit Profile Button
                        Button(
                            onClick = onEditProfileClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ClickaPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "تعديل ✏️", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section: المهام اليومية لرفع الليفل (Tasks)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TaskAlt,
                    contentDescription = null,
                    tint = ClickaSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مهام رفع ليفل الحساب",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        items(tasks, key = { it.id }) { task ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ClickaCardBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = task.description,
                            color = ClickaTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = ClickaTertiary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "+${task.rewardXp} XP",
                                    color = ClickaTertiary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "التقدم: ${task.currentProgress} / ${task.maxProgress}",
                                color = ClickaTextTertiary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    if (task.isClaimed) {
                        Surface(
                            color = Color(0xFF1E3A24),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "مكتملة ✓",
                                color = Color(0xFF66BB6A),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { onClaimTask(task) },
                            colors = ButtonDefaults.buttonColors(containerColor = ClickaSecondary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "استلام", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: الأوسمة والإنجازات (Badges)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = ClickaGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الأوسمة والإنجازات 🏆",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        items(badges, key = { it.id }) { badge ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (badge.isUnlocked) Color(0xFF1F1735) else Color(0xFF140F21)
                ),
                shape = RoundedCornerShape(16.dp),
                border = if (badge.isUnlocked) androidx.compose.foundation.BorderStroke(1.dp, Color(badge.colorHex).copy(alpha = 0.6f)) else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(badge.colorHex).copy(alpha = if (badge.isUnlocked) 0.25f else 0.08f),
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = when (badge.iconName) {
                                    "star" -> "🌟"
                                    "mic" -> "🎙️"
                                    "crown" -> "👑"
                                    "castle" -> "🏰"
                                    else -> "⭐"
                                },
                                fontSize = 22.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = badge.title,
                            color = if (badge.isUnlocked) Color.White else ClickaTextTertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = badge.description,
                            color = if (badge.isUnlocked) ClickaTextSecondary else ClickaTextTertiary,
                            fontSize = 12.sp
                        )
                    }

                    if (badge.isUnlocked) {
                        Surface(
                            color = Color(badge.colorHex).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "مُفعّل ⭐",
                                color = Color(badge.colorHex),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "🔒 قيد الفتح",
                            color = ClickaTextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
