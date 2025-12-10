package com.mobiledelivery.presentation.screens.cart

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobiledelivery.domain.models.CartItem
import com.mobiledelivery.presentation.states.UiState
import com.mobiledelivery.presentation.viewmodels.AuthViewModel
import com.mobiledelivery.presentation.viewmodels.CartViewModel

// Кольори
private val OrangeAccent = Color(0xFFFF6B35)
private val GreenAccent = Color(0xFF4CAF50)
private val BackgroundColor = Color(0xFFFAF8F5)
private val CardBackground = Color.White
private val DarkText = Color(0xFF1A1A1A)
private val GrayText = Color(0xFF9E9E9E)
private val LightBorder = Color(0xFFE8E6E3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    val cart by cartViewModel.cart.collectAsStateWithLifecycle()
    val orderState by cartViewModel.orderState.collectAsStateWithLifecycle()
    val payPalPaymentState by cartViewModel.payPalPaymentState.collectAsStateWithLifecycle()
    val payPalCaptureState by cartViewModel.payPalCaptureState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val customerState by authViewModel.customerState.collectAsStateWithLifecycle()
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showPayPalDialog by remember { mutableStateOf(false) }
    var deliveryAddress by remember { mutableStateOf("") }
    
    // Завантажуємо адресу доставки з профілю користувача
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            authViewModel.refreshProfileData()
        }
    }
    
    // Оновлюємо адресу доставки коли дані клієнта завантажені
    LaunchedEffect(customerState) {
        when (val state = customerState) {
            is UiState.Success -> {
                deliveryAddress = state.data.delivery_address
            }
            else -> {}
        }
    }
    
    // Обробка стану замовлення
    LaunchedEffect(orderState) {
        when (orderState) {
            is UiState.Success -> {
                showSuccessDialog = true
            }
            else -> {}
        }
    }
    
    // Обробка PayPal платежу
    LaunchedEffect(payPalPaymentState) {
        when (val state = payPalPaymentState) {
            is UiState.Success -> {
                // Після створення платежу автоматично підтверджуємо його (тестовий режим)
                cartViewModel.capturePayPalPayment(state.data.id)
            }
            else -> {}
        }
    }
    
    // Обробка підтвердження PayPal платежу
    LaunchedEffect(payPalCaptureState) {
        when (val state = payPalCaptureState) {
            is UiState.Success -> {
                // Після успішної оплати через PayPal створюємо замовлення
                currentUser?.let { user ->
                    cartViewModel.placeOrder(user.id, deliveryAddress)
                }
                showPayPalDialog = false
                cartViewModel.resetPayPalPaymentState()
                cartViewModel.resetPayPalCaptureState()
            }
            is UiState.Error -> {
                showPayPalDialog = false
            }
            else -> {}
        }
    }
    
    // Діалог PayPal оплати
    if (showPayPalDialog) {
        AlertDialog(
            onDismissRequest = {
                showPayPalDialog = false
                cartViewModel.resetPayPalPaymentState()
            },
            title = {
                Text(
                    text = "Оплата PayPal",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    when (val state = payPalPaymentState) {
                        is UiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(16.dp)
                            )
                            Text(
                                text = "Створення платежу...",
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                        is UiState.Success -> {
                            Text("Платіж успішно створено!")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("ID замовлення: ${state.data.id}")
                            Spacer(modifier = Modifier.height(8.dp))
                            if (payPalCaptureState is UiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(8.dp)
                                )
                                Text("Обробка платежу...")
                            }
                        }
                        is UiState.Error -> {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> {}
                    }
                    
                    when (val state = payPalCaptureState) {
                        is UiState.Success -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Платіж завершено!",
                                color = GreenAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        is UiState.Error -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> {}
                    }
                }
            },
            confirmButton = {
                when {
                    payPalCaptureState is UiState.Success -> {
                        TextButton(onClick = {
                            showPayPalDialog = false
                            cartViewModel.resetPayPalPaymentState()
                            cartViewModel.resetPayPalCaptureState()
                        }) {
                            Text("OK")
                        }
                    }
                    payPalPaymentState is UiState.Error || payPalCaptureState is UiState.Error -> {
                        TextButton(onClick = {
                            showPayPalDialog = false
                            cartViewModel.resetPayPalPaymentState()
                            cartViewModel.resetPayPalCaptureState()
                        }) {
                            Text("Close")
                        }
                    }
                    else -> {
                        TextButton(onClick = {
                            showPayPalDialog = false
                            cartViewModel.resetPayPalPaymentState()
                        }) {
                            Text("Cancel")
                        }
                    }
                }
            }
        )
    }
    
    // Діалог успішного замовлення
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                cartViewModel.resetOrderState()
                onOrderPlaced()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = GreenAccent,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { 
                Text(
                    "Замовлення оформлено!",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text(
                    "Ваше замовлення успішно оформлено. Очікуйте доставку.",
                    textAlign = TextAlign.Center
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        cartViewModel.resetOrderState()
                        onOrderPlaced()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("OK")
                }
            }
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        if (cart.isEmpty) {
            // Empty Cart
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = GrayText
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ваш кошик порожній",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Додайте страви до кошика",
                    fontSize = 14.sp,
                    color = GrayText
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = onNavigateBack) {
                    Text(
                        text = "Повернутися до меню",
                        color = OrangeAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
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
                        text = "Оформлення замовлення",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
                
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Delivery Address
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Адреса доставки",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = deliveryAddress,
                            onValueChange = { deliveryAddress = it },
                            placeholder = { Text("Введіть адресу доставки", color = GrayText) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedBorderColor = OrangeAccent,
                                unfocusedBorderColor = LightBorder
                            ),
                            singleLine = true
                        )
                    }
                    
                    // Divider
                    item {
                        Divider(color = LightBorder)
                    }
                    
                    // Cart Items
                    items(cart.items, key = { it.menuItem.id }) { cartItem ->
                        CartItemRow(
                            cartItem = cartItem,
                            onIncreaseQuantity = { cartViewModel.addItem(cartItem.menuItem) },
                            onDecreaseQuantity = {
                                cartViewModel.updateItemQuantity(cartItem.menuItem.id, cartItem.quantity - 1)
                            }
                        )
                    }
                    
                    // Order Summary
                    item {
                        Divider(color = LightBorder)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Підсумок замовлення",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Text(
                                text = "${String.format("%.2f", cart.totalPrice)} ₴",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangeAccent
                            )
                        }
                    }
                    
                    // Error message
                    if (orderState is UiState.Error) {
                        item {
                            Text(
                                text = (orderState as UiState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                
                // Bottom Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBackground)
                        .padding(16.dp)
                ) {
                    // Payment Button (Card/PayPal)
                    Button(
                        onClick = {
                            cartViewModel.createPayPalPayment("USD")
                            showPayPalDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0070BA), // PayPal синій колір
                            disabledContainerColor = Color(0xFF0070BA).copy(alpha = 0.5f)
                        ),
                        enabled = payPalPaymentState !is UiState.Loading && currentUser != null && cart.totalPrice > 0 && deliveryAddress.isNotBlank()
                    ) {
                        if (payPalPaymentState is UiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Оплатити карткою",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Back to cart link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = onNavigateBack) {
                            Text(
                                text = "Повернутися до меню",
                                color = OrangeAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    cartItem: CartItem,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Item info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cartItem.menuItem.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${String.format("%.2f", cartItem.menuItem.price)} ₴",
                fontSize = 14.sp,
                color = GrayText
            )
        }
        
        // Quantity controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onDecreaseQuantity,
                modifier = Modifier
                    .size(32.dp)
                    .background(LightBorder, RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = DarkText,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Text(
                text = "${cartItem.quantity}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                modifier = Modifier.width(24.dp),
                textAlign = TextAlign.Center
            )
            
            IconButton(
                onClick = onIncreaseQuantity,
                modifier = Modifier
                    .size(32.dp)
                    .background(OrangeAccent, RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Item total
        Text(
            text = "${String.format("%.2f", cartItem.totalPrice)} ₴",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
    }
}
