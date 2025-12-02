package com.mobiledelivery.data.api

import com.mobiledelivery.data.api.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

/**
 * API сервіс для роботи з PayPal через Sandbox
 * 
 * PayPal Sandbox - це РЕАЛЬНИЙ PayPal API, але в тестовому режимі.
 * Він імітує справжню оплату, але не списує реальні гроші.
 * 
 * Sandbox URL: https://api.sandbox.paypal.com
 * Production URL: https://api.paypal.com
 */
class PayPalApiService(
    client: HttpClient,
    private val clientId: String,
    private val clientSecret: String,
    private val isSandbox: Boolean = true
) : DeliveryApiService(client, getBaseUrl(isSandbox), null) {
    
    companion object {
        private fun getBaseUrl(isSandbox: Boolean): String {
            return if (isSandbox) {
                "https://api.sandbox.paypal.com"
            } else {
                "https://api.paypal.com"
            }
        }
    }
    
    private var accessToken: String? = null
    private var tokenExpiresAt: Long = 0
    
    /**
     * Отримує access token для PayPal API
     */
    private suspend fun getAccessToken(): String {
        // Перевіряємо чи токен ще дійсний (з невеликим запасом)
        if (accessToken != null && System.currentTimeMillis() < tokenExpiresAt - 60000) {
            return accessToken!!
        }
        
        val response = client.post("$baseUrl/v1/oauth2/token") {
            headers {
                append(HttpHeaders.Authorization, "Basic ${getBasicAuth()}")
                append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
                append(HttpHeaders.Accept, ContentType.Application.Json)
            }
            setBody("grant_type=client_credentials")
        }
        
        return if (response.status.isSuccess()) {
            val tokenResponse = response.body<PayPalTokenResponse>()
            accessToken = tokenResponse.access_token
            // Токен дійсний приблизно 8-9 годин, встановлюємо 8 годин
            tokenExpiresAt = System.currentTimeMillis() + (8 * 60 * 60 * 1000)
            tokenResponse.access_token
        } else {
            val errorMessage = when (response.status.value) {
                401 -> "401 Unauthorized - перевірте правильність Client ID та Client Secret. Можливо credentials некоректні або застарілі."
                400 -> "400 Bad Request - перевірте формат запиту"
                403 -> "403 Forbidden - перевірте права доступу до PayPal API"
                else -> "${response.status}"
            }
            throw Exception("Не вдалося отримати access token від PayPal: $errorMessage")
        }
    }
    
    /**
     * Створює Basic Auth header для PayPal
     */
    private fun getBasicAuth(): String {
        val credentials = "$clientId:$clientSecret"
        return android.util.Base64.encodeToString(
            credentials.toByteArray(Charsets.UTF_8),
            android.util.Base64.NO_WRAP
        )
    }
    
    /**
     * Створює платеж в PayPal
     * @param amount Сума платежу
     * @param currency Валюта (за замовчуванням USD)
     * @param description Опис платежу
     * @return ApiResponse з інформацією про створений платеж
     */
    suspend fun createPayment(
        amount: Double,
        currency: String = "USD",
        description: String = "Mobile Delivery Order"
    ): ApiResponse<PayPalPaymentResponse> {
        return try {
            val token = getAccessToken()
            
            // Форматуємо суму з двома знаками після коми
            val formattedAmount = String.format("%.2f", amount)
            
            // Перевіряємо, що сума більше 0
            if (amount <= 0) {
                return ApiResponse.Error("Сума платежу повинна бути більше 0")
            }
            
            // Створюємо запит згідно з PayPal API v2 специфікацією
            // Для автоматичного одобрення в тестовому режимі використовуємо payment_source
            val request = PayPalPaymentRequest(
                intent = "CAPTURE",
                purchase_units = listOf(
                    PurchaseUnit(
                        amount = Amount(
                            currency_code = currency.uppercase(),
                            value = formattedAmount
                        ),
                        description = description
                    )
                ),
                // Для тестового режиму: використовуємо payment_source для автоматичного одобрення
                // В реальному додатку потрібно буде використовувати approval URL
                payment_source = PaymentSource(
                    paypal = PayPalPaymentSource(
                        experience_context = ExperienceContext(
                            payment_method_preference = "IMMEDIATE_PAYMENT_REQUIRED",
                            user_action = "PAY_NOW"
                        )
                    )
                )
            )
            
            // Створюємо окремий Json serializer для PayPal з encodeDefaults = true
            // щоб гарантувати, що всі поля, включно з intent, будуть включені
            val payPalJson = Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true  // Важливо: включаємо значення за замовчуванням
                coerceInputValues = true
            }
            
            // Серіалізуємо запит вручну для логування та відправки
            val requestBody = payPalJson.encodeToString(PayPalPaymentRequest.serializer(), request)
            android.util.Log.d("PayPal", "Request body: $requestBody")
            android.util.Log.d("PayPal", "Creating payment: amount=$formattedAmount, currency=$currency")
            
            val response = client.post("$baseUrl/v2/checkout/orders") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                    append(HttpHeaders.Accept, ContentType.Application.Json)
                }
                // Відправляємо JSON як рядок
                // Використовуємо правильний спосіб для відправки String в Ktor
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            
            if (response.status.isSuccess()) {
                val paymentResponse = response.body<PayPalPaymentResponse>()
                android.util.Log.d("PayPal", "Payment created successfully: ${paymentResponse.id}")
                ApiResponse.Success(paymentResponse)
            } else {
                // Отримуємо детальну інформацію про помилку
                val errorBody = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Не вдалося прочитати відповідь"
                }
                android.util.Log.e("PayPal", "Payment creation failed: ${response.status}, body: $errorBody")
                ApiResponse.Error("Помилка створення платежу: ${response.status}. $errorBody")
            }
        } catch (e: Exception) {
            android.util.Log.e("PayPal", "Exception creating payment", e)
            ApiResponse.Error("Помилка створення платежу: ${e.message}")
        }
    }
    
    /**
     * Підтверджує (capture) платеж в PayPal
     * @param orderId ID замовлення PayPal
     * @return ApiResponse з інформацією про підтверджений платеж
     */
    suspend fun capturePayment(orderId: String): ApiResponse<PayPalCaptureResponse> {
        return try {
            val token = getAccessToken()
            
            android.util.Log.d("PayPal", "Capturing payment for order: $orderId")
            
            // Для автоматичного одобрення в тестовому режимі використовуємо payment_source
            // Це дозволяє capture без попереднього одобрення через approval URL
            val captureRequest = PayPalCaptureRequest(
                payment_source = PaymentSource(
                    paypal = PayPalPaymentSource(
                        experience_context = ExperienceContext(
                            payment_method_preference = "IMMEDIATE_PAYMENT_REQUIRED",
                            user_action = "PAY_NOW"
                        )
                    )
                )
            )
            
            // Серіалізуємо запит
            val payPalJson = Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
                coerceInputValues = true
            }
            
            val requestBody = payPalJson.encodeToString(PayPalCaptureRequest.serializer(), captureRequest)
            android.util.Log.d("PayPal", "Capture request body: $requestBody")
            
            val response = client.post("$baseUrl/v2/checkout/orders/$orderId/capture") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                    append(HttpHeaders.Accept, ContentType.Application.Json)
                }
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            
            if (response.status.isSuccess()) {
                val captureResponse = response.body<PayPalCaptureResponse>()
                android.util.Log.d("PayPal", "Payment captured successfully: ${captureResponse.id}")
                ApiResponse.Success(captureResponse)
            } else {
                // Отримуємо детальну інформацію про помилку
                val errorBody = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Не вдалося прочитати відповідь"
                }
                android.util.Log.e("PayPal", "Payment capture failed: ${response.status}, body: $errorBody")
                ApiResponse.Error("Помилка підтвердження платежу: ${response.status}. $errorBody")
            }
        } catch (e: Exception) {
            android.util.Log.e("PayPal", "Exception capturing payment", e)
            ApiResponse.Error("Помилка підтвердження платежу: ${e.message}")
        }
    }
    
    /**
     * Тестовий метод для симуляції успішної оплати (без реального виклику PayPal)
     * Використовується для тестування без налаштування PayPal Sandbox
     */
    suspend fun createTestPayment(
        amount: Double,
        currency: String = "USD",
        description: String = "Mobile Delivery Order"
    ): ApiResponse<PayPalPaymentResponse> {
        // Симулюємо успішну відповідь від PayPal
        return ApiResponse.Success(
            PayPalPaymentResponse(
                id = "TEST-${System.currentTimeMillis()}",
                status = "CREATED",
                links = listOf(
                    Link(
                        href = "https://www.sandbox.paypal.com/checkoutnow?token=TEST",
                        rel = "approve",
                        method = "GET"
                    ),
                    Link(
                        href = "$baseUrl/v2/checkout/orders/TEST/capture",
                        rel = "capture",
                        method = "POST"
                    )
                )
            )
        )
    }
    
    /**
     * Тестовий метод для симуляції підтвердження платежу
     */
    suspend fun captureTestPayment(orderId: String): ApiResponse<PayPalCaptureResponse> {
        // Симулюємо успішне підтвердження
        return ApiResponse.Success(
            PayPalCaptureResponse(
                id = orderId,
                status = "COMPLETED",
                payer = Payer(
                    name = PayerName(
                        given_name = "Test",
                        surname = "User"
                    ),
                    email_address = "test@example.com"
                ),
                purchase_units = listOf(
                    PurchaseUnitResponse(
                        amount = Amount(
                            currency_code = "USD",
                            value = "0.00"
                        )
                    )
                )
            )
        )
    }
}

/**
 * Модель для отримання токену від PayPal
 */
@kotlinx.serialization.Serializable
data class PayPalTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)

