package com.mobiledelivery.data.repository

import com.mobiledelivery.data.api.CourierDeliveryApiService
import com.mobiledelivery.data.api.models.ApiResponse
import com.mobiledelivery.data.api.models.CourierResponse
import com.mobiledelivery.data.api.models.UpdateDeliveryRequest
import com.mobiledelivery.domain.models.Delivery
import com.mobiledelivery.domain.models.DeliveryStatus

/**
 * Repository для роботи з доставками
 */
class DeliveryRepository(
    private val deliveryApiService: CourierDeliveryApiService
) {
    
    /**
     * Отримує доставки кур'єра за статусом
     */
    suspend fun getDeliveriesByCourierAndStatus(
        courierId: Int,
        deliveryStatus: String
    ): Result<List<Delivery>> {
        return when (val response = deliveryApiService.getDeliveriesByCourierAndStatus(courierId, deliveryStatus)) {
            is ApiResponse.Success -> {
                val deliveries = response.data.map { deliveryResponse ->
                    Delivery(
                        id = deliveryResponse.id,
                        orderId = deliveryResponse.current_order,
                        courierId = deliveryResponse.courier,
                        deliveryAddress = deliveryResponse.delivery_address,
                        deliveryStatus = mapDeliveryStatus(deliveryResponse.delivery_status),
                        startTime = deliveryResponse.start_time,
                        endTime = deliveryResponse.end_time,
                        customerPhoneNumber = deliveryResponse.user_phone_number
                    )
                }
                Result.success(deliveries)
            }
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Завантаження..."))
        }
    }
    
    /**
     * Оновлює статус доставки на "delivered"
     */
    suspend fun markDeliveryAsDelivered(deliveryId: Int): Result<Delivery> {
        val request = UpdateDeliveryRequest(
            delivery_status = "delivered"
        )
        
        return when (val response = deliveryApiService.updateDelivery(deliveryId, request)) {
            is ApiResponse.Success -> {
                val deliveryResponse = response.data
                Result.success(
                    Delivery(
                        id = deliveryResponse.id,
                        orderId = deliveryResponse.current_order,
                        courierId = deliveryResponse.courier,
                        deliveryAddress = deliveryResponse.delivery_address,
                        deliveryStatus = mapDeliveryStatus(deliveryResponse.delivery_status),
                        startTime = deliveryResponse.start_time,
                        endTime = deliveryResponse.end_time,
                        customerPhoneNumber = deliveryResponse.user_phone_number
                    )
                )
            }
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Завантаження..."))
        }
    }
    
    /**
     * Отримує кур'єра за user_id
     */
    suspend fun getCourierByUserId(userId: Int): Result<CourierResponse?> {
        return when (val response = deliveryApiService.getCourierByUserId(userId)) {
            is ApiResponse.Success -> Result.success(response.data)
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Завантаження..."))
        }
    }
    
    private fun mapDeliveryStatus(status: String): DeliveryStatus {
        return when (status.lowercase()) {
            "pending" -> DeliveryStatus.PENDING
            "in_delivery" -> DeliveryStatus.IN_DELIVERY
            "delivered" -> DeliveryStatus.DELIVERED
            "cancelled" -> DeliveryStatus.CANCELLED
            else -> DeliveryStatus.PENDING
        }
    }
}

