package com.mobiledelivery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiledelivery.domain.models.Cart
import com.mobiledelivery.domain.models.CartItem
import com.mobiledelivery.domain.models.MenuItem
import com.mobiledelivery.domain.models.Order
import com.mobiledelivery.domain.usecases.CapturePayPalPaymentUseCase
import com.mobiledelivery.domain.usecases.CreatePayPalPaymentUseCase
import com.mobiledelivery.domain.usecases.PlaceOrderUseCase
import com.mobiledelivery.data.api.models.PayPalCaptureResponse
import com.mobiledelivery.data.api.models.PayPalPaymentResponse
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
    private val placeOrderUseCase: PlaceOrderUseCase? = null,
    private val createPayPalPaymentUseCase: CreatePayPalPaymentUseCase? = null,
    private val capturePayPalPaymentUseCase: CapturePayPalPaymentUseCase? = null
) : ViewModel() {
    
    private val _cart = MutableStateFlow<Cart>(Cart())
    val cart: StateFlow<Cart> = _cart.asStateFlow()
    
    private val _orderState = MutableStateFlow<UiState<Order>>(UiState.Idle)
    val orderState: StateFlow<UiState<Order>> = _orderState.asStateFlow()
    
    private val _payPalPaymentState = MutableStateFlow<UiState<PayPalPaymentResponse>>(UiState.Idle)
    val payPalPaymentState: StateFlow<UiState<PayPalPaymentResponse>> = _payPalPaymentState.asStateFlow()
    
    private val _payPalCaptureState = MutableStateFlow<UiState<PayPalCaptureResponse>>(UiState.Idle)
    val payPalCaptureState: StateFlow<UiState<PayPalCaptureResponse>> = _payPalCaptureState.asStateFlow()
    
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
    
    /**
     * Створює PayPal платеж
     * @param currency Валюта (за замовчуванням USD)
     */
    fun createPayPalPayment(currency: String = "USD") {
        val useCase = createPayPalPaymentUseCase
        if (useCase == null) {
            _payPalPaymentState.value = UiState.Error("PayPal оплата недоступна")
            return
        }
        
        viewModelScope.launch {
            _payPalPaymentState.value = UiState.Loading
            
            // Конвертуємо гривні в USD (приблизна ставка, для тестування)
            // В реальному додатку використовуйте актуальний курс обміну
            val amountInCart = _cart.value.totalPrice
            val amountInUSD = if (currency == "USD") {
                // Якщо сума вже в USD, використовуємо як є
                // Якщо в гривнях, конвертуємо (приблизно 1 USD = 37 UAH)
                amountInCart / 37.0
            } else {
                amountInCart
            }
            
            useCase(amountInUSD, currency, "Mobile Delivery Order")
                .onSuccess { paymentResponse ->
                    _payPalPaymentState.value = UiState.Success(paymentResponse)
                }
                .onFailure { exception ->
                    _payPalPaymentState.value = UiState.Error(exception.message ?: "Помилка створення PayPal платежу")
                }
        }
    }
    
    /**
     * Підтверджує PayPal платеж
     * @param orderId ID замовлення PayPal
     */
    fun capturePayPalPayment(orderId: String) {
        val useCase = capturePayPalPaymentUseCase
        if (useCase == null) {
            _payPalCaptureState.value = UiState.Error("PayPal оплата недоступна")
            return
        }
        
        viewModelScope.launch {
            _payPalCaptureState.value = UiState.Loading
            
            useCase(orderId)
                .onSuccess { captureResponse ->
                    _payPalCaptureState.value = UiState.Success(captureResponse)
                    // Після успішної оплати створюємо замовлення
                    // Тут можна викликати placeOrder, якщо є userId
                }
                .onFailure { exception ->
                    _payPalCaptureState.value = UiState.Error(exception.message ?: "Помилка підтвердження PayPal платежу")
                }
        }
    }
    
    /**
     * Скидає стан PayPal платежу
     */
    fun resetPayPalPaymentState() {
        _payPalPaymentState.value = UiState.Idle
    }
    
    /**
     * Скидає стан підтвердження PayPal платежу
     */
    fun resetPayPalCaptureState() {
        _payPalCaptureState.value = UiState.Idle
    }
}
