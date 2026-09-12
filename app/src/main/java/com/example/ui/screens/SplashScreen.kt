package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClickaLiveRed
import com.example.ui.theme.ClickaPrimary
import com.example.ui.theme.ClickaSecondary

@Composable
fun SplashScreen(
    remainingSeconds: Int,
    modifier: Modifier = Modifier
) {
    // Pulse animation for LIVE badge
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000)) // Pure black screen as requested
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Live indicator badge
            Surface(
                color = ClickaLiveRed.copy(alpha = 0.2f),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .scale(pulseScale)
                    .testTag("splash_live_badge")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(ClickaLiveRed, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE",
                        color = ClickaLiveRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // French App Name "Clicka"
            Text(
                text = "Clicka",
                color = Color.White,
                fontSize = 54.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 3.sp,
                modifier = Modifier
                    .alpha(glowAlpha)
                    .testTag("splash_title_french")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "كليكا • البث المباشر والغرف الصوتية",
                color = Color(0xFFA09CBD),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(64.dp))

            // 5 Seconds Countdown Visual
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(180.dp)
            ) {
                LinearProgressIndicator(
                    progress = { (5 - remainingSeconds) / 5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = ClickaSecondary,
                    trackColor = Color(0xFF222222)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "جاري التحميل... ($remainingSeconds ثواني)",
                    color = Color(0xFF777777),
                    fontSize = 12.sp
                )
            }
        }
    }
}
