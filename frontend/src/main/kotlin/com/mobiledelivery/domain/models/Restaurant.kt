package com.mobiledelivery.domain.models

/**
 * Domain модель ресторану
 */
data class Restaurant(
    val id: Int,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val rating: Double? = null
)

