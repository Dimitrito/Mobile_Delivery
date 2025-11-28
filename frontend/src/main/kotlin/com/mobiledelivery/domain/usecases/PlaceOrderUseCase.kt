package com.mobiledelivery.domain.usecases

import com.mobiledelivery.domain.models.Order

/**
 * Use case для створення замовлення
 * TODO: Потрібно створити OrderRepository для повної реалізації
 */
class PlaceOrderUseCase {
    /**
     * Створює замовлення
     * @param order Замовлення для створення
     * @return Result з Order або помилкою
     */
    suspend operator fun invoke(order: Order): Result<Order> {
        // Валідація вхідних даних
        if (order.items.isEmpty()) {
            return Result.failure(IllegalArgumentException("Замовлення не може бути порожнім"))
        }
        if (order.totalPrice <= 0) {
            return Result.failure(IllegalArgumentException("Сума замовлення повинна бути більше 0"))
        }
        if (order.userId <= 0) {
            return Result.failure(IllegalArgumentException("ID користувача некоректний"))
        }
        
        // TODO: Реалізувати логіку створення замовлення через OrderRepository
        // Наразі повертаємо помилку, оскільки OrderRepository ще не створено
        return Result.failure(NotImplementedError("PlaceOrderUseCase потребує OrderRepository"))
    }
}

