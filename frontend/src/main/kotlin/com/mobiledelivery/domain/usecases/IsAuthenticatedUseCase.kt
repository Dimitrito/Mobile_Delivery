package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.AuthRepository

/**
 * Use case для перевірки автентифікації користувача
 */
class IsAuthenticatedUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Перевіряє чи користувач автентифікований
     * @return true якщо користувач автентифікований, false інакше
     */
    operator fun invoke(): Boolean {
        return authRepository.isAuthenticated()
    }
}

