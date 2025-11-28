package com.mobiledelivery.data.api

import com.mobiledelivery.data.shared.TokenManager
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultrequest.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * API клієнт для підключення до Django backend
 */
object ApiClient {
    // BASE_URL для емулятора Android: 10.0.2.2 вказує на localhost хоста
    // Для реального пристрою використовуйте IP адресу вашого комп'ютера в локальній мережі
    private const val BASE_URL = "http://10.0.2.2:8000/api/"
    
    /**
     * Створює налаштований HTTP клієнт з автентифікацією
     */
    fun createClient(tokenManager: TokenManager? = null): HttpClient {
        return HttpClient(Android) {
            // Налаштування JSON серіалізації
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = false
                    coerceInputValues = true
                })
            }
            
            // Логування HTTP запитів
            install(Logging) {
                level = LogLevel.INFO
            }
            
            // Налаштування базового URL та заголовків
            install(DefaultRequest) {
                url(BASE_URL)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                
                // Додавання токену автентифікації, якщо він є
                tokenManager?.getToken()?.let { token ->
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            
            // Обробка помилок
            expectSuccess = false
        }
    }
    
    /**
     * Базовий клієнт без автентифікації (для login/register)
     */
    val client: HttpClient = createClient()
    
    fun getBaseUrl(): String = BASE_URL
}

