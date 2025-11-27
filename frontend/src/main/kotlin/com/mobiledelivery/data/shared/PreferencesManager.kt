package com.mobiledelivery.data.shared

import android.content.Context
import android.content.SharedPreferences

/**
 * Менеджер для роботи з SharedPreferences
 */
class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "mobile_delivery_prefs"
        private const val KEY_USER_TOKEN = "user_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_USER_TOKEN, token).apply()
    }
    
    fun getToken(): String? {
        return prefs.getString(KEY_USER_TOKEN, null)
    }
    
    fun clearToken() {
        prefs.edit().remove(KEY_USER_TOKEN).apply()
    }
    
    fun saveUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }
    
    fun getUserId(): Int? {
        return if (prefs.contains(KEY_USER_ID)) prefs.getInt(KEY_USER_ID, -1) else null
    }
    
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}

