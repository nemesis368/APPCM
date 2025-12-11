package com.miraflores.agenda.utils
import android.content.Context

object SessionManager {
    private const val PREF_NAME = "AgendaSession"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_EMAIL = "email"
    private const val KEY_NAME = "name"

    fun saveSession(context: Context, email: String, name: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_EMAIL, email)
            putString(KEY_NAME, name)
            apply()
        }
    }
    fun getEmail(context: Context) = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_EMAIL, "Usuario")
    fun getName(context: Context) = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_NAME, "Invitado")
    fun isLoggedIn(context: Context) = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(KEY_IS_LOGGED_IN, false)
    fun logout(context: Context) = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
}
