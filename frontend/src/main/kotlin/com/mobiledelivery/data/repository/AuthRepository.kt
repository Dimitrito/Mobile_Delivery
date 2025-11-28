package com.mobiledelivery.data.repository

import com.mobiledelivery.data.api.AuthApiService
import com.mobiledelivery.data.api.models.ApiResponse
import com.mobiledelivery.data.api.models.UserResponse
import com.mobiledelivery.data.shared.TokenManager
import com.mobiledelivery.domain.models.User

/**
 * Repository для автентифікації
 * Абстрагує роботу з API та локальним зберіганням даних
 */
class AuthRepository(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) {
    
    /**
     * Виконує логін користувача
     * @param email Email користувача
     * @param password Пароль користувача
     * @return Result з User або помилкою
     */
    suspend fun login(email: String, password: String): Result<User> {
        return when (val response = authApiService.login(email, password)) {
            is ApiResponse.Success -> {
                // Зберігаємо токен
                tokenManager.saveToken(response.data.access_token)
                // Отримуємо інформацію про користувача
                getCurrentUser()
            }
            is ApiResponse.Error -> {
                Result.failure(Exception(response.message))
            }
            is ApiResponse.Loading -> {
                Result.failure(Exception("Запит виконується"))
            }
        }
    }
    
    /**
     * Реєструє нового користувача
     * @param firstName Ім'я користувача
     * @param lastName Прізвище користувача
     * @param email Email користувача
     * @param phoneNumber Номер телефону
     * @param password Пароль
     * @param password2 Підтвердження паролю
     * @return Result з Unit або помилкою
     */
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String,
        password2: String
    ): Result<Unit> {
        return when (val response = authApiService.register(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber,
            password = password,
            password2 = password2
        )) {
            is ApiResponse.Success -> {
                Result.success(Unit)
            }
            is ApiResponse.Error -> {
                Result.failure(Exception(response.message))
            }
            is ApiResponse.Loading -> {
                Result.failure(Exception("Запит виконується"))
            }
        }
    }
    
    /**
     * Виконує вихід користувача
     * @return Result з Unit або помилкою
     */
    suspend fun logout(): Result<Unit> {
        return when (val response = authApiService.logout()) {
            is ApiResponse.Success -> {
                // Очищаємо токен та інші дані
                tokenManager.clearToken()
                Result.success(Unit)
            }
            is ApiResponse.Error -> {
                // Навіть якщо помилка на сервері, очищаємо локальні дані
                tokenManager.clearToken()
                Result.failure(Exception(response.message))
            }
            is ApiResponse.Loading -> {
                Result.failure(Exception("Запит виконується"))
            }
        }
    }
    
    /**
     * Отримує інформацію про поточного користувача
     * @return Result з User або помилкою
     */
    suspend fun getCurrentUser(): Result<User> {
        return when (val response = authApiService.getCurrentUser<UserResponse>()) {
            is ApiResponse.Success -> {
                val userResponse = response.data
                val user = User(
                    id = userResponse.id,
                    email = userResponse.email,
                    firstName = userResponse.first_name,
                    lastName = userResponse.last_name,
                    phoneNumber = userResponse.phone_number
                )
                Result.success(user)
            }
            is ApiResponse.Error -> {
                Result.failure(Exception(response.message))
            }
            is ApiResponse.Loading -> {
                Result.failure(Exception("Запит виконується"))
            }
        }
    }
    
    /**
     * Перевіряє чи користувач автентифікований
     * @return true якщо користувач автентифікований, false інакше
     */
    fun isAuthenticated(): Boolean {
        return tokenManager.isAuthenticated()
    }
    
    /**
     * Оновлює токен доступу
     * @return Result з Unit або помилкою
     */
    suspend fun refreshToken(): Result<Unit> {
        return when (val response = authApiService.refreshToken()) {
            is ApiResponse.Success -> {
                tokenManager.saveToken(response.data.access_token)
                Result.success(Unit)
            }
            is ApiResponse.Error -> {
                Result.failure(Exception(response.message))
            }
            is ApiResponse.Loading -> {
                Result.failure(Exception("Запит виконується"))
            }
        }
    }
}

