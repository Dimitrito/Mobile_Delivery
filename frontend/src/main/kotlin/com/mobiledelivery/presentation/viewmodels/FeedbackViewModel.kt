package com.mobiledelivery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiledelivery.data.api.models.CreateFeedbackRequest
import com.mobiledelivery.data.api.models.FeedbackResponse
import com.mobiledelivery.domain.usecases.CreateFeedbackUseCase
import com.mobiledelivery.domain.usecases.GetFeedbacksUseCase
import com.mobiledelivery.presentation.states.UiState
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedbackViewModel(
    private val getFeedbacksUseCase: GetFeedbacksUseCase,
    private val createFeedbackUseCase: CreateFeedbackUseCase
) : ViewModel() {

    private val _feedbacksState = MutableStateFlow<UiState<List<FeedbackResponse>>>(UiState.Loading)
    val feedbacksState: StateFlow<UiState<List<FeedbackResponse>>> = _feedbacksState

    private val _createState = MutableStateFlow<UiState<FeedbackResponse>>(UiState.Idle)
    val createState: StateFlow<UiState<FeedbackResponse>> = _createState

    fun loadFeedbacks() {
        viewModelScope.launch {
            _feedbacksState.value = UiState.Loading
            val result = getFeedbacksUseCase()
            _feedbacksState.value = when (result) {
                is com.mobiledelivery.data.api.models.ApiResponse.Success -> UiState.Success(result.data)
                is com.mobiledelivery.data.api.models.ApiResponse.Error -> UiState.Error(result.message)
                else -> UiState.Error("Unknown error")
            }
        }
    }

    fun submitFeedback(customerId: Int, text: String, rating: Double) {
        viewModelScope.launch {
            _createState.value = UiState.Loading
            val now = DateTimeFormatter.ISO_INSTANT.format(Instant.now().atZone(ZoneOffset.UTC))
            val request = CreateFeedbackRequest(
                user = customerId,
                review_text = text,
                rating = rating,
                review_date = now
            )
            val result = createFeedbackUseCase(request)
            _createState.value = when (result) {
                is com.mobiledelivery.data.api.models.ApiResponse.Success -> {
                    loadFeedbacks()
                    UiState.Success(result.data)
                }
                is com.mobiledelivery.data.api.models.ApiResponse.Error -> UiState.Error(result.message)
                else -> UiState.Error("Unknown error")
            }
        }
    }
}

