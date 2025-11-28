package com.mobiledelivery.data.api.interceptors

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*

/**
 * Перехоплювач для автоматичного додавання токенів автентифікації до запитів
 */
class AuthInterceptor(private val tokenProvider: () -> String?) {
    
    fun configure(client: HttpClientConfig<*>) {
        client.install(HttpRequestPipeline.State) {
            intercept(HttpRequestPipeline.State) {
                val token = tokenProvider()
                if (token != null) {
                    context.headers.append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }
}
