package com.mobiledelivery.presentation.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
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
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var deliveryAddress by remember { mutableStateOf("") }
    var isDelivery by remember { mutableStateOf(true) }
    var isCashPayment by remember { mutableStateOf(true) }
    
    // Обробка стану замовлення
    LaunchedEffect(orderState) {
        when (orderState) {
            is UiState.Success -> {
                showSuccessDialog = true
            }
            else -> {}
        }
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
                    "Order placed!",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text(
                    "Your order has been successfully placed. Wait for delivery.",
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
                    text = "Your cart is empty",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add dishes to your cart",
                    fontSize = 14.sp,
                    color = GrayText
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = onNavigateBack) {
                    Text(
                        text = "Back to menu",
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
                        text = "Checkout",
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
                
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Delivery Method
                    item {
                        Text(
                            text = "Delivery method",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DeliveryMethodButton(
                                text = "Delivery",
                                isSelected = isDelivery,
                                onClick = { isDelivery = true },
                                modifier = Modifier.weight(1f)
                            )
                            DeliveryMethodButton(
                                text = "Pickup",
                                isSelected = !isDelivery,
                                onClick = { isDelivery = false },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    // Address
                    if (isDelivery) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Address",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                TextButton(onClick = { }) {
                                    Text(
                                        text = "Edit",
                                        color = OrangeAccent,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            OutlinedTextField(
                                value = deliveryAddress,
                                onValueChange = { deliveryAddress = it },
                                placeholder = { Text("Enter delivery address", color = GrayText) },
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
                    }
                    
                    // Payment Method
                    item {
                        Text(
                            text = "Payment method",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isCashPayment) "Cash" else "Card",
                                fontSize = 14.sp,
                                color = DarkText
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Card",
                                    fontSize = 12.sp,
                                    color = if (!isCashPayment) OrangeAccent else GrayText
                                )
                                Switch(
                                    checked = isCashPayment,
                                    onCheckedChange = { isCashPayment = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = GreenAccent,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = GrayText
                                    )
                                )
                                Text(
                                    text = "Cash",
                                    fontSize = 12.sp,
                                    color = if (isCashPayment) GreenAccent else GrayText
                                )
                            }
                        }
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
                                text = "Order summary",
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
                    // Place Order Button
                    Button(
                        onClick = {
                            currentUser?.let { user ->
                                cartViewModel.placeOrder(user.id, deliveryAddress)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeAccent,
                            disabledContainerColor = OrangeAccent.copy(alpha = 0.5f)
                        ),
                        enabled = orderState !is UiState.Loading && currentUser != null
                    ) {
                        if (orderState is UiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Place order",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
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
                                text = "Back to cart",
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
private fun DeliveryMethodButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) OrangeAccent.copy(alpha = 0.1f) else CardBackground)
            .border(
                width = 1.dp,
                color = if (isSelected) OrangeAccent else LightBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = if (isSelected) OrangeAccent else GrayText,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = OrangeAccent,
                    modifier = Modifier.size(20.dp)
                )
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
