package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.api.models.CategoryResponse
import com.mobiledelivery.data.repository.RestaurantRepository
import com.mobiledelivery.domain.models.Restaurant

/**
 * Use case для отримання списку ресторанів
 * Наразі використовує категорії як ресторани (можна змінити пізніше)
 */
class GetRestaurantsUseCase(
    private val restaurantRepository: RestaurantRepository
) {
    /**
     * Отримує список ресторанів (наразі повертає категорії)
     * @return Result зі списком ресторанів або помилкою
     */
    suspend operator fun invoke(): Result<List<Restaurant>> {
        return restaurantRepository.getCategories().fold(
            onSuccess = { categories ->
                // Конвертуємо категорії в ресторани (тимчасово)
                val restaurants = categories.map { category ->
                    Restaurant(
                        id = category.id,
                        name = category.category_name,
                        description = null,
                        imageUrl = category.url_image,
                        rating = null
                    )
                }
                Result.success(restaurants)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}

