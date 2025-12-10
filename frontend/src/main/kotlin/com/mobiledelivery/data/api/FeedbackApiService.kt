package com.mobiledelivery.data.api

import com.mobiledelivery.data.api.models.ApiResponse
import com.mobiledelivery.data.api.models.CreateFeedbackRequest
import com.mobiledelivery.data.api.models.FeedbackResponse
import io.ktor.client.*

class FeedbackApiService(
    client: HttpClient,
    baseUrl: String,
    tokenProvider: (() -> String?)? = null
) : DeliveryApiService(client, baseUrl, tokenProvider) {

    suspend fun getFeedbacks(): ApiResponse<List<FeedbackResponse>> {
        return get("feedback")
    }

    suspend fun getLastFeedback(): ApiResponse<List<FeedbackResponse>> {
        return get("feedback/last")
    }

    suspend fun createFeedback(request: CreateFeedbackRequest): ApiResponse<FeedbackResponse> {
        return post("feedback", request)
    }
}

