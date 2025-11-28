package com.mobiledelivery.data.repository

import com.mobiledelivery.data.api.RestaurantApiService
import com.mobiledelivery.data.api.models.ApiResponse
import com.mobiledelivery.data.api.models.CategoryResponse
import com.mobiledelivery.data.api.models.DishResponse
import com.mobiledelivery.domain.models.MenuItem

/**
 * Repository для роботи з ресторанами та категоріями
 * Абстрагує роботу з API та перетворення даних
 */
class RestaurantRepository(
    private val restaurantApiService: RestaurantApiService
) {
    
    /**
     * Отримує список категорій
     * @return Result зі списком категорій або помилкою
     */
    suspend fun getCategories(): Result<List<CategoryResponse>> {
        return when (val response = restaurantApiService.getCategories()) {
            is ApiResponse.Success -> {
                Result.success(response.data)
            }
            is ApiResponse.Error -> {
                Result.failure(Exception(response.message))
            }
            is ApiResponse.Loading -> {
                Result.failure(Exception("Запит виконується"))
            }
        }
    }
    
    /**
     * Отримує страви за категорією
     * @param categoryId ID категорії
     * @return Result зі списком страв або помилкою
     */
    suspend fun getDishesByCategory(categoryId: Int): Result<List<MenuItem>> {
        return when (val response = restaurantApiService.getDishesByCategory(categoryId)) {
            is ApiResponse.Success -> {
                val dishes = response.data.map { dishResponse ->
                    MenuItem(
                        id = dishResponse.id,
                        name = dishResponse.product_name,
                        description = dishResponse.description,
                        price = dishResponse.price,
                        discount = dishResponse.discount,
                        imageUrl = dishResponse.image_url,
                        available = dishResponse.available,
                        categoryId = dishResponse.category.id,
                        categoryName = dishResponse.category.category_name,
                        mass = dishResponse.mass,
                        calories = dishResponse.calories,
                        protein = dishResponse.protein,
                        fat = dishResponse.fat,
                        carbohydrate = dishResponse.carbohydrate
                    )
                }
                Result.success(dishes)
            }
            is ApiResponse.Error -> {
                Result.failure(Exception(response.message))
            }
            is ApiResponse.Loading -> {
                Result.failure(Exception("Запит виконується"))
            }
        }
    }
}

