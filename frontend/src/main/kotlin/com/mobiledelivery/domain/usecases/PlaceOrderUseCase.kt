package com.mobiledelivery.domain.usecases

import com.mobiledelivery.domain.models.Order

/**
 * Use case для створення замовлення
 */
class PlaceOrderUseCase {
    suspend operator fun invoke(order: Order): Result<Order> {
        // TODO: Реалізувати логіку створення замовлення
        return Result.failure(NotImplementedError("PlaceOrderUseCase not implemented yet"))
    }
}

