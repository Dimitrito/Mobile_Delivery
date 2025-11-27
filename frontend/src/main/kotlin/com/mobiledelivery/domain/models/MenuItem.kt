package com.mobiledelivery.domain.models

/**
 * Domain модель елемента меню
 */
data class MenuItem(
    val id: Int,
    val name: String,
    val description: String? = null,
    val price: Double,
    val imageUrl: String? = null,
    val categoryId: Int? = null
)

