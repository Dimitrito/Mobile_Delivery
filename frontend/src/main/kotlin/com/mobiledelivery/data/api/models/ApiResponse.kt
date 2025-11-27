package com.mobiledelivery.data.api.models

import kotlinx.serialization.Serializable

/**
 * Базовий клас для API відповідей
 */
@Serializable
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}

