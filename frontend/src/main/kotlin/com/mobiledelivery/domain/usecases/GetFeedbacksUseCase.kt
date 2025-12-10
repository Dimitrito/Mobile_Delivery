package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.api.models.ApiResponse
import com.mobiledelivery.data.api.models.FeedbackResponse
import com.mobiledelivery.data.repository.FeedbackRepository

class GetFeedbacksUseCase(
    private val feedbackRepository: FeedbackRepository
) {
    suspend operator fun invoke(): ApiResponse<List<FeedbackResponse>> {
        return feedbackRepository.getFeedbacks()
    }
}

