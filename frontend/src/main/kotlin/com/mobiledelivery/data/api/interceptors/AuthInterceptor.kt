package com.mobiledelivery.data.api.interceptors

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Налаштовує HTTP клієнт для додавання токенів автентифікації до запитів
 */
fun HttpClientConfig<*>.configureAuth(tokenProvider: () -> String?) {
    install(DefaultRequest) {
        val token = tokenProvider()
        if (token != null) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}
