package com.mobiledelivery.data.api.models

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackResponse(
    val id: Int,
    val user: Int,
    val user_first_name: String? = null,
    val review_text: String,
    val rating: Double,
    val review_date: String
)

@Serializable
data class CreateFeedbackRequest(
    val user: Int,
    val review_text: String,
    val rating: Double,
    val review_date: String
)

