package com.mobiledelivery.presentation.screens.support

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Кольори
private val OrangeAccent = Color(0xFFFF6B35)
private val BackgroundColor = Color(0xFFFAF8F5)
private val CardBackground = Color.White
private val DarkText = Color(0xFF1A1A1A)
private val GrayText = Color(0xFF9E9E9E)
private val LightBorder = Color(0xFFE8E6E3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    
    // Посилання на Telegram бота підтримки
    val telegramBotUsername = "fooodDelivery_bot"
    
    fun openTelegram() {
        try {
            // Спробуємо відкрити через Telegram app
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=$telegramBotUsername"))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Якщо Telegram не встановлено, відкриваємо через браузер
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$telegramBotUsername"))
                context.startActivity(intent)
            } catch (e2: Exception) {
                // Якщо і це не спрацювало, показуємо повідомлення
                android.widget.Toast.makeText(
                    context,
                    "Telegram не встановлено. Встановіть Telegram для зв'язку з підтримкою.",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Служба підтримки",
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = DarkText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Іконка Telegram
            Surface(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(60.dp),
                color = OrangeAccent.copy(alpha = 0.15f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Message,
                        contentDescription = null,
                        tint = OrangeAccent,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            
            Text(
                text = "Зв'яжіться з нами через Telegram",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Натисніть кнопку нижче, щоб відкрити Telegram та написати нашій службі підтримки",
                fontSize = 16.sp,
                color = GrayText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Кнопка відкриття Telegram
            Button(
                onClick = { openTelegram() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeAccent
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Message,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Відкрити Telegram",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Інформаційна картка
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = OrangeAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Додаткова інформація",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                    }
                    
                    Divider(color = LightBorder)
                    
                    ContactInfoItem(
                        icon = Icons.Outlined.Schedule,
                        title = "Години роботи",
                        value = "Пн-Нд: 09:00 - 21:00"
                    )
                    
                    ContactInfoItem(
                        icon = Icons.Outlined.AccessTime,
                        title = "Час відповіді",
                        value = "Зазвичай відповідаємо протягом 1 години"
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OrangeAccent,
            modifier = Modifier.size(20.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = GrayText
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkText
            )
        }
    }
}

