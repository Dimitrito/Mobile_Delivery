package com.mobiledelivery.data.api

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8000/api/" // 10.0.2.2 для Android Emulator
    
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }
    
    fun getBaseUrl(): String = BASE_URL
}

