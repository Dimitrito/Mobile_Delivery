package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.api.PayPalApiService
import com.mobiledelivery.data.api.models.PayPalCaptureResponse
import com.mobiledelivery.data.api.models.PayPalPaymentResponse

/**
 * Use Case для створення PayPal платежу
 * 
 * @param useTestMode Якщо false - використовує реальний PayPal Sandbox API (рекомендовано)
 *                    Якщо true - використовує мок-методи без викликів до API
 */
class CreatePayPalPaymentUseCase(
    private val payPalApiService: PayPalApiService,
    private val useTestMode: Boolean = false
) {
    suspend operator fun invoke(
        amount: Double,
        currency: String = "USD",
        description: String = "Mobile Delivery Order"
    ): Result<PayPalPaymentResponse> {
        return try {
            val response = if (useTestMode) {
                payPalApiService.createTestPayment(amount, currency, description)
            } else {
                payPalApiService.createPayment(amount, currency, description)
            }
            
            when (response) {
                is com.mobiledelivery.data.api.models.ApiResponse.Success -> {
                    Result.success(response.data)
                }
                is com.mobiledelivery.data.api.models.ApiResponse.Error -> {
                    Result.failure(Exception(response.message))
                }
                else -> {
                    Result.failure(Exception("Невідома помилка"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Use Case для підтвердження PayPal платежу
 * 
 * @param useTestMode Якщо false - використовує реальний PayPal Sandbox API (рекомендовано)
 *                    Якщо true - використовує мок-методи без викликів до API
 */
class CapturePayPalPaymentUseCase(
    private val payPalApiService: PayPalApiService,
    private val useTestMode: Boolean = false
) {
    suspend operator fun invoke(orderId: String): Result<PayPalCaptureResponse> {
        return try {
            val response = if (useTestMode) {
                payPalApiService.captureTestPayment(orderId)
            } else {
                payPalApiService.capturePayment(orderId)
            }
            
            when (response) {
                is com.mobiledelivery.data.api.models.ApiResponse.Success -> {
                    Result.success(response.data)
                }
                is com.mobiledelivery.data.api.models.ApiResponse.Error -> {
                    Result.failure(Exception(response.message))
                }
                else -> {
                    Result.failure(Exception("Невідома помилка"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


