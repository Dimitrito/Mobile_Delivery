package com.mobiledelivery.presentation.screens.courier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobiledelivery.domain.models.Delivery
import com.mobiledelivery.presentation.states.UiState
import com.mobiledelivery.presentation.viewmodels.CourierViewModel

// Кольори
private val OrangeAccent = Color(0xFFFF6B35)
private val GreenAccent = Color(0xFF4CAF50)
private val BackgroundColor = Color(0xFFFAF8F5)
private val CardBackground = Color.White
private val DarkText = Color(0xFF1A1A1A)
private val GrayText = Color(0xFF9E9E9E)

@Composable
fun CourierOrderDetailScreen(
    delivery: Delivery,
    courierViewModel: CourierViewModel,
    onNavigateBack: () -> Unit
) {
    val markDeliveryState by courierViewModel.markDeliveryState.collectAsStateWithLifecycle()
    
    LaunchedEffect(markDeliveryState) {
        if (markDeliveryState is UiState.Success) {
            // Після успішного позначення доставки повертаємось назад
            onNavigateBack()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = DarkText
                    )
                }
                Text(
                    text = "Замовлення #${delivery.orderId}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Order Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Інформація про замовлення",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        
                        Divider(color = GrayText.copy(alpha = 0.3f))
                        
                        InfoRow(
                            icon = Icons.Default.LocationOn,
                            label = "Адреса доставки",
                            value = delivery.deliveryAddress
                        )
                        
                        if (delivery.customerPhoneNumber != null) {
                            InfoRow(
                                icon = Icons.Default.Phone,
                                label = "Телефон клієнта",
                                value = delivery.customerPhoneNumber
                            )
                        }
                        
                        InfoRow(
                            icon = Icons.Default.Schedule,
                            label = "Статус",
                            value = delivery.deliveryStatus.name.replace("_", " ").uppercase()
                        )
                        
                        InfoRow(
                            icon = Icons.Default.AccessTime,
                            label = "Час початку",
                            value = delivery.startTime
                        )
                    }
                }
                
                // Deliver Button
                Button(
                    onClick = {
                        courierViewModel.markDeliveryAsDelivered(delivery.id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                    enabled = markDeliveryState !is UiState.Loading
                ) {
                    if (markDeliveryState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Text(
                                text = "Позначити як доставлено",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                
                val errorState = markDeliveryState
                if (errorState is UiState.Error) {
                    Text(
                        text = errorState.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OrangeAccent,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = GrayText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkText
            )
        }
    }
}

