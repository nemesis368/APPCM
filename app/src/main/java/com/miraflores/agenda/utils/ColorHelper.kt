package com.miraflores.agenda.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView // Asegúrate de tener esta importación

object ColorHelper {

    private const val PREFS = "APP_THEME"
    const val THEME_WINE = "wine"
    const val THEME_BLUE = "blue"
    const val THEME_GREEN = "green"

    const val TEXT_BLACK = "black"
    const val TEXT_WHITE = "white"
    const val TEXT_AUTO = "auto"

    fun saveTheme(context: Context, theme: String) {
        prefs(context).edit().putString("theme", theme).apply()
    }

    fun getTheme(context: Context) =
        prefs(context).getString("theme", THEME_WINE) ?: THEME_WINE

    fun saveTextStyle(context: Context, mode: String) {
        prefs(context).edit().putString("text", mode).apply()
    }

    fun getTextStyle(context: Context) =
        prefs(context).getString("text", TEXT_AUTO) ?: TEXT_AUTO

    fun applyTheme(context: Context, root: View) {
        // 1. Aplicar Color de Fondo General
        when (getTheme(context)) {
            THEME_BLUE -> root.setBackgroundColor(0xFF2F80ED.toInt()) // Azul
            THEME_GREEN -> root.setBackgroundColor(0xFF006D5B.toInt()) // Verde
            else -> root.setBackgroundColor(0xFF3E0018.toInt()) // Vino
        }

        // 2. Determinar qué color de texto usar
        val textMode = getTextStyle(context)
        val targetTextColor = when (textMode) {
            TEXT_BLACK -> Color.BLACK
            TEXT_WHITE -> Color.WHITE
            else -> null // Auto: No forzamos nada
        }

        // 3. Aplicar cambios recursivamente (Textos y Tarjetas)
        if (targetTextColor != null) {
            applyStyleRecursive(root, targetTextColor)
        }
    }

    // Función recursiva corregida para manejar Tarjetas y Textos
    private fun applyStyleRecursive(view: View, textColor: Int) {

        // A) Si es Texto, cambiar color
        if (view is TextView) {
            view.setTextColor(textColor)
            if (view is EditText) {
                view.setHintTextColor(if (textColor == Color.WHITE) Color.LTGRAY else Color.GRAY)
            }
        }

        // B) SOLUCIÓN: Si es una TARJETA (CardView), adaptar su fondo para contraste
        if (view is CardView) {
            if (textColor == Color.WHITE) {
                // Si la letra es blanca -> Tarjeta Oscura (Gris oscuro)
                view.setCardBackgroundColor(Color.parseColor("#2C2C2C"))
            } else {
                // Si la letra es negra -> Tarjeta Blanca
                view.setCardBackgroundColor(Color.WHITE)
            }
        }

        // C) Recursividad para hijos (Layouts, ScrollViews, etc)
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                applyStyleRecursive(child, textColor)
            }
        }
    }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
