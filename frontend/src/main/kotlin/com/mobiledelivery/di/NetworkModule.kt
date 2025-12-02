package com.mobiledelivery.di

import com.mobiledelivery.data.api.ApiClient
import com.mobiledelivery.data.api.AuthApiService
import com.mobiledelivery.data.api.CourierDeliveryApiService
import com.mobiledelivery.data.api.OrderApiService
import com.mobiledelivery.data.api.PayPalApiService
import com.mobiledelivery.data.api.RestaurantApiService
import com.mobiledelivery.data.shared.TokenManager

/**
 * DI модуль для мережевих залежностей
 * Надає готові API сервіси для роботи з backend
 */
object NetworkModule {
    val baseUrl = ApiClient.getBaseUrl()
    
    /**
     * Базовий клієнт без автентифікації (для login/register)
     */
    val apiClient = ApiClient.client
    
    /**
     * Створює API сервіс для автентифікації
     * @param tokenManager Менеджер токенів (опціонально)
     */
    fun createAuthApiService(tokenManager: TokenManager? = null): AuthApiService {
        val client = ApiClient.client
        val tokenProvider: (() -> String?)? = tokenManager?.let { { it.getToken() } }
        return AuthApiService(client, baseUrl, tokenProvider)
    }
    
    /**
     * Створює API сервіс для роботи з ресторанами
     * @param tokenManager Менеджер токенів (обов'язково для автентифікованих запитів)
     */
    fun createRestaurantApiService(tokenManager: TokenManager? = null): RestaurantApiService {
        val client = ApiClient.client
        val tokenProvider: (() -> String?)? = tokenManager?.let { { it.getToken() } }
        return RestaurantApiService(client, baseUrl, tokenProvider)
    }
    
    /**
     * Створює API сервіс для роботи з замовленнями
     * @param tokenManager Менеджер токенів (обов'язково для автентифікованих запитів)
     */
    fun createOrderApiService(tokenManager: TokenManager): OrderApiService {
        val client = ApiClient.client
        val tokenProvider: (() -> String?) = { tokenManager.getToken() }
        return OrderApiService(client, baseUrl, tokenProvider)
    }
    
    /**
     * Створює API сервіс для роботи з доставками кур'єрів
     * @param tokenManager Менеджер токенів (обов'язково для автентифікованих запитів)
     */
    fun createCourierDeliveryApiService(tokenManager: TokenManager): CourierDeliveryApiService {
        val client = ApiClient.client
        val tokenProvider: (() -> String?) = { tokenManager.getToken() }
        return CourierDeliveryApiService(client, baseUrl, tokenProvider)
    }
    
    /**
     * Створює клієнт з автентифікацією
     */
    fun createAuthenticatedClient(tokenManager: TokenManager): io.ktor.client.HttpClient {
        return ApiClient.createClient(tokenManager)
    }
    
    /**
     * Створює PayPal API сервіс для оплати через PayPal Sandbox
     * 
     * PayPal Sandbox - це реальний PayPal API, але в тестовому режимі.
     * Він імітує справжню оплату, але не списує реальні гроші.
     * 
     * Для отримання credentials:
     * 1. Зареєструйтеся на https://developer.paypal.com/
     * 2. Створіть новий додаток в Dashboard
     * 3. Скопіюйте Client ID та Client Secret (Sandbox)
     * 4. Встановіть їх тут або через BuildConfig
     * 
     * @param clientId PayPal Sandbox Client ID
     * @param clientSecret PayPal Sandbox Client Secret
     * @param isSandbox Використовувати Sandbox (завжди true для тестування)
     */
    fun createPayPalApiService(
        clientId: String = getPayPalClientId(),
        clientSecret: String = getPayPalClientSecret(),
        isSandbox: Boolean = true
    ): PayPalApiService {
        val client = ApiClient.client
        return PayPalApiService(client, clientId, clientSecret, isSandbox)
    }
    
    /**
     * Отримує PayPal Client ID
     * PayPal Sandbox credentials налаштовано
     */
    private fun getPayPalClientId(): String {
        return "AQIso8YTLHu3s9AQYChK2iofdaEW5icfCL-8tblf4a9X7qDBjWElCHcflD-pLMi5onl2u_kiGJS_orPH"
    }
    
    /**
     * Отримує PayPal Client Secret
     * PayPal Sandbox credentials налаштовано
     */
    private fun getPayPalClientSecret(): String {
        return "EI0OadJd3nflmYUx6xGvC-pGv-hvmUozT8EQ5eB3OvLLaaUshtphLpYzwx8rBUKkCEc6kbLmO-m1GSwB"
    }
}

