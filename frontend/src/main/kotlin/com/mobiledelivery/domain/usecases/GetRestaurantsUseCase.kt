package com.mobiledelivery.domain.usecases

import com.mobiledelivery.domain.models.Restaurant

/**
 * Use case для отримання списку ресторанів
 */
class GetRestaurantsUseCase {
    suspend operator fun invoke(): Result<List<Restaurant>> {
        // TODO: Реалізувати логіку отримання ресторанів
        return Result.failure(NotImplementedError("GetRestaurantsUseCase not implemented yet"))
    }
}

