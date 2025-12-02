package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.DeliveryRepository
import com.mobiledelivery.domain.models.Delivery

/**
 * Use case для позначення доставки як доставленої
 */
class MarkDeliveryAsDeliveredUseCase(
    private val deliveryRepository: DeliveryRepository
) {
    suspend operator fun invoke(deliveryId: Int): Result<Delivery> {
        if (deliveryId <= 0) {
            return Result.failure(IllegalArgumentException("ID доставки некоректний"))
        }
        
        return deliveryRepository.markDeliveryAsDelivered(deliveryId)
    }
}


