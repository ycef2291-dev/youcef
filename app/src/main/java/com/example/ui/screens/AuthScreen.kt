package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    isRegisterMode: Boolean,
    onToggleMode: (Boolean) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    contact: String,
    onContactChange: (String) -> Unit,
    isGmail: Boolean,
    onToggleIsGmail: (Boolean) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF190B2F), // Deep Royal Purple
            Color(0xFF0F081D), // Dark Midnight
            Color(0xFF1C0929)  // Rich Magenta Dark
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .testTag("auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    })
                }
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo & Header with beautiful colors
            Surface(
                color = ClickaPrimary.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(76.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = "Clicka Live",
                        tint = ClickaSecondary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "كليكا",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = ClickaLiveRed,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = if (isRegisterMode) "أنشئ حسابك الجديد وانضم إلى عالم كليكا" else "أهلاً بك مجدداً! ادخل للاستمتاع بالغرف",
                color = ClickaTextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
            )

            // Auth Card
            Card(
                colors = CardDefaults.cardColors(containerColor = ClickaCardBg),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mode Selector Tabs (تسجيل الدخول / إنشاء حساب)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF100D1B), RoundedCornerShape(14.dp))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isRegisterMode) ClickaPrimary else Color.Transparent)
                                .clickable { onToggleMode(false) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "تسجيل الدخول",
                                color = if (!isRegisterMode) Color.White else ClickaTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isRegisterMode) ClickaSecondary else Color.Transparent)
                                .clickable { onToggleMode(true) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "إنشاء حساب جديد",
                                color = if (isRegisterMode) Color.White else ClickaTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Error banner if any
                    AnimatedVisibility(visible = errorMessage != null) {
                        Surface(
                            color = Color(0xFF4A101D),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFFF5252),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage ?: "",
                                    color = Color(0xFFFFCDD2),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Field: Username / Name
                    OutlinedTextField(
                        value = username,
                        onValueChange = onUsernameChange,
                        label = { Text("ادخل الاسم / اسم المستخدم") },
                        placeholder = { Text("مثال: كريم_الجزائري") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = ClickaPrimary)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ClickaPrimary,
                            unfocusedBorderColor = ClickaDivider,
                            focusedLabelColor = ClickaPrimary,
                            unfocusedLabelColor = ClickaTextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_username_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Field: Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("ادخل كلمة السر") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = ClickaSecondary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "عرض كلمة المرور",
                                    tint = ClickaTextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (isRegisterMode) ImeAction.Next else ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                onSubmit()
                            }
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ClickaSecondary,
                            unfocusedBorderColor = ClickaDivider,
                            focusedLabelColor = ClickaSecondary,
                            unfocusedLabelColor = ClickaTextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input")
                    )

                    // Registration specific fields: Gmail or Phone
                    if (isRegisterMode) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Toggle Phone or Gmail
                        Text(
                            text = "اختر طريقة التسجيل (جمايل أو رقم الهاتف):",
                            color = ClickaTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            FilterChip(
                                selected = isGmail,
                                onClick = { onToggleIsGmail(true) },
                                label = { Text("بريد جمايل (Gmail)") },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ClickaPrimary.copy(alpha = 0.3f),
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f).padding(end = 4.dp)
                            )
                            FilterChip(
                                selected = !isGmail,
                                onClick = { onToggleIsGmail(false) },
                                label = { Text("رقم الهاتف") },
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ClickaSecondary.copy(alpha = 0.3f),
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f).padding(start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = contact,
                            onValueChange = onContactChange,
                            label = { Text(if (isGmail) "ادخل بريد الجمايل" else "ادخل رقم الهاتف") },
                            placeholder = { Text(if (isGmail) "user@gmail.com" else "0661234567") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isGmail) Icons.Default.AlternateEmail else Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = ClickaTertiary
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = if (isGmail) KeyboardType.Email else KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    onSubmit()
                                }
                            ),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ClickaTertiary,
                                unfocusedBorderColor = ClickaDivider,
                                focusedLabelColor = ClickaTertiary,
                                unfocusedLabelColor = ClickaTextSecondary,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_contact_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button with vibrant gradient
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onSubmit()
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_submit_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = if (isRegisterMode) listOf(ClickaSecondary, ClickaPrimary)
                                        else listOf(ClickaPrimary, ClickaTertiary)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = if (isRegisterMode) "تأكيد وإنشاء الحساب ✨" else "تسجيل الدخول إلى كليكا 🚀",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom helper toggle
                    TextButton(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onToggleMode(!isRegisterMode)
                        },
                        modifier = Modifier.testTag("auth_toggle_mode_link")
                    ) {
                        Text(
                            text = if (isRegisterMode) "لديك حساب بالفعل؟ سجل دخولك الآن" else "ليس لديك حساب؟ إنشاء حساب جديد الآن",
                            color = ClickaTertiary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Auto-login persistence info notice
            Surface(
                color = Color(0xFF140E26),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ClickaSuccess,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تسجيل الدخول يتم لمرة واحدة، وفي المرات القادمة ستدخل مباشرة لتطبيق كليكا دون طلب كلمة السر.",
                        color = ClickaTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
