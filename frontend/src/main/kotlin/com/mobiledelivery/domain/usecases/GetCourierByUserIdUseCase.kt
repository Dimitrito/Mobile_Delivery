package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.DeliveryRepository
import com.mobiledelivery.data.api.models.CourierResponse

/**
 * Use case для отримання кур'єра за user_id
 */
class GetCourierByUserIdUseCase(
    private val deliveryRepository: DeliveryRepository
) {
    suspend operator fun invoke(userId: Int): Result<CourierResponse?> {
        if (userId <= 0) {
            return Result.failure(IllegalArgumentException("ID користувача некоректний"))
        }
        
        return deliveryRepository.getCourierByUserId(userId)
    }
}


