package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.GiftItem
import com.example.data.model.RoomEntity
import com.example.data.model.UserEntity
import com.example.ui.components.DecoratedAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.FlexyBundle
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateRoomDialog(
    errorMessage: String?,
    onDismiss: () -> Unit,
    onCreate: (name: String, imageUrl: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=400") }

    val presetImages = listOf(
        "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=400",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
        "https://images.unsplash.com/photo-1478737270239-2f02b77fc618?w=400",
        "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClickaCardBg,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddBusiness, contentDescription = null, tint = ClickaSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("إنشاء غرفة بث جديدة 🎙️", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = ClickaPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ملاحظة: يمكنك إنشاء غرفة واحدة فقط خاصة بك في تطبيق كليكا.",
                        color = ClickaTertiary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الغرفة") },
                    placeholder = { Text("مثال: ديوانية كليكا والموسيقى 🎵") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ClickaPrimary,
                        unfocusedBorderColor = ClickaDivider,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("اختر صورة للغرفة:", color = ClickaTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    presetImages.forEach { url ->
                        val isSelected = imageUrl == url
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { imageUrl = url }
                                .then(
                                    if (isSelected) Modifier.background(ClickaSecondary).padding(2.dp).clip(RoundedCornerShape(10.dp))
                                    else Modifier
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, imageUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = ClickaSecondary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("إنشاء الغرفة الآن ✨", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = ClickaTextSecondary)
            }
        }
    )
}

@Composable
fun EditProfileDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onSave: (name: String, bio: String, avatarUrl: String) -> Unit
) {
    var name by remember { mutableStateOf(user.displayName) }
    var bio by remember { mutableStateOf(user.bio) }
    var avatarUrl by remember { mutableStateOf(user.avatarUrl) }

    val presetAvatars = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200",
        "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=200"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClickaCardBg,
        title = {
            Text("تعديل الحساب ✏️", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("اختر الصورة الشخصية:", color = ClickaTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    presetAvatars.forEach { url ->
                        val isSelected = avatarUrl == url
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .clickable { avatarUrl = url }
                                .then(
                                    if (isSelected) Modifier.background(ClickaPrimary).padding(2.dp).clip(CircleShape)
                                    else Modifier
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("الاسم المستعار") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ClickaPrimary,
                        unfocusedBorderColor = ClickaDivider,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("الحالة / النبذة الشخصية") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ClickaPrimary,
                        unfocusedBorderColor = ClickaDivider,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, bio, avatarUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = ClickaPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ التعديلات", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = ClickaTextSecondary)
            }
        }
    )
}

/**
 * Wallet Dialog: Displays coins count (النقود تظهر في المحفظة وفي الهدية فقط)
 * and DZD Flexy recharge bundles.
 */
@Composable
fun WalletDialog(
    user: UserEntity,
    bundles: List<FlexyBundle>,
    onSelectBundle: (FlexyBundle) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClickaCardBg,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = ClickaGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("محفظتي 🪙", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Coins balance card (shown ONLY here & gifts)
                Surface(
                    color = Color(0xFF241C10),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ClickaGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("رصيد العملات الحالي:", color = Color(0xFFFFE082), fontSize = 12.sp)
                        Text("${user.walletCoins} عملة", color = ClickaGold, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color(0xFF3D2F17))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("الماسات والجوائز: 💎 ${user.walletDiamonds} ماسة", color = ClickaTertiary, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("شحن العملات بالدينار الجزائري (فليكسي):", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                bundles.forEach { bundle ->
                    Surface(
                        color = if (bundle.isPopular) Color(0xFF2B1D3D) else Color(0xFF1E182F),
                        shape = RoundedCornerShape(10.dp),
                        border = if (bundle.isPopular) androidx.compose.foundation.BorderStroke(1.dp, ClickaSecondary) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSelectBundle(bundle) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("🪙 ${bundle.coinsAmount} عملة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${bundle.dzdAmount} د.ج فليكسي", color = ClickaGold, fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onSelectBundle(bundle) },
                                colors = ButtonDefaults.buttonColors(containerColor = if (bundle.isPopular) ClickaSecondary else ClickaGold),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${bundle.dzdAmount} د.ج",
                                    color = if (bundle.isPopular) Color.White else Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ClickaPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("إغلاق")
            }
        }
    )
}

/**
 * Flexy Payment Dialog:
 * Details target number +213793474663, copy button, sender phone input, instant recharge!
 */
@Composable
fun FlexyRechargeDialog(
    bundle: FlexyBundle,
    targetPhoneNumber: String = "+213793474663",
    onDismiss: () -> Unit,
    onConfirm: (senderPhone: String) -> Unit
) {
    var senderPhone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClickaCardBg,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = ClickaGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("شحن فليكسي بالدينار 🇩🇿", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = Color(0xFF1E1730),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "الباقة المختارة: ${bundle.coinsAmount} عملة مقابل ${bundle.dzdAmount} د.ج",
                            color = ClickaGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "شحن آلي وسريع عبر بوابة فليكسي المشفرة. يتم تحويل الفليكسي مباشرة وإيداع ${bundle.coinsAmount} عملة في محفظتك فوراً.",
                            color = ClickaTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("أدخل رقم هاتفك لتأكيد الشحن الفوري:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = senderPhone,
                    onValueChange = { senderPhone = it },
                    placeholder = { Text("مثال: 0793474663") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ClickaGold,
                        unfocusedBorderColor = ClickaDivider,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = ClickaTertiary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("عملية شحن مشفرة ومؤمنة بالكامل تلقائياً", color = ClickaTextTertiary, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val phone = if (senderPhone.isNotBlank()) senderPhone else "0793474663"
                    onConfirm(phone)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ClickaGold),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("تأكيد الشحن الفوري ⚡", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = ClickaTextSecondary)
            }
        }
    )
}

/**
 * Clicka Store Dialog (متجر كليكا في حسابي):
 * - Special User ID: 11111, 22222, 33333, etc. (2000 coins, 1600 VIP)
 * - Special Room ID (2000 coins, 1600 VIP)
 * - Avatar decorations: crown (1999), rainbow (2999), butterfly (2999), kiss (2999)
 * - VIP Pass: 19999 coins for 1 week (20% discount on all items + VIP badges)
 * - Room 4-digit PIN lock (2000 coins)
 * - YouTube TV
 */
@Composable
fun StoreDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onBuySpecialUserId: (String) -> Unit,
    onBuySpecialRoomId: (String) -> Unit,
    onBuyAvatarFrame: (frameType: String, price: Int, title: String) -> Unit,
    onBuyVip: () -> Unit,
    onBuyRoomPin: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("VIP") } // VIP, USER_ID, ROOM_ID, FRAMES, PIN
    var pinInput by remember { mutableStateOf("") }

    val specialIds = listOf("11111", "22222", "33333", "44444", "66666", "77777", "88888", "99999")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF130D22),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🛍️", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("متجر كليكا الحصري", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
                if (user.isVip) {
                    Surface(
                        color = ClickaGold,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "خصم VIP 20%",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Category Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1D1432), RoundedCornerShape(12.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(
                        "VIP" to "👑 VIP",
                        "USER_ID" to "ID حساب",
                        "ROOM_ID" to "ID غرفة",
                        "FRAMES" to "زينة بروفايل",
                        "PIN" to "قفل الغرفة"
                    ).forEach { (code, title) ->
                        val isSelected = selectedCategory == code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ClickaSecondary else Color.Transparent)
                                .clickable { selectedCategory = code }
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (isSelected) Color.White else ClickaTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedCategory) {
                    "VIP" -> {
                        Surface(
                            color = Color(0xFF26193E),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, ClickaGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("👑", fontSize = 42.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("عضوية VIP الملكية", color = ClickaGold, fontWeight = FontWeight.Black, fontSize = 17.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• خصم 20% على كل ما يباع في التطبيق\n• شارة VIP ذهبية تظهر على صورتك الشخصية\n• شارة VIP مميزة على غرفتك الصوتية\n• المدة: أسبوع كامل (7 أيام)",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onBuyVip,
                                    colors = ButtonDefaults.buttonColors(containerColor = ClickaGold),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("شراء VIP (19999 عملة)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    "USER_ID" -> {
                        Text(
                            text = "شراء ID حساب مميز (5 أرقام) لمدة أسبوع بسعر ${if (user.isVip) "1600 (VIP)" else "2000"} عملة:",
                            color = ClickaTextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(220.dp)
                        ) {
                            items(specialIds) { spId ->
                                Surface(
                                    color = Color(0xFF211736),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ClickaTertiary.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("ID: $spId", color = ClickaTertiary, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Button(
                                            onClick = { onBuySpecialUserId(spId) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ClickaPrimary),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(if (user.isVip) "1600 🪙" else "2000 🪙", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "ROOM_ID" -> {
                        Text(
                            text = "شراء ID غرفة مميز (5 أرقام) لغرفتك لمدة أسبوع بسعر ${if (user.isVip) "1600 (VIP)" else "2000"} عملة:",
                            color = ClickaTextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(220.dp)
                        ) {
                            items(specialIds) { spId ->
                                Surface(
                                    color = Color(0xFF211736),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ClickaSecondary.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("ID: $spId", color = ClickaSecondary, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Button(
                                            onClick = { onBuySpecialRoomId(spId) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ClickaSecondary),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(if (user.isVip) "1600 🪙" else "2000 🪙", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "FRAMES" -> {
                        val frames = listOf(
                            Triple("crown", "تاج فوق الصورة 👑", 1999),
                            Triple("rainbow", "ألوان قوس قزح متحركة 🌈", 2999),
                            Triple("butterfly", "دائرة مع فراشة 🦋", 2999),
                            Triple("kiss", "دائرة رومانسية مع قبلة 💋", 2999)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            frames.forEach { (frameType, title, price) ->
                                val finalPrice = if (user.isVip) (price * 0.8).toInt() else price
                                Surface(
                                    color = Color(0xFF221838),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            DecoratedAvatar(
                                                avatarUrl = user.avatarUrl,
                                                displayName = user.displayName,
                                                avatarFrame = frameType,
                                                isVip = user.isVip,
                                                isAdmin = user.isAdmin,
                                                size = 36.dp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = { onBuyAvatarFrame(frameType, price, title) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ClickaSecondary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("$finalPrice 🪙", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "PIN" -> {
                        Surface(
                            color = Color(0xFF26193E),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("🔒 شراء قفل للغرفة (4 أرقام)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "السعر: ${if (user.isVip) "1600" else "2000"} عملة. لن يتمكن أي شخص من دخول غرفتك إلا بإدخال رمز PIN.",
                                    color = ClickaTextSecondary,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = pinInput,
                                    onValueChange = { if (it.length <= 4) pinInput = it },
                                    label = { Text("رمز PIN (4 أرقام)") },
                                    placeholder = { Text("1234") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ClickaGold,
                                        unfocusedBorderColor = ClickaDivider,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { onBuyRoomPin(pinInput) },
                                    enabled = pinInput.length == 4,
                                    colors = ButtonDefaults.buttonColors(containerColor = ClickaGold),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("شراء وتفعيل القفل (${if (user.isVip) "1600" else "2000"} عملة)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ClickaPrimary)) {
                Text("إغلاق")
            }
        }
    )
}

/**
 * Locked Room Prompt Dialog: enters 4-digit PIN code to enter room.
 */
@Composable
fun EnterRoomPinDialog(
    room: RoomEntity,
    onDismiss: () -> Unit,
    onSubmitPin: (String) -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClickaCardBg,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = ClickaGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("الغرفة مقفولة برمز PIN 🔒", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "هذه الغرفة (${room.name}) محمية برمز قفل سري مكون من 4 أرقام. يرجى إدخال الرمز للدخول:",
                    color = ClickaTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = enteredPin,
                    onValueChange = {
                        if (it.length <= 4) {
                            enteredPin = it
                            hasError = false
                        }
                    },
                    placeholder = { Text("••••") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ClickaGold,
                        unfocusedBorderColor = ClickaDivider,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (hasError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("رمز PIN غير صحيح، يرجى التأكد وإعادة المحاولة", color = Color(0xFFFF5252), fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (enteredPin == room.pinCode) {
                        onSubmitPin(enteredPin)
                    } else {
                        hasError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ClickaGold),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("دخول الغرفة 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = ClickaTextSecondary)
            }
        }
    )
}

@Composable
fun SettingsDialog(
    currentLanguage: String,
    user: UserEntity? = null,
    onLanguageChange: (String) -> Unit,
    onPrivacyChange: ((allowMessages: Boolean, allowRequests: Boolean) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var allowPrivateMessages by remember(user?.allowPrivateMessages) { mutableStateOf(user?.allowPrivateMessages ?: true) }
    var allowFriendRequests by remember(user?.allowFriendRequests) { mutableStateOf(user?.allowFriendRequests ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClickaCardBg,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = ClickaPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("إعدادات التطبيق والخصوصية ⚙️", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Language selector requested: عربي او فرنسي
                Text("لغة التطبيق / Langue de l'application:", color = ClickaGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = currentLanguage == "ar",
                        onClick = { onLanguageChange("ar") },
                        label = { Text("العربية 🇩🇿", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ClickaPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = currentLanguage == "fr",
                        onClick = { onLanguageChange("fr") },
                        label = { Text("Français 🇫🇷", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ClickaSecondary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(color = ClickaDivider, modifier = Modifier.padding(vertical = 12.dp))

                // Privacy Settings Section
                Text("🔒 الخصوصية والأمان:", color = ClickaGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("استقبال الرسائل الخاصة", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("عند الإيقاف، لن يستطيع التحدث معك في الخاص إلا الأصدقاء فقط", color = ClickaTextSecondary, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = allowPrivateMessages,
                        onCheckedChange = {
                            allowPrivateMessages = it
                            onPrivacyChange?.invoke(allowPrivateMessages, allowFriendRequests)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("استقبال طلبات الصداقة", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("قفل أو فتح إمكانية إرسال طلبات الصداقة لحسابك", color = ClickaTextSecondary, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = allowFriendRequests,
                        onCheckedChange = {
                            allowFriendRequests = it
                            onPrivacyChange?.invoke(allowPrivateMessages, allowFriendRequests)
                        }
                    )
                }

                Divider(color = ClickaDivider, modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("الإشعارات والتنبيهات", color = Color.White, fontSize = 13.sp)
                    Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
                }
                Divider(color = ClickaDivider, modifier = Modifier.padding(vertical = 6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("المؤثرات الصوتية والتكبيس", color = Color.White, fontSize = 13.sp)
                    Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ClickaPrimary)) {
                Text("تم")
            }
        }
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClickaCardBg,
        title = {
            Text("حول تطبيق كليكا (Clicka) 📱", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "كليكا (Clicka Live)",
                    color = ClickaSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "الإصدار 1.0.0 • منصة البث المباشر والغرف الصوتية التفاعلية",
                    color = ClickaTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "تطبيق كليكا منصة جزائرية وعالمية تتيح إنشاء غرف تفاعلية، مشاهدة فيديوهات اليوتيوب المشتركة، التحدث عبر 4 مقاعد ميكروفون، الشحن بالدينار الجزائري عبر فليكسي (+213793474663)، شراء آيديهات مميزة وزينات البروفايل وعضويات VIP.",
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ClickaPrimary)) {
                Text("حسناً")
            }
        }
    )
}

@Composable
fun ShareDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClickaCardBg,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Share, contentDescription = null, tint = ClickaTertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("شارك مع الأصدقاء 🔗", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ادعُ أصدقاءك للانضمام إلى كليكا والاستمتاع بالغرف الصوتية ومشاهدة التلفاز معاً!",
                    color = Color.White,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFF1B142B),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "https://clicka.live/invite/app",
                        color = ClickaTertiary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ClickaPrimary)) {
                Text("نسخ الرابط ومشاركة")
            }
        }
    )
}

@Composable
fun TvUrlDialog(
    currentUrl: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var urlInput by remember { mutableStateOf(currentUrl) }

    val presetLinks = listOf(
        "موسيقى لوفي مهدئة 🎵" to "https://www.youtube.com/watch?v=jfKfPfyJRdk",
        "بث مباشر مكة المكرمة 🕋" to "https://www.youtube.com/watch?v=cM_lC7zM5g8",
        "سهرة استرخاء وطبيعة 🌿" to "https://www.youtube.com/watch?v=5qap5aO4i9A"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClickaCardBg,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tv, contentDescription = null, tint = ClickaTertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تغيير فيديو تلفاز الغرفة 📺", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "انسخ أي رابط يوتيوب وضعه هنا لتشغيله لجميع أعضاء الغرفة على التلفاز:",
                    color = ClickaTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    placeholder = { Text("https://www.youtube.com/watch?v=...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ClickaTertiary,
                        unfocusedBorderColor = ClickaDivider,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("أو اختر من البثوث الشائعة:", color = ClickaTextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                presetLinks.forEach { (title, link) ->
                    Surface(
                        color = Color(0xFF1E1730),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { urlInput = link }
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(urlInput) },
                colors = ButtonDefaults.buttonColors(containerColor = ClickaTertiary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("تشغيل على التلفاز ▶️", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = ClickaTextSecondary)
            }
        }
    )
}

/**
 * TikTok-style Gifts Bottom Sheet:
 * Shows coins count HERE (النقود تظهر في المحفظة وفي الهدية فقط)
 * 4 columns, category tabs, selection indicator, and vibrant TikTok send button!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftsBottomSheet(
    gifts: List<GiftItem>,
    userCoins: Int,
    isVip: Boolean = false,
    onSendGift: (GiftItem) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedGift by remember { mutableStateOf(gifts.firstOrNull()) }
    var selectedCategory by remember { mutableStateOf("الكل") }

    val filteredGifts = remember(selectedCategory, gifts) {
        when (selectedCategory) {
            "كلاسيك 🌹" -> gifts.filter { it.coinPrice < 100 }
            "رائج 🔥" -> gifts.filter { it.coinPrice in 100..4999 }
            "ملكي 👑" -> gifts.filter { it.coinPrice >= 5000 }
            else -> gifts
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF140E1E),
        dragHandle = { BottomSheetDefaults.DragHandle(color = ClickaTextSecondary) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Header: Title and Coins Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎁", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "هدايا كليكا لايف (TikTok Gifts)",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }

                // Coins balance in Gift sheet as requested
                Surface(
                    color = Color(0xFF261C3B),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ClickaGold.copy(alpha = 0.4f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "🪙 $userCoins عملة", color = ClickaGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips (الكل, كلاسيك, رائج, ملكي)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("الكل", "كلاسيك 🌹", "رائج 🔥", "ملكي 👑").forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        color = if (isSelected) Color(0xFFE91E63) else Color(0xFF201730),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else ClickaTextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4-Column Grid like TikTok
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
            ) {
                items(filteredGifts, key = { it.id }) { gift ->
                    val actualPrice = if (isVip) (gift.coinPrice * 0.80).toInt() else gift.coinPrice
                    val canAfford = userCoins >= actualPrice
                    val isSelected = selectedGift?.id == gift.id

                    Surface(
                        color = when {
                            isSelected -> Color(0xFF2E1A3D)
                            canAfford -> Color(0xFF1E162B)
                            else -> Color(0xFF140F1F)
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE91E63)) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedGift = gift
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = gift.emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = gift.name,
                                color = if (isSelected) Color(0xFFFF80AB) else if (canAfford) Color.White else ClickaTextTertiary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "🪙 $actualPrice",
                                color = if (canAfford) ClickaGold else ClickaTextTertiary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Action Row: Selected Gift summary & Send Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedGift != null) {
                    val currentGift = selectedGift!!
                    val actualPrice = if (isVip) (currentGift.coinPrice * 0.80).toInt() else currentGift.coinPrice
                    val canAfford = userCoins >= actualPrice

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = currentGift.emoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = currentGift.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "السعر: $actualPrice عملة",
                                color = ClickaGold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (canAfford) {
                                onSendGift(currentGift)
                            }
                        },
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE91E63),
                            disabledContainerColor = Color(0xFF3B273A)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (canAfford) "إرسال 🎁" else "رصيد غير كافٍ",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Daily Reward Dialog (الهدايا اليومية):
 * Every day user gets 5 coins (5 قطع كل يوم في الأسبوع).
 * When user opens the app, this notification dialog pops up!
 */
@Composable
fun DailyRewardDialog(
    user: UserEntity,
    onClaim: () -> Unit,
    onDismiss: () -> Unit
) {
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val isAlreadyClaimed = user.lastDailyRewardDate == today
    val daysOfWeek = listOf("اليوم 1", "اليوم 2", "اليوم 3", "اليوم 4", "اليوم 5", "اليوم 6", "اليوم 7")
    val currentDayIndex = ((user.dailyRewardDayOfWeek - 1).coerceIn(0, 6))

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1B1429),
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🎁", fontSize = 46.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "الهدايا اليومية الأسبوعية",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Text(
                    text = "استلم 5 عملات مجاناً كل يوم في الأسبوع! 🪙",
                    color = ClickaGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(8.dp))
                // 7 Days row / grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    daysOfWeek.take(4).forEachIndexed { index, dayName ->
                        val isCurrent = index == currentDayIndex
                        val isPassed = index < currentDayIndex || (index == currentDayIndex && isAlreadyClaimed)
                        DayRewardCard(
                            dayName = dayName,
                            coins = 5,
                            isCurrent = isCurrent,
                            isClaimed = isPassed,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    daysOfWeek.drop(4).forEachIndexed { index, dayName ->
                        val actualIndex = index + 4
                        val isCurrent = actualIndex == currentDayIndex
                        val isPassed = actualIndex < currentDayIndex || (actualIndex == currentDayIndex && isAlreadyClaimed)
                        DayRewardCard(
                            dayName = dayName,
                            coins = 5,
                            isCurrent = isCurrent,
                            isClaimed = isPassed,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Color(0xFF261D3B),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "✨", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAlreadyClaimed) "تم استلام مكافأة اليوم (5 عملات)! عُد غداً للمزيد." else "هدية اليوم جاهزة للاستلام الآن! اضغط على الزر بالأسفل.",
                            color = if (isAlreadyClaimed) ClickaSecondary else Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onClaim,
                enabled = !isAlreadyClaimed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ClickaGold,
                    disabledContainerColor = Color(0xFF332948)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isAlreadyClaimed) "تم استلام هدية اليوم بنجاح ✅" else "استلام 5 عملات الآن 🪙",
                    color = if (isAlreadyClaimed) ClickaTextSecondary else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("إغلاق", color = ClickaTextSecondary)
            }
        }
    )
}

@Composable
private fun DayRewardCard(
    dayName: String,
    coins: Int,
    isCurrent: Boolean,
    isClaimed: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = when {
            isClaimed -> Color(0xFF2B3A2C)
            isCurrent -> Color(0xFF42285E)
            else -> Color(0xFF1E1730)
        },
        border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, ClickaGold) else null,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(dayName, color = if (isCurrent) ClickaGold else ClickaTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(if (isClaimed) "✅" else "🪙", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text("+$coins", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
    }
}
