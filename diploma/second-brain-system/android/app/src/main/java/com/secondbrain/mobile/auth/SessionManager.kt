package com.secondbrain.mobile.auth

import android.content.Context

object SessionManager {
    private const val PREFS_NAME = "second_brain_auth"
    private const val KEY_TOKEN = "token"
    private const val KEY_USERNAME = "username"
    private const val KEY_ROLE = "role"

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val prefs by lazy {
        appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveSession(token: String, username: String, role: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USERNAME, username)
            .putString(KEY_ROLE, role)
            .apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun getRole(): String? = prefs.getString(KEY_ROLE, null)

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()
}