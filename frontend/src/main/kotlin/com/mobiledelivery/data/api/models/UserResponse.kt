package com.mobiledelivery.data.api.models

import kotlinx.serialization.Serializable

/**
 * Модель відповіді API для користувача
 */
@Serializable
data class UserResponse(
    val id: Int,
    val email: String,
    val phone_number: String? = null,
    val is_staff: Boolean = false,
    val first_name: String? = null,
    val last_name: String? = null
)

