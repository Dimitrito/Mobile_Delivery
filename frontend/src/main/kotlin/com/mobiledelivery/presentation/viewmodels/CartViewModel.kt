package com.mobiledelivery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiledelivery.domain.models.Cart
import com.mobiledelivery.domain.models.CartItem
import com.mobiledelivery.domain.models.MenuItem
import com.mobiledelivery.domain.models.Order
import com.mobiledelivery.domain.usecases.PlaceOrderUseCase
import com.mobiledelivery.presentation.states.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel для управління кошиком
 */
class CartViewModel(
    private val placeOrderUseCase: PlaceOrderUseCase? = null
) : ViewModel() {
    
    private val _cart = MutableStateFlow<Cart>(Cart())
    val cart: StateFlow<Cart> = _cart.asStateFlow()
    
    private val _orderState = MutableStateFlow<UiState<Order>>(UiState.Idle)
    val orderState: StateFlow<UiState<Order>> = _orderState.asStateFlow()
    
    /**
     * Додає страву до кошика
     */
    fun addItem(menuItem: MenuItem, quantity: Int = 1) {
        _cart.update { currentCart ->
            val existingItem = currentCart.items.find { it.menuItem.id == menuItem.id }
            
            val updatedItems = if (existingItem != null) {
                // Якщо страва вже є в кошику, збільшуємо кількість
                currentCart.items.map { item ->
                    if (item.menuItem.id == menuItem.id) {
                        item.copy(quantity = item.quantity + quantity)
                    } else {
                        item
                    }
                }
            } else {
                // Додаємо нову страву
                currentCart.items + CartItem(menuItem = menuItem, quantity = quantity)
            }
            
            // Перераховуємо загальну суму
            val totalPrice = updatedItems.sumOf { it.totalPrice }
            
            currentCart.copy(
                items = updatedItems,
                totalPrice = totalPrice
            )
        }
    }
    
    /**
     * Видаляє страву з кошика
     */
    fun removeItem(menuItemId: Int) {
        _cart.update { currentCart ->
            val updatedItems = currentCart.items.filter { it.menuItem.id != menuItemId }
            val totalPrice = updatedItems.sumOf { it.totalPrice }
            
            currentCart.copy(
                items = updatedItems,
                totalPrice = totalPrice
            )
        }
    }
    
    /**
     * Оновлює кількість страви в кошику
     */
    fun updateItemQuantity(menuItemId: Int, quantity: Int) {
        if (quantity <= 0) {
            removeItem(menuItemId)
            return
        }
        
        _cart.update { currentCart ->
            val updatedItems = currentCart.items.map { item ->
                if (item.menuItem.id == menuItemId) {
                    item.copy(quantity = quantity)
                } else {
                    item
                }
            }
            val totalPrice = updatedItems.sumOf { it.totalPrice }
            
            currentCart.copy(
                items = updatedItems,
                totalPrice = totalPrice
            )
        }
    }
    
    /**
     * Очищає кошик
     */
    fun clearCart() {
        _cart.value = Cart()
    }
    
    /**
     * Отримує кількість конкретної страви в кошику
     */
    fun getItemQuantity(menuItemId: Int): Int {
        return _cart.value.items.find { it.menuItem.id == menuItemId }?.quantity ?: 0
    }
    
    /**
     * Створює замовлення з кошика
     * @param userId ID користувача
     * @param deliveryAddress Адреса доставки (опціонально)
     */
    fun placeOrder(userId: Int, deliveryAddress: String = "") {
        val useCase = placeOrderUseCase
        if (useCase == null) {
            _orderState.value = UiState.Error("Функція замовлення недоступна")
            return
        }
        
        viewModelScope.launch {
            _orderState.value = UiState.Loading
            
            useCase(userId, _cart.value, deliveryAddress)
                .onSuccess { order ->
                    _orderState.value = UiState.Success(order)
                    // Очищаємо кошик після успішного замовлення
                    clearCart()
                }
                .onFailure { exception ->
                    _orderState.value = UiState.Error(exception.message ?: "Помилка створення замовлення")
                }
        }
    }
    
    /**
     * Скидає стан замовлення
     */
    fun resetOrderState() {
        _orderState.value = UiState.Idle
    }
}
