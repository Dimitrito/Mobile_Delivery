package com.mobiledelivery.domain.models

/**
 * Domain модель користувача
 */
data class User(
    val id: Int,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null
)

