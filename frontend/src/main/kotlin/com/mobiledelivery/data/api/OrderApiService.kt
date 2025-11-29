package com.mobiledelivery.data.api

import com.mobiledelivery.data.api.models.ApiResponse
import com.mobiledelivery.data.api.models.CreateDishToOrderRequest
import com.mobiledelivery.data.api.models.CreateOrderRequest
import com.mobiledelivery.data.api.models.CustomerResponse
import com.mobiledelivery.data.api.models.DishToOrderResponse
import com.mobiledelivery.data.api.models.OrderResponse
import io.ktor.client.*

/**
 * API сервіс для роботи з замовленнями
 */
class OrderApiService(
    client: HttpClient,
    baseUrl: String,
    tokenProvider: (() -> String?)? = null
) : DeliveryApiService(client, baseUrl, tokenProvider) {
    
    /**
     * Отримує дані Customer за user_id
     */
    suspend fun getCustomer(userId: Int): ApiResponse<CustomerResponse> {
        return get("customer/$userId")
    }
    
    /**
     * Створює нове замовлення
     */
    suspend fun createOrder(request: CreateOrderRequest): ApiResponse<OrderResponse> {
        return post("order", request)
    }
    
    /**
     * Додає страву до замовлення
     */
    suspend fun addDishToOrder(request: CreateDishToOrderRequest): ApiResponse<DishToOrderResponse> {
        return post("dish_to_order", request)
    }
    
    /**
     * Отримує замовлення користувача за статусом
     */
    suspend fun getOrdersByStatus(userId: Int, status: String): ApiResponse<List<OrderResponse>> {
        return get("order/$userId/$status")
    }
    
    /**
     * Отримує всі замовлення користувача
     */
    suspend fun getOrders(userId: Int): ApiResponse<List<OrderResponse>> {
        return get("order/$userId")
    }
}

