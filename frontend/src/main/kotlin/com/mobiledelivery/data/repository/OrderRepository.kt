package com.mobiledelivery.data.repository

import com.mobiledelivery.data.api.OrderApiService
import com.mobiledelivery.data.api.models.ApiResponse
import com.mobiledelivery.data.api.models.CreateDishToOrderRequest
import com.mobiledelivery.data.api.models.CreateOrderRequest
import com.mobiledelivery.domain.models.Cart
import com.mobiledelivery.domain.models.Order
import com.mobiledelivery.domain.models.OrderItem
import com.mobiledelivery.domain.models.OrderStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Repository для роботи з замовленнями
 */
class OrderRepository(
    private val orderApiService: OrderApiService
) {
    
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    /**
     * Створює замовлення з кошика
     * @param userId ID користувача (User.id)
     * @param cart Кошик з товарами
     * @param deliveryAddress Адреса доставки
     * @return Result з Order або помилкою
     */
    suspend fun createOrderFromCart(
        userId: Int,
        cart: Cart,
        deliveryAddress: String = ""
    ): Result<Order> {
        // 1. Отримуємо Customer ID за User ID
        val customerResponse = orderApiService.getCustomer(userId)
        val customerId = when (customerResponse) {
            is ApiResponse.Success -> customerResponse.data.id
            is ApiResponse.Error -> return Result.failure(Exception(customerResponse.message))
            is ApiResponse.Loading -> return Result.failure(Exception("Завантаження..."))
        }
        
        // 2. Створюємо замовлення
        val now = LocalDateTime.now()
        val comment = if (deliveryAddress.isNotBlank()) {
            "Адреса доставки: $deliveryAddress"
        } else {
            ""
        }
        
        val createOrderRequest = CreateOrderRequest(
            user = customerId,
            start_time = now.format(dateTimeFormatter),
            end_time = now.plusHours(1).format(dateTimeFormatter),
            order_status = "ready",
            price = cart.totalPrice,
            comment = comment
        )
        
        val orderResponse = orderApiService.createOrder(createOrderRequest)
        val createdOrder = when (orderResponse) {
            is ApiResponse.Success -> orderResponse.data
            is ApiResponse.Error -> return Result.failure(Exception(orderResponse.message))
            is ApiResponse.Loading -> return Result.failure(Exception("Завантаження..."))
        }
        
        // 3. Додаємо страви до замовлення
        for (cartItem in cart.items) {
            val dishToOrderRequest = CreateDishToOrderRequest(
                order = createdOrder.id,
                dish = cartItem.menuItem.id,
                count = cartItem.quantity
            )
            
            val dishResponse = orderApiService.addDishToOrder(dishToOrderRequest)
            if (dishResponse is ApiResponse.Error) {
                // Логуємо помилку, але продовжуємо
                println("Помилка додавання страви ${cartItem.menuItem.id}: ${dishResponse.message}")
            }
        }
        
        // 4. Повертаємо результат
        val order = Order(
            id = createdOrder.id,
            userId = userId,
            items = cart.items.map { cartItem ->
                OrderItem(
                    menuItemId = cartItem.menuItem.id,
                    quantity = cartItem.quantity,
                    price = cartItem.menuItem.price
                )
            },
            totalPrice = createdOrder.price,
            status = OrderStatus.PENDING,
            createdAt = createdOrder.start_time
        )
        
        return Result.success(order)
    }
    
    /**
     * Отримує замовлення користувача
     */
    suspend fun getOrders(userId: Int): Result<List<Order>> {
        // Отримуємо Customer ID
        val customerResponse = orderApiService.getCustomer(userId)
        val customerId = when (customerResponse) {
            is ApiResponse.Success -> customerResponse.data.id
            is ApiResponse.Error -> return Result.failure(Exception(customerResponse.message))
            is ApiResponse.Loading -> return Result.failure(Exception("Завантаження..."))
        }
        
        return when (val response = orderApiService.getOrders(customerId)) {
            is ApiResponse.Success -> {
                val orders = response.data.map { orderResponse ->
                    Order(
                        id = orderResponse.id,
                        userId = userId,
                        items = emptyList(), // Можна завантажити окремо при потребі
                        totalPrice = orderResponse.price,
                        status = mapOrderStatus(orderResponse.order_status),
                        createdAt = orderResponse.start_time
                    )
                }
                Result.success(orders)
            }
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Завантаження..."))
        }
    }
    
    private fun mapOrderStatus(status: String): OrderStatus {
        return when (status.lowercase()) {
            "pending" -> OrderStatus.PENDING
            "confirmed" -> OrderStatus.CONFIRMED
            "preparing" -> OrderStatus.PREPARING
            "ready" -> OrderStatus.READY
            "delivering" -> OrderStatus.DELIVERING
            "delivered" -> OrderStatus.DELIVERED
            "cancelled" -> OrderStatus.CANCELLED
            "paid" -> OrderStatus.DELIVERED
            else -> OrderStatus.PENDING
        }
    }
}

