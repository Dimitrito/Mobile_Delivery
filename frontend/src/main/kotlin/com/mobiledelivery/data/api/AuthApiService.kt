package com.mobiledelivery.data.api

import com.mobiledelivery.data.api.models.ApiResponse
import com.mobiledelivery.data.api.models.AuthResponse
import com.mobiledelivery.data.api.models.LoginRequest
import com.mobiledelivery.data.api.models.RegisterRequest
import io.ktor.client.*

/**
 * API сервіс для автентифікації
 * Реалізує методи для логіну, реєстрації та виходу
 */
class AuthApiService(
    client: HttpClient,
    baseUrl: String,
    tokenProvider: (() -> String?)? = null
) : DeliveryApiService(client, baseUrl, tokenProvider) {
    
    /**
     * Виконує логін користувача
     * @param email Email користувача
     * @param password Пароль користувача
     * @return ApiResponse з токенами автентифікації
     */
    suspend fun login(email: String, password: String): ApiResponse<AuthResponse> {
        val request = LoginRequest(email = email, password = password)
        return post("login", request)
    }
    
    /**
     * Реєструє нового користувача
     * @param firstName Ім'я користувача
     * @param lastName Прізвище користувача
     * @param email Email користувача
     * @param phoneNumber Номер телефону
     * @param password Пароль
     * @param password2 Підтвердження паролю
     * @return ApiResponse з результатом реєстрації
     */
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String,
        password2: String
    ): ApiResponse<String> {
        val request = RegisterRequest(
            first_name = firstName,
            last_name = lastName,
            email = email,
            phone_number = phoneNumber,
            password = password,
            password2 = password2
        )
        return post("register", request)
    }
    
    /**
     * Виконує вихід користувача
     * @return ApiResponse з результатом виходу
     */
    suspend fun logout(): ApiResponse<Unit> {
        return post("logout", mapOf<String, String>())
    }
    
    /**
     * Оновлює токен доступу
     * @return ApiResponse з новим токеном
     */
    suspend fun refreshToken(): ApiResponse<AuthResponse> {
        return post("refresh-token", mapOf<String, String>())
    }
    
    /**
     * Отримує інформацію про поточного користувача
     * @return ApiResponse з інформацією про користувача
     */
    suspend inline fun <reified T> getCurrentUser(): ApiResponse<T> {
        return get("user")
    }
}

