package com.mobiledelivery.data.api

import com.mobiledelivery.data.api.models.ApiError
import com.mobiledelivery.data.api.models.ApiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException

/**
 * Базовий API сервіс для роботи з backend
 * Надає методи для виконання HTTP запитів з обробкою помилок
 */
abstract class DeliveryApiService(
    val client: HttpClient,
    val baseUrl: String,
    internal val tokenProvider: (() -> String?)? = null
) {
    
    /**
     * Додає стандартні заголовки до запиту
     */
    fun HttpRequestBuilder.addDefaultHeaders() {
        headers {
            append(HttpHeaders.ContentType, ContentType.Application.Json)
            append(HttpHeaders.Accept, ContentType.Application.Json)
            
            // Додавання токену автентифікації, якщо він є
            tokenProvider?.invoke()?.let { token ->
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
    
    /**
     * Додає заголовки без авторизації (для публічних endpoints)
     */
    fun HttpRequestBuilder.addPublicHeaders() {
        headers {
            append(HttpHeaders.ContentType, ContentType.Application.Json)
            append(HttpHeaders.Accept, ContentType.Application.Json)
        }
    }
    
    /**
     * Виконує GET запит
     */
    suspend inline fun <reified T> get(endpoint: String): ApiResponse<T> {
        return try {
            val response = client.get("$baseUrl$endpoint") {
                addDefaultHeaders()
            }
            
            if (response.status.isSuccess()) {
                val data = response.body<T>()
                ApiResponse.Success(data)
            } else {
                val errorBody = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Помилка: ${response.status}"
                }
                ApiResponse.Error("Помилка запиту: ${response.status}. $errorBody")
            }
        } catch (e: ClientRequestException) {
            val errorBody = try {
                e.response.body<String>()
            } catch (ex: Exception) {
                e.message ?: "Помилка запиту"
            }
            ApiResponse.Error(errorBody)
        } catch (e: SerializationException) {
            ApiResponse.Error("Помилка десеріалізації: ${e.message}")
        } catch (e: Exception) {
            ApiResponse.Error("Помилка мережі: ${e.message}")
        }
    }
    
    /**
     * Виконує GET запит без автентифікації (публічний)
     */
    suspend inline fun <reified T> getPublic(endpoint: String): ApiResponse<T> {
        return try {
            val response = client.get("$baseUrl$endpoint") {
                addPublicHeaders()
            }
            
            if (response.status.isSuccess()) {
                val data = response.body<T>()
                ApiResponse.Success(data)
            } else {
                val errorBody = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Помилка: ${response.status}"
                }
                ApiResponse.Error("Помилка запиту: ${response.status}. $errorBody")
            }
        } catch (e: ClientRequestException) {
            val errorBody = try {
                e.response.body<String>()
            } catch (ex: Exception) {
                e.message ?: "Помилка запиту"
            }
            ApiResponse.Error(errorBody)
        } catch (e: SerializationException) {
            ApiResponse.Error("Помилка десеріалізації: ${e.message}")
        } catch (e: Exception) {
            ApiResponse.Error("Помилка мережі: ${e.message}")
        }
    }
    
    /**
     * Виконує POST запит
     */
    suspend inline fun <reified T> post(endpoint: String, body: Any? = null): ApiResponse<T> {
        return try {
            val response = client.post("$baseUrl$endpoint") {
                addDefaultHeaders()
                if (body != null) {
                    setBody(body)
                }
            }
            
            if (response.status.isSuccess()) {
                val data = response.body<T>()
                ApiResponse.Success(data)
            } else {
                val errorBody = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Помилка: ${response.status}"
                }
                ApiResponse.Error("Помилка запиту: ${response.status}. $errorBody")
            }
        } catch (e: ClientRequestException) {
            val errorBody = try {
                e.response.body<String>()
            } catch (ex: Exception) {
                e.message ?: "Помилка запиту"
            }
            ApiResponse.Error(errorBody)
        } catch (e: SerializationException) {
            ApiResponse.Error("Помилка десеріалізації: ${e.message}")
        } catch (e: Exception) {
            ApiResponse.Error("Помилка мережі: ${e.message}")
        }
    }
    
    /**
     * Виконує POST запит без автентифікації (публічний)
     */
    suspend inline fun <reified T> postPublic(endpoint: String, body: Any? = null): ApiResponse<T> {
        return try {
            val response = client.post("$baseUrl$endpoint") {
                addPublicHeaders()
                if (body != null) {
                    setBody(body)
                }
            }
            
            if (response.status.isSuccess()) {
                val data = response.body<T>()
                ApiResponse.Success(data)
            } else {
                val errorBody = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Помилка: ${response.status}"
                }
                ApiResponse.Error("Помилка запиту: ${response.status}. $errorBody")
            }
        } catch (e: ClientRequestException) {
            val errorBody = try {
                e.response.body<String>()
            } catch (ex: Exception) {
                e.message ?: "Помилка запиту"
            }
            ApiResponse.Error(errorBody)
        } catch (e: SerializationException) {
            ApiResponse.Error("Помилка десеріалізації: ${e.message}")
        } catch (e: Exception) {
            ApiResponse.Error("Помилка мережі: ${e.message}")
        }
    }
    
    /**
     * Виконує PUT запит
     */
    suspend inline fun <reified T> put(endpoint: String, body: Any? = null): ApiResponse<T> {
        return try {
            val response = client.put("$baseUrl$endpoint") {
                addDefaultHeaders()
                if (body != null) {
                    setBody(body)
                }
            }
            
            if (response.status.isSuccess()) {
                val data = response.body<T>()
                ApiResponse.Success(data)
            } else {
                val errorBody = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Помилка: ${response.status}"
                }
                ApiResponse.Error("Помилка запиту: ${response.status}. $errorBody")
            }
        } catch (e: ClientRequestException) {
            val errorBody = try {
                e.response.body<String>()
            } catch (ex: Exception) {
                e.message ?: "Помилка запиту"
            }
            ApiResponse.Error(errorBody)
        } catch (e: SerializationException) {
            ApiResponse.Error("Помилка десеріалізації: ${e.message}")
        } catch (e: Exception) {
            ApiResponse.Error("Помилка мережі: ${e.message}")
        }
    }
}


