package com.mobiledelivery.di

import com.mobiledelivery.data.repository.AuthRepository
import com.mobiledelivery.data.repository.RestaurantRepository

/**
 * DI модуль для репозиторіїв
 */
object RepositoryModule {
    val authRepository = AuthRepository()
    val restaurantRepository = RestaurantRepository()
}

