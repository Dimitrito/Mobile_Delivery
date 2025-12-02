package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.api.models.CustomerResponse
import com.mobiledelivery.data.repository.OrderRepository

/**
 * Use case для отримання інформації про клієнта
 */
class GetCustomerUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(userId: Int): Result<CustomerResponse> {
        return orderRepository.getCustomer(userId)
    }
}




