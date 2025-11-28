package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.RestaurantRepository
import com.mobiledelivery.domain.models.MenuItem

/**
 * Use case для отримання страв за категорією
 */
class GetDishesByCategoryUseCase(
    private val restaurantRepository: RestaurantRepository
) {
    /**
     * Отримує страви за категорією
     * @param categoryId ID категорії
     * @return Result зі списком страв або помилкою
     */
    suspend operator fun invoke(categoryId: Int): Result<List<MenuItem>> {
        // Валідація вхідних даних
        if (categoryId <= 0) {
            return Result.failure(IllegalArgumentException("ID категорії повинен бути більше 0"))
        }
        
        return restaurantRepository.getDishesByCategory(categoryId)
    }
}

