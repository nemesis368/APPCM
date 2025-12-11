package com.miraflores.agenda.utils

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.miraflores.agenda.R
// IMPORTANTE: Importar todas las actividades de tu paquete UI
import com.miraflores.agenda.ui.MainActivity
import com.miraflores.agenda.ui.AppointmentActivity
import com.miraflores.agenda.ui.AppointmentsListActivity
import com.miraflores.agenda.ui.SettingsActivity

object NavHelper {
    fun setupNavigation(activity: Activity, bottomNav: BottomNavigationView, selectedId: Int) {
        bottomNav.selectedItemId = selectedId
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == selectedId) return@setOnItemSelectedListener true

            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(activity, MainActivity::class.java)
                R.id.nav_citas -> Intent(activity, AppointmentActivity::class.java)
                R.id.nav_clientes -> Intent(activity, AppointmentsListActivity::class.java)
                R.id.nav_info, R.id.nav_settings -> Intent(activity, SettingsActivity::class.java)
                else -> null
            }
            intent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                activity.startActivity(it)
                // Opcional: activity.finish() si no quieres acumular actividades
            }
            true
        }
    }
}
