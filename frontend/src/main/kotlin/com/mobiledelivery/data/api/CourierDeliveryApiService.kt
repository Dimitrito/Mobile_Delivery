package com.mobiledelivery.data.api

import com.mobiledelivery.data.api.models.ApiResponse
import com.mobiledelivery.data.api.models.CourierResponse
import com.mobiledelivery.data.api.models.DeliveryResponse
import com.mobiledelivery.data.api.models.UpdateDeliveryRequest
import io.ktor.client.*

/**
 * API сервіс для роботи з доставками кур'єрів
 */
class CourierDeliveryApiService(
    client: HttpClient,
    baseUrl: String,
    tokenProvider: (() -> String?)? = null
) : DeliveryApiService(client, baseUrl, tokenProvider) {
    
    /**
     * Отримує доставки кур'єра за статусом
     * @param courierId ID кур'єра
     * @param deliveryStatus Статус доставки (наприклад, "in_delivery", "pending")
     */
    suspend fun getDeliveriesByCourierAndStatus(
        courierId: Int,
        deliveryStatus: String
    ): ApiResponse<List<DeliveryResponse>> {
        return get("delivery/$courierId/$deliveryStatus/")
    }
    
    /**
     * Оновлює доставку
     * @param deliveryId ID доставки
     * @param request Запит на оновлення
     */
    suspend fun updateDelivery(
        deliveryId: Int,
        request: UpdateDeliveryRequest
    ): ApiResponse<DeliveryResponse> {
        return put("delivery/$deliveryId", request)
    }
    
    /**
     * Отримує кур'єра за user_id
     * Спочатку отримуємо всіх кур'єрів і знаходимо потрібного
     */
    suspend fun getCourierByUserId(userId: Int): ApiResponse<CourierResponse?> {
        return when (val response = get<List<CourierResponse>>("courier")) {
            is ApiResponse.Success -> {
                val courier = response.data.find { it.user == userId }
                ApiResponse.Success(courier)
            }
            is ApiResponse.Error -> {
                ApiResponse.Error(response.message)
            }
            else -> {
                ApiResponse.Error("Помилка отримання кур'єра")
            }
        }
    }
}


