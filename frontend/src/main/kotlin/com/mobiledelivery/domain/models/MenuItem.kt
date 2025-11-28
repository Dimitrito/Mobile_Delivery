package com.mobiledelivery.domain.models

/**
 * Domain модель елемента меню
 */
data class MenuItem(
    val id: Int,
    val name: String,
    val description: String? = null,
    val price: Double,
    val discount: Double = 0.0,
    val imageUrl: String? = null,
    val categoryId: Int? = null,
    val categoryName: String? = null,
    val available: Boolean = true,
    val mass: Double? = null,
    val calories: Double? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val carbohydrate: Double? = null
)

