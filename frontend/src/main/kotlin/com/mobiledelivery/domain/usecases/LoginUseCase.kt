package com.mobiledelivery.domain.usecases

/**
 * Use case для авторизації користувача
 */
class LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        // TODO: Реалізувати логіку авторизації
        return Result.failure(NotImplementedError("LoginUseCase not implemented yet"))
    }
}

