package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.AuthRepository

/**
 * Use case для реєстрації нового користувача
 */
class RegisterUseCase(
    private val authRepository: AuthRepository
) {
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
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String,
        password2: String
    ): Result<Unit> {
        // Валідація вхідних даних
        if (firstName.isBlank()) {
            return Result.failure(IllegalArgumentException("Ім'я не може бути порожнім"))
        }
        if (lastName.isBlank()) {
            return Result.failure(IllegalArgumentException("Прізвище не може бути порожнім"))
        }
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email не може бути порожнім"))
        }
        if (phoneNumber.isBlank()) {
            return Result.failure(IllegalArgumentException("Номер телефону не може бути порожнім"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Пароль не може бути порожнім"))
        }
        if (password != password2) {
            return Result.failure(IllegalArgumentException("Паролі не співпадають"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Пароль повинен містити мінімум 6 символів"))
        }
        
        // Виконуємо реєстрацію через репозиторій
        return authRepository.register(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber,
            password = password,
            password2 = password2
        )
    }
}

