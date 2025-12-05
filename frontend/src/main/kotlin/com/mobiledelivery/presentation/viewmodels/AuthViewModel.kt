package com.mobiledelivery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiledelivery.data.api.models.CustomerResponse
import com.mobiledelivery.domain.models.Order
import com.mobiledelivery.domain.models.User
import com.mobiledelivery.domain.usecases.ForgotPasswordUseCase
import com.mobiledelivery.domain.usecases.GetCourierByUserIdUseCase
import com.mobiledelivery.domain.usecases.GetCustomerUseCase
import com.mobiledelivery.domain.usecases.GetCurrentUserUseCase
import com.mobiledelivery.domain.usecases.GetOrdersUseCase
import com.mobiledelivery.domain.usecases.IsAuthenticatedUseCase
import com.mobiledelivery.domain.usecases.LoginUseCase
import com.mobiledelivery.domain.usecases.LogoutUseCase
import com.mobiledelivery.domain.usecases.RegisterUseCase
import com.mobiledelivery.domain.usecases.UpdateProfileUseCase
import com.mobiledelivery.presentation.states.UiEvent
import com.mobiledelivery.presentation.states.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для автентифікації
 */
class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isAuthenticatedUseCase: IsAuthenticatedUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase? = null,
    private val updateProfileUseCase: UpdateProfileUseCase? = null,
    private val getCustomerUseCase: GetCustomerUseCase? = null,
    private val getOrdersUseCase: GetOrdersUseCase? = null,
    private val getCourierByUserIdUseCase: GetCourierByUserIdUseCase? = null
) : ViewModel() {
    
    // Стани для логіну
    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val loginState: StateFlow<UiState<User>> = _loginState.asStateFlow()
    
    // Стани для реєстрації
    private val _registerState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val registerState: StateFlow<UiState<Unit>> = _registerState.asStateFlow()
    
    // Стани для відновлення паролю
    private val _forgotPasswordState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val forgotPasswordState: StateFlow<UiState<String>> = _forgotPasswordState.asStateFlow()
    
    // Стани для поточного користувача
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // Дані клієнта (адреса доставки)
    private val _customerState = MutableStateFlow<UiState<CustomerResponse>>(UiState.Idle)
    val customerState: StateFlow<UiState<CustomerResponse>> = _customerState.asStateFlow()
    
    // Історія замовлень
    private val _orderHistoryState = MutableStateFlow<UiState<List<Order>>>(UiState.Idle)
    val orderHistoryState: StateFlow<UiState<List<Order>>> = _orderHistoryState.asStateFlow()
    
    // Оновлення профілю
    private val _updateProfileState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val updateProfileState: StateFlow<UiState<Unit>> = _updateProfileState.asStateFlow()
    
    // Події UI
    private val _uiEvent = MutableStateFlow<UiEvent?>(null)
    val uiEvent: StateFlow<UiEvent?> = _uiEvent.asStateFlow()
    
    // Перевірка автентифікації
    val isAuthenticated: Boolean
        get() = isAuthenticatedUseCase()
    
    init {
        // Перевіряємо чи користувач вже автентифікований
        if (isAuthenticated) {
            loadCurrentUser()
        }
    }
    
    /**
     * Виконує логін користувача
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            loginUseCase(email, password)
                .onSuccess { user ->
                    // Перевіряємо чи користувач є кур'єром через api/courier
                    checkAndUpdateCourierStatus(user.id) { isCourier ->
                        val updatedUser = user.copy(isCourier = isCourier)
                        _loginState.value = UiState.Success(updatedUser)
                        _currentUser.value = updatedUser
                        // Завантажуємо додаткові дані тільки якщо не кур'єр
                        if (!isCourier) {
                            loadAdditionalUserData(updatedUser.id)
                        } else {
                            // Очищаємо стани для кур'єрів
                            _customerState.value = UiState.Idle
                            _orderHistoryState.value = UiState.Idle
                        }
                        _uiEvent.value = UiEvent.Navigate("home")
                    }
                }
                .onFailure { exception ->
                    _loginState.value = UiState.Error(exception.message ?: "Помилка входу")
                    _uiEvent.value = UiEvent.ShowMessage(exception.message ?: "Помилка входу")
                }
        }
    }
    
    /**
     * Реєструє нового користувача
     */
    fun register(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String,
        password2: String
    ) {
        viewModelScope.launch {
            _registerState.value = UiState.Loading
            registerUseCase(firstName, lastName, email, phoneNumber, password, password2)
                .onSuccess {
                    _registerState.value = UiState.Success(Unit)
                    _uiEvent.value = UiEvent.ShowMessage("Реєстрація успішна! Тепер ви можете увійти.")
                    _uiEvent.value = UiEvent.Navigate("login")
                }
                .onFailure { exception ->
                    _registerState.value = UiState.Error(exception.message ?: "Помилка реєстрації")
                    _uiEvent.value = UiEvent.ShowMessage(exception.message ?: "Помилка реєстрації")
                }
        }
    }
    
    /**
     * Виконує вихід користувача
     */
    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
                .onSuccess {
                    _currentUser.value = null
                    _customerState.value = UiState.Idle
                    _orderHistoryState.value = UiState.Idle
                    _updateProfileState.value = UiState.Idle
                    _uiEvent.value = UiEvent.Navigate("login")
                }
                .onFailure { exception ->
                    _uiEvent.value = UiEvent.ShowMessage(exception.message ?: "Помилка виходу")
                }
        }
    }
    
    /**
     * Завантажує інформацію про поточного користувача
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase()
                .onSuccess { user ->
                    // Перевіряємо чи користувач є кур'єром через api/courier
                    checkAndUpdateCourierStatus(user.id) { isCourier ->
                        val updatedUser = user.copy(isCourier = isCourier)
                        _currentUser.value = updatedUser
                        // Завантажуємо додаткові дані тільки якщо не кур'єр
                        if (!isCourier) {
                            loadAdditionalUserData(updatedUser.id)
                        } else {
                            // Очищаємо стани для кур'єрів
                            _customerState.value = UiState.Idle
                            _orderHistoryState.value = UiState.Idle
                        }
                    }
                }
                .onFailure {
                    // Якщо не вдалося завантажити користувача, виходимо
                    logout()
                }
        }
    }
    
    /**
     * Перевіряє чи користувач є кур'єром через api/courier
     * Отримує список кур'єрів і перевіряє чи є там користувач
     */
    private suspend fun checkAndUpdateCourierStatus(userId: Int, onResult: (Boolean) -> Unit) {
        val useCase = getCourierByUserIdUseCase
        if (useCase == null) {
            // Якщо use case недоступний, вважаємо що не кур'єр
            onResult(false)
            return
        }
        
        useCase(userId)
            .onSuccess { courierResponse ->
                // Якщо знайдено кур'єра - користувач є кур'єром
                val isCourier = courierResponse != null
                onResult(isCourier)
            }
            .onFailure {
                // У разі помилки вважаємо що не кур'єр
                onResult(false)
            }
    }

    private fun loadAdditionalUserData(userId: Int) {
        // Перевіряємо чи користувач є кур'єром перед завантаженням даних Customer
        val isCourier = _currentUser.value?.isCourier == true
        if (!isCourier) {
            // Завантажуємо дані Customer тільки для не-кур'єрів
            loadCustomer(userId)
            loadOrderHistory(userId)
        } else {
            // Для кур'єрів не завантажуємо дані Customer та очищаємо стани
            _customerState.value = UiState.Idle
            _orderHistoryState.value = UiState.Idle
        }
    }

    fun refreshProfileData() {
        _currentUser.value?.id?.let { userId ->
            val isCourier = _currentUser.value?.isCourier == true
            if (!isCourier) {
                loadAdditionalUserData(userId)
            }
        }
    }

    private fun loadCustomer(userId: Int) {
        val useCase = getCustomerUseCase ?: return
        viewModelScope.launch {
            _customerState.value = UiState.Loading
            useCase(userId)
                .onSuccess { customer ->
                    _customerState.value = UiState.Success(customer)
                }
                .onFailure { exception ->
                    _customerState.value = UiState.Error(exception.message ?: "Не вдалося завантажити адресу доставки")
                }
        }
    }

    private fun loadOrderHistory(userId: Int) {
        val useCase = getOrdersUseCase ?: return
        viewModelScope.launch {
            _orderHistoryState.value = UiState.Loading
            useCase(userId)
                .onSuccess { orders ->
                    _orderHistoryState.value = UiState.Success(orders)
                }
                .onFailure { exception ->
                    _orderHistoryState.value = UiState.Error(exception.message ?: "Не вдалося завантажити історію замовлень")
                }
        }
    }

    fun refreshOrderHistory() {
        _currentUser.value?.id?.let { loadOrderHistory(it) }
    }

    fun updateProfile(
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        deliveryAddress: String?,
        password: String?
    ) {
        val userId = _currentUser.value?.id
        val useCase = updateProfileUseCase
        if (userId == null || useCase == null) {
            _updateProfileState.value = UiState.Error("Функція недоступна")
            return
        }

        viewModelScope.launch {
            _updateProfileState.value = UiState.Loading
            useCase(userId, firstName, lastName, phoneNumber, deliveryAddress, password)
                .onSuccess {
                    _updateProfileState.value = UiState.Success(Unit)
                    loadCurrentUser()
                    loadCustomer(userId)
                }
                .onFailure { exception ->
                    _updateProfileState.value = UiState.Error(exception.message ?: "Не вдалося оновити профіль")
                }
        }
    }

    fun resetUpdateProfileState() {
        _updateProfileState.value = UiState.Idle
    }
    
    /**
     * Очищає подію UI після обробки
     */
    fun clearEvent() {
        _uiEvent.value = null
    }
    
    /**
     * Відновлення паролю
     */
    fun forgotPassword(email: String) {
        val useCase = forgotPasswordUseCase
        if (useCase == null) {
            _forgotPasswordState.value = UiState.Error("Функція недоступна")
            return
        }
        
        viewModelScope.launch {
            _forgotPasswordState.value = UiState.Loading
            useCase(email)
                .onSuccess { message ->
                    _forgotPasswordState.value = UiState.Success(message)
                }
                .onFailure { exception ->
                    _forgotPasswordState.value = UiState.Error(exception.message ?: "Помилка відновлення паролю")
                }
        }
    }
    
    /**
     * Скидає стан відновлення паролю
     */
    fun resetForgotPasswordState() {
        _forgotPasswordState.value = UiState.Idle
    }
}

