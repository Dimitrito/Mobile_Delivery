package com.mobiledelivery.presentation.states

/**
 * Базовий клас для подій UI
 */
sealed class UiEvent {
    data class ShowMessage(val message: String) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    object NavigateBack : UiEvent()
}

