package com.mobiledelivery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiledelivery.domain.models.User
import com.mobiledelivery.domain.usecases.*
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
    private val isAuthenticatedUseCase: IsAuthenticatedUseCase
) : ViewModel() {
    
    // Стани для логіну
    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val loginState: StateFlow<UiState<User>> = _loginState.asStateFlow()
    
    // Стани для реєстрації
    private val _registerState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val registerState: StateFlow<UiState<Unit>> = _registerState.asStateFlow()
    
    // Стани для поточного користувача
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
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
                    _loginState.value = UiState.Success(user)
                    _currentUser.value = user
                    _uiEvent.value = UiEvent.Navigate("home")
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
                    _currentUser.value = user
                }
                .onFailure {
                    // Якщо не вдалося завантажити користувача, виходимо
                    logout()
                }
        }
    }
    
    /**
     * Очищає подію UI після обробки
     */
    fun clearEvent() {
        _uiEvent.value = null
    }
}

