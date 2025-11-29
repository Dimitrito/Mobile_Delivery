package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.AuthRepository

/**
 * Use case для відновлення паролю
 */
class ForgotPasswordUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Надсилає новий пароль на email користувача
     * @param email Email користувача
     * @return Result з повідомленням або помилкою
     */
    suspend operator fun invoke(email: String): Result<String> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email не може бути порожнім"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(IllegalArgumentException("Невірний формат email"))
        }
        return authRepository.forgotPassword(email)
    }
}

