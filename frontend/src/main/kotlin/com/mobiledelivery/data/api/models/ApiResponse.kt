package com.mobiledelivery.data.api.models

import kotlinx.serialization.Serializable

/**
 * Базовий клас для API відповідей
 */
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}

/**
 * Модель відповіді з токенами автентифікації
 */
@Serializable
data class AuthResponse(
    val access_token: String,
    val refresh_token: String
)

/**
 * Модель помилки від API
 */
@Serializable
data class ApiError(
    val error: String? = null,
    val message: String? = null,
    val detail: String? = null,
    val code: Int? = null
) {
    fun getErrorMessage(): String {
        return error ?: message ?: detail ?: "Невідома помилка"
    }
}

