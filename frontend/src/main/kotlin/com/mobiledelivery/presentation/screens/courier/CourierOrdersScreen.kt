package com.mobiledelivery.presentation.screens.courier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
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
private val LightBorder = Color(0xFFE8E6E3)

@Composable
fun CourierOrdersScreen(
    courierViewModel: CourierViewModel,
    onNavigateToOrderDetail: (Delivery) -> Unit,
    onNavigateBack: () -> Unit
) {
    val deliveriesState by courierViewModel.deliveriesState.collectAsStateWithLifecycle()
    val courierIdState by courierViewModel.courierId.collectAsStateWithLifecycle()
    
    LaunchedEffect(courierIdState) {
        // Завантажуємо доставки коли отримаємо courierId
        courierIdState?.let { courierId ->
            courierViewModel.loadDeliveries(courierId)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    text = "Мої замовлення",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = OrangeAccent
                    )
                }
            }
            
            when (val state = deliveriesState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = OrangeAccent)
                    }
                }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        // Empty state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = GrayText
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Немає замовлень",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "У вас поки немає замовлень",
                                    fontSize = 14.sp,
                                    color = GrayText
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(state.data, key = { it.id }) { delivery ->
                                DeliveryCard(
                                    delivery = delivery,
                                    onClick = {
                                        onNavigateToOrderDetail(delivery)
                                    }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    courierViewModel.courierId.value?.let { courierId ->
                                        courierViewModel.loadDeliveries(courierId)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                            ) {
                                Text("Повторити")
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeliveryCard(
    delivery: Delivery,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Замовлення #${delivery.orderId}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = OrangeAccent.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = delivery.deliveryStatus.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangeAccent
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = GrayText,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = delivery.deliveryAddress,
                    fontSize = 14.sp,
                    color = GrayText,
                    modifier = Modifier.weight(1f)
                )
            }
            
            if (delivery.customerPhoneNumber != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = GrayText,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = delivery.customerPhoneNumber,
                        fontSize = 14.sp,
                        color = GrayText
                    )
                }
            }
        }
    }
}

