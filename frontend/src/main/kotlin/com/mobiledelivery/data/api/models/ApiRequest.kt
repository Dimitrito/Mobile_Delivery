package com.mobiledelivery.data.api.models

import kotlinx.serialization.Serializable

/**
 * Базовий клас для API запитів
 */
@Serializable
sealed class ApiRequest

/**
 * Модель запиту для логіну
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Модель запиту для реєстрації
 */
@Serializable
data class RegisterRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone_number: String,
    val password: String,
    val password2: String
)

