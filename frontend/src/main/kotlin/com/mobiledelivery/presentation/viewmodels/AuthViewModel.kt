package com.mobiledelivery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiledelivery.presentation.states.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для автентифікації
 */
class AuthViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val loginState: StateFlow<UiState<Unit>> = _loginState
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            // TODO: Реалізувати логіку авторизації
        }
    }
}

