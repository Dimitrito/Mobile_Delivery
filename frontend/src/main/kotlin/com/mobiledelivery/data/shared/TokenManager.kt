package com.mobiledelivery.data.shared

/**
 * Менеджер для роботи з токенами автентифікації
 */
class TokenManager(private val preferencesManager: PreferencesManager) {
    
    fun saveToken(token: String) {
        preferencesManager.saveToken(token)
    }
    
    fun getToken(): String? {
        return preferencesManager.getToken()
    }
    
    fun clearToken() {
        preferencesManager.clearToken()
    }
    
    fun isAuthenticated(): Boolean {
        return getToken() != null && preferencesManager.isLoggedIn()
    }
}

