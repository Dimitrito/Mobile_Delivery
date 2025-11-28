package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.AuthRepository

/**
 * Use case для виходу користувача
 */
class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Виконує вихід користувача
     * @return Result з Unit або помилкою
     */
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}

