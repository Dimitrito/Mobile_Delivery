package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.AuthRepository
import com.mobiledelivery.domain.models.User

/**
 * Use case для авторизації користувача
 * Виконує логін та повертає інформацію про користувача
 */
class LoginUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Виконує логін користувача
     * @param email Email користувача
     * @param password Пароль користувача
     * @return Result з User або помилкою
     */
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Валідація вхідних даних
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email не може бути порожнім"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Пароль не може бути порожнім"))
        }
        
        // Виконуємо логін через репозиторій
        return authRepository.login(email, password)
    }
}

