package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.ClickaGold
import com.example.ui.theme.ClickaPrimary
import com.example.ui.theme.ClickaSecondary
import com.example.ui.theme.ClickaTertiary

@Composable
fun DecoratedAvatar(
    avatarUrl: String,
    displayName: String,
    avatarFrame: String,
    isVip: Boolean,
    isAdmin: Boolean,
    size: Dp = 108.dp,
    modifier: Modifier = Modifier
) {
    // Rainbow rotation transition
    val infiniteTransition = rememberInfiniteTransition(label = "rainbow_anim")
    val rainbowAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainbow_angle"
    )

    Box(
        modifier = modifier.size(size + 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Frame decorations:
        when (avatarFrame) {
            "rainbow" -> {
                // Animated moving rainbow ring
                Box(
                    modifier = Modifier
                        .size(size + 8.dp)
                        .rotate(rainbowAngle)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFFFF007F),
                                    Color(0xFFFF7F00),
                                    Color(0xFFFFFF00),
                                    Color(0xFF00FF7F),
                                    Color(0xFF00FFFF),
                                    Color(0xFF7F00FF),
                                    Color(0xFFFF007F)
                                )
                            ),
                            CircleShape
                        )
                )
            }
            "butterfly" -> {
                // Butterfly ring
                Box(
                    modifier = Modifier
                        .size(size + 6.dp)
                        .border(
                            2.5.dp,
                            Brush.linearGradient(listOf(Color(0xFFFF69B4), Color(0xFF9370DB))),
                            CircleShape
                        )
                )
            }
            "kiss" -> {
                // Kiss / romance ring
                Box(
                    modifier = Modifier
                        .size(size + 6.dp)
                        .border(
                            2.5.dp,
                            Brush.linearGradient(listOf(Color(0xFFFF1493), Color(0xFFFF4500))),
                            CircleShape
                        )
                )
            }
            "crown" -> {
                // Crown gold ring
                Box(
                    modifier = Modifier
                        .size(size + 6.dp)
                        .border(
                            2.5.dp,
                            Brush.linearGradient(listOf(ClickaGold, Color(0xFFFFB300))),
                            CircleShape
                        )
                )
            }
        }

        // Base circular avatar
        AsyncImage(
            model = avatarUrl,
            contentDescription = displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color(0xFF1E1730))
        )

        // Crown decoration on TOP of avatar
        if (avatarFrame == "crown") {
            Text(
                text = "👑",
                fontSize = (size.value * 0.28).sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-10).dp)
            )
        }

        // Butterfly decoration on rim
        if (avatarFrame == "butterfly") {
            Text(
                text = "🦋",
                fontSize = (size.value * 0.26).sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-4).dp)
            )
        }

        // Kiss decoration on rim
        if (avatarFrame == "kiss") {
            Text(
                text = "💋",
                fontSize = (size.value * 0.26).sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-4).dp)
            )
        }

        // VIP badge
        if (isVip) {
            Surface(
                color = ClickaGold,
                shape = RoundedCornerShape(4.dp),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 4.dp, y = (-2).dp)
            ) {
                Text(
                    text = "VIP",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = (size.value * 0.12).coerceAtLeast(9.0).sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}
