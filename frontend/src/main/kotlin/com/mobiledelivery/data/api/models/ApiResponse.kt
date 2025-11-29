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
    val code: String? = null,
    // Поля для валідаційних помилок Django REST Framework
    val email: List<String>? = null,
    val password: List<String>? = null,
    val password2: List<String>? = null,
    val phone_number: List<String>? = null,
    val first_name: List<String>? = null,
    val last_name: List<String>? = null,
    val non_field_errors: List<String>? = null
) {
    fun getErrorMessage(): String {
        // Спочатку перевіряємо валідаційні помилки полів
        val fieldErrors = listOfNotNull(
            email?.firstOrNull()?.let { "Email: $it" },
            password?.firstOrNull()?.let { "Пароль: $it" },
            password2?.firstOrNull()?.let { "Підтвердження паролю: $it" },
            phone_number?.firstOrNull()?.let { "Номер телефону: $it" },
            first_name?.firstOrNull()?.let { "Ім'я: $it" },
            last_name?.firstOrNull()?.let { "Прізвище: $it" },
            non_field_errors?.firstOrNull()
        )
        
        if (fieldErrors.isNotEmpty()) {
            return fieldErrors.joinToString("\n")
        }
        
        return detail ?: message ?: error ?: "Невідома помилка"
    }
}

