package com.mobiledelivery.data.api.models

import kotlinx.serialization.Serializable

/**
 * Запит на створення замовлення
 */
@Serializable
data class CreateOrderRequest(
    val user: Int,           // Customer ID
    val start_time: String,  // ISO datetime
    val end_time: String,    // ISO datetime
    val order_status: String,
    val price: Double,
    val comment: String
)

/**
 * Відповідь при створенні замовлення
 */
@Serializable
data class OrderResponse(
    val id: Int,
    val user: Int,
    val start_time: String,
    val end_time: String,
    val order_status: String,
    val price: Double,
    val comment: String
)

/**
 * Запит на створення зв'язку страви з замовленням
 */
@Serializable
data class CreateDishToOrderRequest(
    val order: Int,
    val dish: Int,
    val count: Int
)

/**
 * Відповідь DishToOrder
 */
@Serializable
data class DishToOrderResponse(
    val id: Int,
    val order: Int,
    val dish: Int,
    val count: Int
)

/**
 * Відповідь з даними Customer
 */
@Serializable
data class CustomerResponse(
    val id: Int,
    val user: Int,
    val delivery_address: String
)

