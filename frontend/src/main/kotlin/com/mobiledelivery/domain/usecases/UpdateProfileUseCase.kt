package com.mobiledelivery.domain.usecases

import com.mobiledelivery.data.repository.AuthRepository

/**
 * Use case для оновлення профілю користувача
 */
class UpdateProfileUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        userId: Int,
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        deliveryAddress: String?,
        password: String?
    ): Result<Unit> {
        return authRepository.updateProfile(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            deliveryAddress = deliveryAddress,
            password = password
        )
    }
}




