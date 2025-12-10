package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.api.models.ApiResponse
import com.mobiledelivery.data.api.models.CreateFeedbackRequest
import com.mobiledelivery.data.api.models.FeedbackResponse
import com.mobiledelivery.data.repository.FeedbackRepository

class CreateFeedbackUseCase(
    private val feedbackRepository: FeedbackRepository
) {
    suspend operator fun invoke(request: CreateFeedbackRequest): ApiResponse<FeedbackResponse> {
        return feedbackRepository.createFeedback(request)
    }
}

