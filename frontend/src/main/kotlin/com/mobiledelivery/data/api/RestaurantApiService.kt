package com.mobiledelivery.data.api

import com.mobiledelivery.data.api.models.ApiResponse
import com.mobiledelivery.data.api.models.CategoryResponse
import com.mobiledelivery.data.api.models.DishResponse
import io.ktor.client.*

/**
 * API сервіс для роботи з ресторанами та категоріями
 */
class RestaurantApiService(
    client: HttpClient,
    baseUrl: String,
    tokenProvider: (() -> String?)? = null
) : DeliveryApiService(client, baseUrl, tokenProvider) {
    
    /**
     * Отримує список категорій
     */
    suspend fun getCategories(): ApiResponse<List<CategoryResponse>> {
        return get("category")
    }
    
    /**
     * Отримує страви за категорією
     * @param categoryId ID категорії
     */
    suspend fun getDishesByCategory(categoryId: Int): ApiResponse<List<DishResponse>> {
        return get("categorydish/$categoryId")
    }
}

