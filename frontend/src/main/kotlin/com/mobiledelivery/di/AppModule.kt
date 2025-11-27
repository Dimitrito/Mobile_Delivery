package com.mobiledelivery.di

import android.content.Context

/**
 * Основний DI модуль додатку
 */
object AppModule {
    lateinit var applicationContext: Context
    
    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }
}

