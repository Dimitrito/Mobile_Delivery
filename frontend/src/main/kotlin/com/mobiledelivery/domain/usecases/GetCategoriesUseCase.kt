package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.api.models.CategoryResponse
import com.mobiledelivery.data.repository.RestaurantRepository

/**
 * Use case для отримання списку категорій
 */
class GetCategoriesUseCase(
    private val restaurantRepository: RestaurantRepository
) {
    /**
     * Отримує список категорій
     * @return Result зі списком категорій або помилкою
     */
    suspend operator fun invoke(): Result<List<CategoryResponse>> {
        return restaurantRepository.getCategories()
    }
}

