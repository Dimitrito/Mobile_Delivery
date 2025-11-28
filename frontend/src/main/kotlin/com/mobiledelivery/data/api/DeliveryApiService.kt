package com.mobiledelivery.data.api

import com.mobiledelivery.data.api.models.ApiError
import com.mobiledelivery.data.api.models.ApiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException

/**
 * Базовий API сервіс для роботи з backend
 * Надає методи для виконання HTTP запитів з обробкою помилок
 */
abstract class DeliveryApiService(
    protected val client: HttpClient,
    protected val baseUrl: String
) {
    
    /**
     * Виконує GET запит
     */
    suspend inline fun <reified T> get(endpoint: String): ApiResponse<T> {
        return try {
            val response = client.get("$baseUrl$endpoint")
            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body<T>())
            } else {
                handleError(response)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }
    
    /**
     * Виконує POST запит
     */
    suspend inline fun <reified T> post(
        endpoint: String, 
        body: Any
    ): ApiResponse<T> {
        return try {
            val response = client.post("$baseUrl$endpoint") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body<T>())
            } else {
                handleError(response)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }
    
    /**
     * Виконує PUT запит
     */
    suspend inline fun <reified T> put(
        endpoint: String, 
        body: Any
    ): ApiResponse<T> {
        return try {
            val response = client.put("$baseUrl$endpoint") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body<T>())
            } else {
                handleError(response)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }
    
    /**
     * Виконує DELETE запит
     */
    suspend fun delete(endpoint: String): ApiResponse<Unit> {
        return try {
            val response = client.delete("$baseUrl$endpoint")
            if (response.status.isSuccess()) {
                ApiResponse.Success(Unit)
            } else {
                handleError(response)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }
    
    /**
     * Обробка помилок HTTP відповіді
     */
    private suspend inline fun <reified T> handleError(response: HttpResponse): ApiResponse<T> {
        return try {
            val errorBody = response.body<ApiError>()
            ApiResponse.Error(
                message = errorBody.getErrorMessage(),
                code = response.status.value
            )
        } catch (e: SerializationException) {
            // Якщо не вдалося розпарсити помилку, повертаємо загальне повідомлення
            ApiResponse.Error(
                message = "Помилка ${response.status.value}: ${response.status.description}",
                code = response.status.value
            )
        }
    }
    
    /**
     * Обробка винятків під час виконання запиту
     */
    private fun <T> handleException(e: Exception): ApiResponse<T> {
        return when (e) {
            is ClientRequestException -> {
                ApiResponse.Error(
                    message = "Помилка запиту: ${e.message}",
                    code = e.response.status.value
                )
            }
            is ServerResponseException -> {
                ApiResponse.Error(
                    message = "Помилка сервера: ${e.message}",
                    code = e.response.status.value
                )
            }
            is SerializationException -> {
                ApiResponse.Error(
                    message = "Помилка обробки даних: ${e.message}",
                    code = null
                )
            }
            else -> {
                ApiResponse.Error(
                    message = "Мережева помилка: ${e.message ?: "Невідома помилка"}",
                    code = null
                )
            }
        }
    }
}

