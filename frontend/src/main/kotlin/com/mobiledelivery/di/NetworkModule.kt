package com.mobiledelivery.di

import com.mobiledelivery.data.api.ApiClient
import com.mobiledelivery.data.api.AuthApiService
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
        val client = if (tokenManager != null) {
            ApiClient.createClient(tokenManager)
        } else {
            ApiClient.client
        }
        return AuthApiService(client, baseUrl)
    }
    
    /**
     * Створює API сервіс для роботи з ресторанами
     * @param tokenManager Менеджер токенів (обов'язково для автентифікованих запитів)
     */
    fun createRestaurantApiService(tokenManager: TokenManager? = null): RestaurantApiService {
        val client = if (tokenManager != null) {
            ApiClient.createClient(tokenManager)
        } else {
            ApiClient.client
        }
        return RestaurantApiService(client, baseUrl)
    }
    
    /**
     * Створює клієнт з автентифікацією
     */
    fun createAuthenticatedClient(tokenManager: TokenManager): io.ktor.client.HttpClient {
        return ApiClient.createClient(tokenManager)
    }
}

