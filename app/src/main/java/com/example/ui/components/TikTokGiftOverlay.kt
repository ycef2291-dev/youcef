package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.ClickaGold
import com.example.ui.theme.ClickaPrimary
import com.example.ui.theme.ClickaSecondary
import com.example.ui.theme.ClickaTertiary
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

data class ActiveGiftAnimation(
    val senderName: String,
    val senderAvatar: String,
    val giftName: String,
    val giftIcon: String,
    val giftId: String,
    val comboCount: Int = 1
)

@Composable
fun TikTokGiftOverlay(
    gift: ActiveGiftAnimation?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (gift == null) return

    // Auto dismiss after 3.8 seconds
    LaunchedEffect(gift) {
        delay(3800)
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // 1. TOP TIKTOK SENDER BANNER
        TopTikTokBanner(
            gift = gift,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 65.dp)
        )

        // 2. CENTRAL ANIMATED STAGE
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp, bottom = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                gift.giftId.contains("lion") -> LionGiftAnimation()
                gift.giftId.contains("galaxy") -> GalaxyGiftAnimation()
                gift.giftId.contains("sports_car") || gift.giftId.contains("car") -> SportsCarGiftAnimation()
                gift.giftId.contains("jet") -> JetGiftAnimation()
                gift.giftId.contains("money_gun") || gift.giftId.contains("money") -> MoneyGunGiftAnimation()
                gift.giftId.contains("falcon") -> FalconGiftAnimation()
                gift.giftId.contains("yacht") -> YachtGiftAnimation()
                gift.giftId.contains("train") -> TrainGiftAnimation()
                else -> SweetGiftAnimation(gift.giftIcon)
            }
        }
    }
}

/**
 * Top TikTok-style sliding banner with avatar, sender name, gift name & combo multiplier
 */
