package com.mobiledelivery.di

import com.mobiledelivery.data.api.ApiClient

/**
 * DI модуль для мережевих залежностей
 */
object NetworkModule {
    val apiClient = ApiClient.client
    val baseUrl = ApiClient.getBaseUrl()
}

