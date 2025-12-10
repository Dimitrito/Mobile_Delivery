package com.mobiledelivery.data.repository

import android.util.Log
import com.mobiledelivery.data.api.FeedbackApiService
import com.mobiledelivery.data.api.models.ApiResponse
import com.mobiledelivery.data.api.models.CreateFeedbackRequest
import com.mobiledelivery.data.api.models.FeedbackResponse

class FeedbackRepository(
    private val feedbackApiService: FeedbackApiService
) {

    suspend fun getFeedbacks(): ApiResponse<List<FeedbackResponse>> {
        val result = feedbackApiService.getFeedbacks()
        if (result is ApiResponse.Error) {
            Log.d("FeedbackRepository", "getFeedbacks error: ${result.message}")
        }
        return result
    }

    suspend fun createFeedback(request: CreateFeedbackRequest): ApiResponse<FeedbackResponse> {
        val result = feedbackApiService.createFeedback(request)
        if (result is ApiResponse.Error) {
            Log.d("FeedbackRepository", "createFeedback error: ${result.message}")
        }
        return result
    }
}

