package com.mobiledelivery.data.api.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Модель відповіді API для доставки
 */
@Serializable
data class DeliveryResponse(
    val id: Int,
    @SerialName("current_order")
    val current_order: Int,
    val courier: Int,
    @SerialName("start_time")
    val start_time: String,
    @SerialName("end_time")
    val end_time: String,
    @SerialName("delivery_address")
    val delivery_address: String,
    @SerialName("delivery_status")
    val delivery_status: String,
    @SerialName("user_phone_number")
    val user_phone_number: String? = null
)

/**
 * Запит на оновлення доставки
 */
@Serializable
data class UpdateDeliveryRequest(
    @SerialName("delivery_status")
    val delivery_status: String? = null,
    @SerialName("delivery_address")
    val delivery_address: String? = null
)

/**
 * Модель відповіді API для кур'єра
 */
@Serializable
data class CourierResponse(
    val id: Int,
    val user: Int
)

