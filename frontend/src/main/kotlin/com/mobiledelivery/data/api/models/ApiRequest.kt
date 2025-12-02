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

/**
 * Модель запиту для відновлення паролю
 */
@Serializable
data class ForgotPasswordRequest(
    val email: String
)

/**
 * Модель запиту для оновлення профілю
 */
@Serializable
data class UpdateProfileRequest(
    val phone_number: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val delivery_address: String? = null,
    val password: String? = null
)

/**
 * Модель відповіді для відновлення паролю
 */
@Serializable
data class ForgotPasswordResponse(
    val message: String? = null,
    val error: String? = null
)

/**
 * Проста відповідь з повідомленням
 */
@Serializable
data class SimpleMessageResponse(
    val message: String? = null,
    val error: String? = null
)

