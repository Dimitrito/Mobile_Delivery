package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.OrderRepository
import com.mobiledelivery.domain.models.Cart
import com.mobiledelivery.domain.models.Order

/**
 * Use case для створення замовлення
 */
class PlaceOrderUseCase(
    private val orderRepository: OrderRepository
) {
    /**
     * Створює замовлення з кошика
     * @param userId ID користувача
     * @param cart Кошик з товарами
     * @param deliveryAddress Адреса доставки (опціонально)
     * @return Result з Order або помилкою
     */
    suspend operator fun invoke(
        userId: Int,
        cart: Cart,
        deliveryAddress: String = ""
    ): Result<Order> {
        // Валідація вхідних даних
        if (cart.isEmpty) {
            return Result.failure(IllegalArgumentException("Кошик порожній"))
        }
        if (cart.totalPrice <= 0) {
            return Result.failure(IllegalArgumentException("Сума замовлення повинна бути більше 0"))
        }
        if (userId <= 0) {
            return Result.failure(IllegalArgumentException("ID користувача некоректний"))
        }
        
        return orderRepository.createOrderFromCart(userId, cart, deliveryAddress)
    }
}
