package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.AuthRepository
import com.mobiledelivery.domain.models.User

/**
 * Use case для отримання інформації про поточного користувача
 */
class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Отримує інформацію про поточного користувача
     * @return Result з User або помилкою
     */
    suspend operator fun invoke(): Result<User> {
        return authRepository.getCurrentUser()
    }
}

