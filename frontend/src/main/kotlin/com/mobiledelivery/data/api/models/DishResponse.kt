package com.mobiledelivery.data.api.models

import kotlinx.serialization.Serializable

/**
 * Модель відповіді API для страви
 */
@Serializable
data class DishResponse(
    val id: Int,
    val product_name: String,
    val description: String,
    val price: Double,
    val discount: Double,
    val category: CategoryResponse,
    val mass: Double,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrate: Double,
    val image_url: String? = null,
    val available: Boolean
)

