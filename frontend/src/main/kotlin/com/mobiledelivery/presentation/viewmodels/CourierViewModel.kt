package com.mobiledelivery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiledelivery.domain.models.Delivery
import com.mobiledelivery.domain.usecases.GetCourierByUserIdUseCase
import com.mobiledelivery.domain.usecases.GetCourierDeliveriesUseCase
import com.mobiledelivery.domain.usecases.MarkDeliveryAsDeliveredUseCase
import com.mobiledelivery.presentation.states.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для роботи кур'єра з доставками
 */
class CourierViewModel(
    private val getCourierDeliveriesUseCase: GetCourierDeliveriesUseCase?,
    private val markDeliveryAsDeliveredUseCase: MarkDeliveryAsDeliveredUseCase?,
    private val getCourierByUserIdUseCase: GetCourierByUserIdUseCase?
) : ViewModel() {
    
    private val _deliveriesState = MutableStateFlow<UiState<List<Delivery>>>(UiState.Idle)
    val deliveriesState: StateFlow<UiState<List<Delivery>>> = _deliveriesState.asStateFlow()
    
    private val _markDeliveryState = MutableStateFlow<UiState<Delivery>>(UiState.Idle)
    val markDeliveryState: StateFlow<UiState<Delivery>> = _markDeliveryState.asStateFlow()
    
    private val _courierId = MutableStateFlow<Int?>(null)
    val courierId: StateFlow<Int?> = _courierId.asStateFlow()
    
    private val _selectedDelivery = MutableStateFlow<Delivery?>(null)
    val selectedDelivery: StateFlow<Delivery?> = _selectedDelivery.asStateFlow()
    
    /**
     * Встановлює вибрану доставку
     */
    fun setSelectedDelivery(delivery: Delivery) {
        _selectedDelivery.value = delivery
    }
    
    /**
     * Очищає вибрану доставку
     */
    fun clearSelectedDelivery() {
        _selectedDelivery.value = null
    }
    
    /**
     * Отримує ID кур'єра за user_id
     */
    fun loadCourierId(userId: Int) {
        val useCase = getCourierByUserIdUseCase
        if (useCase == null) {
            return
        }
        
        viewModelScope.launch {
            useCase(userId)
                .onSuccess { courierResponse ->
                    _courierId.value = courierResponse?.id
                }
                .onFailure { 
                    _courierId.value = null
                }
        }
    }
    
    /**
     * Завантажує доставки кур'єра
     */
    fun loadDeliveries(courierId: Int, status: String = "in_delivery") {
        val useCase = getCourierDeliveriesUseCase
        if (useCase == null) {
            _deliveriesState.value = UiState.Error("Функція недоступна")
            return
        }
        
        viewModelScope.launch {
            _deliveriesState.value = UiState.Loading
            
            useCase(courierId, status)
                .onSuccess { deliveries ->
                    _deliveriesState.value = UiState.Success(deliveries)
                }
                .onFailure { exception ->
                    _deliveriesState.value = UiState.Error(exception.message ?: "Помилка завантаження доставок")
                }
        }
    }
    
    /**
     * Позначає доставку як доставлену
     */
    fun markDeliveryAsDelivered(deliveryId: Int) {
        val useCase = markDeliveryAsDeliveredUseCase
        if (useCase == null) {
            _markDeliveryState.value = UiState.Error("Функція недоступна")
            return
        }
        
        viewModelScope.launch {
            _markDeliveryState.value = UiState.Loading
            
            useCase(deliveryId)
                .onSuccess { delivery ->
                    _markDeliveryState.value = UiState.Success(delivery)
                    // Оновлюємо список доставок
                    _courierId.value?.let { courierId ->
                        loadDeliveries(courierId)
                    }
                }
                .onFailure { exception ->
                    _markDeliveryState.value = UiState.Error(exception.message ?: "Помилка оновлення доставки")
                }
        }
    }
    
    /**
     * Скидає стан позначення доставки
     */
    fun resetMarkDeliveryState() {
        _markDeliveryState.value = UiState.Idle
    }
}


