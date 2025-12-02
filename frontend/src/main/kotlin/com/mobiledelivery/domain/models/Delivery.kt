package com.mobiledelivery.domain.models

/**
 * Domain модель доставки
 */
data class Delivery(
    val id: Int,
    val orderId: Int,
    val courierId: Int,
    val deliveryAddress: String,
    val deliveryStatus: DeliveryStatus,
    val startTime: String,
    val endTime: String,
    val customerPhoneNumber: String? = null
)

/**
 * Статус доставки
 */
enum class DeliveryStatus {
    PENDING,
    IN_DELIVERY,
    DELIVERED,
    CANCELLED
}


