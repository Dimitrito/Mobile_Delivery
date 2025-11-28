package com.mobiledelivery.data.api.models

import kotlinx.serialization.Serializable

/**
 * Модель відповіді API для категорії
 */
@Serializable
data class CategoryResponse(
    val id: Int,
    val category_name: String,
    val url_image: String? = null
)

