package com.mobiledelivery.di

import com.mobiledelivery.data.repository.AuthRepository
import com.mobiledelivery.data.repository.RestaurantRepository
import com.mobiledelivery.data.shared.TokenManager
import com.mobiledelivery.domain.usecases.*

/**
 * DI модуль для Use Cases
 * Надає методи для створення Use Cases з необхідними залежностями
 */
object UseCaseModule {
    
    /**
     * Створює LoginUseCase
     * @param tokenManager Менеджер токенів
     * @return LoginUseCase
     */
    fun createLoginUseCase(tokenManager: TokenManager): LoginUseCase {
        val authRepository = RepositoryModule.createAuthRepository(tokenManager)
        return LoginUseCase(authRepository)
    }
    
    /**
     * Створює RegisterUseCase
     * @param tokenManager Менеджер токенів
     * @return RegisterUseCase
     */
    fun createRegisterUseCase(tokenManager: TokenManager): RegisterUseCase {
        val authRepository = RepositoryModule.createAuthRepository(tokenManager)
        return RegisterUseCase(authRepository)
    }
    
    /**
     * Створює LogoutUseCase
     * @param tokenManager Менеджер токенів
     * @return LogoutUseCase
     */
    fun createLogoutUseCase(tokenManager: TokenManager): LogoutUseCase {
        val authRepository = RepositoryModule.createAuthRepository(tokenManager)
        return LogoutUseCase(authRepository)
    }
    
    /**
     * Створює GetCurrentUserUseCase
     * @param tokenManager Менеджер токенів
     * @return GetCurrentUserUseCase
     */
    fun createGetCurrentUserUseCase(tokenManager: TokenManager): GetCurrentUserUseCase {
        val authRepository = RepositoryModule.createAuthRepository(tokenManager)
        return GetCurrentUserUseCase(authRepository)
    }
    
    /**
     * Створює IsAuthenticatedUseCase
     * @param tokenManager Менеджер токенів
     * @return IsAuthenticatedUseCase
     */
    fun createIsAuthenticatedUseCase(tokenManager: TokenManager): IsAuthenticatedUseCase {
        val authRepository = RepositoryModule.createAuthRepository(tokenManager)
        return IsAuthenticatedUseCase(authRepository)
    }
    
    /**
     * Створює GetCategoriesUseCase
     * @param tokenManager Менеджер токенів (опціонально)
     * @return GetCategoriesUseCase
     */
    fun createGetCategoriesUseCase(tokenManager: TokenManager? = null): GetCategoriesUseCase {
        val restaurantRepository = RepositoryModule.createRestaurantRepository(tokenManager)
        return GetCategoriesUseCase(restaurantRepository)
    }
    
    /**
     * Створює GetDishesByCategoryUseCase
     * @param tokenManager Менеджер токенів (опціонально)
     * @return GetDishesByCategoryUseCase
     */
    fun createGetDishesByCategoryUseCase(tokenManager: TokenManager? = null): GetDishesByCategoryUseCase {
        val restaurantRepository = RepositoryModule.createRestaurantRepository(tokenManager)
        return GetDishesByCategoryUseCase(restaurantRepository)
    }
    
    /**
     * Створює GetRestaurantsUseCase
     * @param tokenManager Менеджер токенів (опціонально)
     * @return GetRestaurantsUseCase
     */
    fun createGetRestaurantsUseCase(tokenManager: TokenManager? = null): GetRestaurantsUseCase {
        val restaurantRepository = RepositoryModule.createRestaurantRepository(tokenManager)
        return GetRestaurantsUseCase(restaurantRepository)
    }
    
    /**
     * Створює PlaceOrderUseCase
     * @param tokenManager Менеджер токенів
     * @return PlaceOrderUseCase
     */
    fun createPlaceOrderUseCase(tokenManager: TokenManager): PlaceOrderUseCase {
        val orderRepository = RepositoryModule.createOrderRepository(tokenManager)
        return PlaceOrderUseCase(orderRepository)
    }
}

