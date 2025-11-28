package com.mobiledelivery.di

import com.mobiledelivery.data.api.AuthApiService
import com.mobiledelivery.data.api.RestaurantApiService
import com.mobiledelivery.data.repository.AuthRepository
import com.mobiledelivery.data.repository.RestaurantRepository
import com.mobiledelivery.data.shared.TokenManager

/**
 * DI модуль для репозиторіїв
 * Надає методи для створення репозиторіїв з необхідними залежностями
 */
object RepositoryModule {
    
    /**
     * Створює репозиторій для автентифікації
     * @param tokenManager Менеджер токенів
     * @return AuthRepository
     */
    fun createAuthRepository(tokenManager: TokenManager): AuthRepository {
        val authApiService = NetworkModule.createAuthApiService(tokenManager)
        return AuthRepository(authApiService, tokenManager)
    }
    
    /**
     * Створює репозиторій для роботи з ресторанами
     * @param tokenManager Менеджер токенів (опціонально, для автентифікованих запитів)
     * @return RestaurantRepository
     */
    fun createRestaurantRepository(tokenManager: TokenManager? = null): RestaurantRepository {
        val restaurantApiService = NetworkModule.createRestaurantApiService(tokenManager)
        return RestaurantRepository(restaurantApiService)
    }
}