@Composable
private fun TopTikTokBanner(
    gift: ActiveGiftAnimation,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "combo_pulse")
    val comboScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "combo_scale"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = Color(0xDD150E24),
            shadowElevation = 8.dp,
            modifier = Modifier.border(
                width = 1.8.dp,
                brush = Brush.horizontalGradient(
                    listOf(ClickaGold, Color(0xFFFF9100), ClickaPrimary)
                ),
                shape = RoundedCornerShape(26.dp)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                // Sender Avatar
                AsyncImage(
                    model = gift.senderAvatar.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100" },
                    contentDescription = gift.senderName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, ClickaGold, CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = gift.senderName,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "أرسل ${gift.giftName} 🎁",
                        color = ClickaGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Gift Icon Display
                Surface(
                    color = Color(0x44FFD700),
                    shape = CircleShape,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = gift.giftIcon, fontSize = 20.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // TikTok Combo Counter Badge
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFF2A6D),
            shadowElevation = 6.dp,
            modifier = Modifier.scale(comboScale)
        ) {
            Text(
                text = "x${gift.comboCount.coerceAtLeast(1)}",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * 🦁 Lion Animation (أسد تيك توك الملكي)
 */
@Composable
private fun LionGiftAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "lion_rays")
    val rayRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ray_rot"
    )

    val scaleTransition = rememberInfiniteTransition(label = "lion_pulse")
    val lionScale by scaleTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lion_scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Rotating Golden Sunburst Rays
        Canvas(
            modifier = Modifier
                .size(340.dp)
                .rotate(rayRotation)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val rayCount = 14
            for (i in 0 until rayCount) {
                rotate(degrees = (360f / rayCount) * i, pivot = center) {
                    drawLine(
                        brush = Brush.radialGradient(
                            listOf(Color(0x99FFD700), Color(0x33FF9100), Color.Transparent)
                        ),
                        start = center,
                        end = Offset(center.x, 0f),
                        strokeWidth = 32f
                    )
                }
            }
        }

        // Golden Lion Roaring Centerpiece
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(lionScale)
        ) {
            // Floating Crown on Top
            Text(text = "👑", fontSize = 52.sp)

            // Majestic Lion
            Text(
                text = "🦁",
                fontSize = 120.sp,
                modifier = Modifier.offset(y = (-15).dp)
            )

            // Shimmer Title Card
            Surface(
                color = Color(0xCC1A0E28),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .border(1.5.dp, ClickaGold, RoundedCornerShape(16.dp))
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "👑 أسد كليكا الملكي يزأر في البث! 🦁",
                    color = ClickaGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * 🌌 Galaxy Gift Animation (المجرة الكونية)
 */
@Composable
private fun GalaxyGiftAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "galaxy_anim")
    val galaxyRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "galaxy_rot"
    )

    val portalScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "portal_scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Cosmic Swirling Nebula Rings
        Box(
            modifier = Modifier
                .size(310.dp)
                .scale(portalScale)
                .rotate(galaxyRotation)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            Color(0x999400D3),
                            Color(0x9900F0FF),
                            Color(0x99FF007F),
                            Color(0x99FFD700),
                            Color(0x999400D3)
                        )
                    ),
                    CircleShape
                )
        )

        // Center Galaxy Core
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.rotate(-galaxyRotation / 2)
        ) {
            Text(text = "🌌", fontSize = 115.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xDD0B061A),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.border(1.5.dp, Color(0xFF00F0FF), RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "✨ انفجار المجرّة الكونية الخارقة! 🌌",
                    color = Color(0xFF00F0FF),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * 🏎️ Sports Car Gift Animation (سيارة فيراري الرياضية)
 */
@Composable
private fun SportsCarGiftAnimation() {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(2800, easing = FastOutSlowInEasing)
        )
    }

    val carOffsetX = (animProgress.value * 500f - 250f)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(x = carOffsetX.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💨", fontSize = 42.sp)
                Text(text = "🏎️", fontSize = 95.sp)
                Text(text = "🏁", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xDD1E0000),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.border(1.5.dp, Color(0xFFFF2A2A), RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "🔥 وصول سيارة الفيراري الرياضية! 🏎️",
                    color = Color(0xFFFF4D4D),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * ✈️ Private Jet Animation (طائرة نفاثة)
 */
@Composable
private fun JetGiftAnimation() {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(2700, easing = LinearOutSlowInEasing)
        )
    }

    val jetOffsetX = (animProgress.value * 450f - 225f)
    val jetOffsetY = (-animProgress.value * 220f + 110f)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(x = jetOffsetX.dp, y = jetOffsetY.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "☁️", fontSize = 38.sp)
                Text(text = "✈️", fontSize = 90.sp, modifier = Modifier.rotate(-20f))
                Text(text = "✨", fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xDD0D1B2A),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.border(1.5.dp, Color(0xFF48CAE4), RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "✈️ طائرة الملوك النفاثة تحلّق في سماء الغرفة! 👑",
                    color = Color(0xFF90E0EF),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * 💸 Money Gun Gift Animation (مسدس الأموال)
 */
@Composable
private fun MoneyGunGiftAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "money_gun")
    val gunScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(280, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gun_scale"
    )

    // Falling dollar bills
    val billPositions = remember {
        List(24) { i ->
            val angle = (i * 15) * (Math.PI / 180.0)
            val dist = 50 + (i * 7)
            Pair(
                (cos(angle) * dist).toFloat(),
                (sin(angle) * dist).toFloat()
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Floating Money Bills Burst
        billPositions.forEachIndexed { idx, pos ->
            val emoji = if (idx % 3 == 0) "💵" else if (idx % 3 == 1) "💸" else "💰"
            Text(
                text = emoji,
                fontSize = 24.sp,
                modifier = Modifier.offset(x = pos.first.dp, y = pos.second.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(gunScale)
        ) {
            Text(text = "🔫", fontSize = 90.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xDD062810),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.border(1.5.dp, Color(0xFF00FF66), RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "💸 أمطار من الأموال تنهال على الغرفة! 🎉",
                    color = Color(0xFF00FF66),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * 🦅 Falcon Gift Animation (الصقر الملكي)
 */
@Composable
private fun FalconGiftAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "falcon_wings")
    val flapScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flap_scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(flapScale)
        ) {
            Text(text = "👑", fontSize = 42.sp)
            Text(text = "🦅", fontSize = 110.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xDD2A1F0D),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.border(1.5.dp, ClickaGold, RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "🦅 الصقر الملكي الأصيل يحلّق شامخاً! 👑",
                    color = ClickaGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * 🛥️ Yacht Gift Animation (يخت الملوك)
 */
@Composable
private fun YachtGiftAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "yacht_rock")
    val waveRotation by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_rot"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.rotate(waveRotation)
        ) {
            Text(text = "🛥️", fontSize = 100.sp)
            Text(text = "🌊🌊🌊", fontSize = 28.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xDD0D1B2A),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.border(1.5.dp, ClickaGold, RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "🛥️ يخت الملوك الفاخر يرسو في الغرفة! 👑",
                    color = ClickaGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * 🚅 Train Gift Animation (قطار كليكا السريع)
 */
@Composable
private fun TrainGiftAnimation() {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(2600, easing = LinearEasing)
        )
    }

    val trainOffset = (animProgress.value * 460f - 230f)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(x = trainOffset.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🚅", fontSize = 95.sp)
                Text(text = "🎈", fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xDD1E1730),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.border(1.5.dp, ClickaTertiary, RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "🚅 قطار كليكا السريع يمر عبر البث! 🎉",
                    color = ClickaTertiary,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * 🌹 Sweet Gift Animation (Roses, Hearts, Teddy, Perfume, etc.)
 */
@Composable
private fun SweetGiftAnimation(giftIcon: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "sweet_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Floating sparkle particles
    val particleOffsets = remember {
        List(18) { i ->
            val angle = (i * 20) * (Math.PI / 180.0)
            val dist = 45 + (i * 6)
            Pair((cos(angle) * dist).toFloat(), (sin(angle) * dist).toFloat())
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        particleOffsets.forEachIndexed { idx, pos ->
            val particle = if (idx % 3 == 0) "💖" else if (idx % 3 == 1) "✨" else "🌸"
            Text(
                text = particle,
                fontSize = 20.sp,
                modifier = Modifier.offset(x = pos.first.dp, y = pos.second.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(pulseScale)
        ) {
            Text(text = giftIcon, fontSize = 95.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xDD2A0D20),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.border(1.5.dp, Color(0xFFFF69B4), RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "💖 هدية فاخرة ومميزة من القلب! 🌹",
                    color = Color(0xFFFFB6C1),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}
