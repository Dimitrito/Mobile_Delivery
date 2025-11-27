package com.mobiledelivery.domain.models

/**
 * Domain модель замовлення
 */
data class Order(
    val id: Int,
    val userId: Int,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val status: OrderStatus,
    val createdAt: String? = null
)

data class OrderItem(
    val menuItemId: Int,
    val quantity: Int,
    val price: Double
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    DELIVERING,
    DELIVERED,
    CANCELLED
}

