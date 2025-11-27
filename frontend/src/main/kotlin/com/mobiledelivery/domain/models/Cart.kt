package com.mobiledelivery.domain.models

/**
 * Domain модель кошика
 */
data class Cart(
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0
) {
    val isEmpty: Boolean
        get() = items.isEmpty()
    
    val itemCount: Int
        get() = items.sumOf { it.quantity }
}

data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int
) {
    val totalPrice: Double
        get() = menuItem.price * quantity
}

