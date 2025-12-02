package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.DeliveryRepository
import com.mobiledelivery.domain.models.Delivery

/**
 * Use case для отримання доставок кур'єра
 */
class GetCourierDeliveriesUseCase(
    private val deliveryRepository: DeliveryRepository
) {
    suspend operator fun invoke(
        courierId: Int,
        deliveryStatus: String = "in_delivery"
    ): Result<List<Delivery>> {
        if (courierId <= 0) {
            return Result.failure(IllegalArgumentException("ID кур'єра некоректний"))
        }
        
        return deliveryRepository.getDeliveriesByCourierAndStatus(courierId, deliveryStatus)
    }
}


