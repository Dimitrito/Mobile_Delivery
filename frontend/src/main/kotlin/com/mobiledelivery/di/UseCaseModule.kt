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
    
    /**
     * Створює ForgotPasswordUseCase
     * @param tokenManager Менеджер токенів
     * @return ForgotPasswordUseCase
     */
    fun createForgotPasswordUseCase(tokenManager: TokenManager): ForgotPasswordUseCase {
        val authRepository = RepositoryModule.createAuthRepository(tokenManager)
        return ForgotPasswordUseCase(authRepository)
    }

    /**
     * Створює UpdateProfileUseCase
     */
    fun createUpdateProfileUseCase(tokenManager: TokenManager): UpdateProfileUseCase {
        val authRepository = RepositoryModule.createAuthRepository(tokenManager)
        return UpdateProfileUseCase(authRepository)
    }

    /**
     * Створює GetCustomerUseCase
     */
    fun createGetCustomerUseCase(tokenManager: TokenManager): GetCustomerUseCase {
        val orderRepository = RepositoryModule.createOrderRepository(tokenManager)
        return GetCustomerUseCase(orderRepository)
    }

    /**
     * Створює GetOrdersUseCase
     */
    fun createGetOrdersUseCase(tokenManager: TokenManager): GetOrdersUseCase {
        val orderRepository = RepositoryModule.createOrderRepository(tokenManager)
        return GetOrdersUseCase(orderRepository)
    }
    
    /**
     * Створює CreatePayPalPaymentUseCase
     * 
     * @param useTestMode Якщо false - використовує реальний PayPal Sandbox API
     *                    Якщо true - використовує мок-методи (без викликів до API)
     *                    
     * PayPal Sandbox - це реальний API PayPal в тестовому режимі.
     * Він імітує справжню оплату, але не списує реальні гроші.
     */
    fun createPayPalPaymentUseCase(useTestMode: Boolean = false): com.mobiledelivery.domain.usecases.CreatePayPalPaymentUseCase {
        // Завжди використовуємо Sandbox для тестування
        val payPalApiService = NetworkModule.createPayPalApiService(isSandbox = true)
        return com.mobiledelivery.domain.usecases.CreatePayPalPaymentUseCase(payPalApiService, useTestMode)
    }
    
    /**
     * Створює CapturePayPalPaymentUseCase
     * 
     * @param useTestMode Якщо false - використовує реальний PayPal Sandbox API
     *                    Якщо true - використовує мок-методи (без викликів до API)
     */
    fun createCapturePayPalPaymentUseCase(useTestMode: Boolean = false): com.mobiledelivery.domain.usecases.CapturePayPalPaymentUseCase {
        // Завжди використовуємо Sandbox для тестування
        val payPalApiService = NetworkModule.createPayPalApiService(isSandbox = true)
        return com.mobiledelivery.domain.usecases.CapturePayPalPaymentUseCase(payPalApiService, useTestMode)
    }
    
    /**
     * Створює GetCourierDeliveriesUseCase
     */
    fun createGetCourierDeliveriesUseCase(tokenManager: TokenManager): GetCourierDeliveriesUseCase {
        val deliveryRepository = RepositoryModule.createDeliveryRepository(tokenManager)
        return GetCourierDeliveriesUseCase(deliveryRepository)
    }
    
    /**
     * Створює MarkDeliveryAsDeliveredUseCase
     */
    fun createMarkDeliveryAsDeliveredUseCase(tokenManager: TokenManager): MarkDeliveryAsDeliveredUseCase {
        val deliveryRepository = RepositoryModule.createDeliveryRepository(tokenManager)
        return MarkDeliveryAsDeliveredUseCase(deliveryRepository)
    }
    
    /**
     * Створює GetCourierByUserIdUseCase
     */
    fun createGetCourierByUserIdUseCase(tokenManager: TokenManager): GetCourierByUserIdUseCase {
        val deliveryRepository = RepositoryModule.createDeliveryRepository(tokenManager)
        return GetCourierByUserIdUseCase(deliveryRepository)
    }
}

