package com.mobiledelivery.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Базовий API сервіс для роботи з backend
 */
abstract class DeliveryApiService(
    val client: HttpClient,
    val baseUrl: String
) {
    suspend inline fun <reified T> get(endpoint: String): T {
        return client.get("$baseUrl$endpoint").body()
    }
    
    suspend inline fun <reified T> post(endpoint: String, body: Any): T {
        return client.post("$baseUrl$endpoint") {
            setBody(body)
        }.body()
    }
    
    suspend inline fun <reified T> put(endpoint: String, body: Any): T {
        return client.put("$baseUrl$endpoint") {
            setBody(body)
        }.body()
    }
    
    suspend fun delete(endpoint: String) {
        client.delete("$baseUrl$endpoint")
    }
}

