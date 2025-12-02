package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.OrderRepository
import com.mobiledelivery.domain.models.Order

/**
 * Use case для отримання історії замовлень користувача
 */
class GetOrdersUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(userId: Int): Result<List<Order>> {
        return orderRepository.getOrders(userId)
    }
}




