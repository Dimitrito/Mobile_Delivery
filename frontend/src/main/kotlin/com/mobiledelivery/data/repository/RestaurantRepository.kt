package com.mobiledelivery.data.repository

import com.mobiledelivery.domain.models.Restaurant

/**
 * Repository для роботи з ресторанами
 */
class RestaurantRepository {
    suspend fun getRestaurants(): Result<List<Restaurant>> {
        // TODO: Реалізувати отримання ресторанів
        return Result.failure(NotImplementedError("Not implemented yet"))
    }
}

