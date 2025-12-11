package com.miraflores.agenda.ui

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miraflores.agenda.R
import com.miraflores.agenda.adapter.AppointmentsAdapter
import com.miraflores.agenda.db.AgendaDbHelper
import com.miraflores.agenda.utils.ColorHelper

class AppointmentsListActivity : AppCompatActivity() {

    private lateinit var dbHelper: AgendaDbHelper
    private lateinit var recycler: RecyclerView
    private val CHANNEL_ID = "admin_notifications"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments_list)

        // Configuración Inicial
        createNotificationChannel()
        checkNotificationPermission()

        val root = findViewById<View>(R.id.rootLayout)
        try { ColorHelper.applyTheme(this, root) } catch (e: Exception) {}

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.title).text = "ADMINISTRACIÓN DE CITAS"

        dbHelper = AgendaDbHelper(this)
        recycler = findViewById(R.id.recyclerAppointments)
        recycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        loadList()
    }

    private fun loadList() {
        val list = dbHelper.getAllAppointments()

        recycler.adapter = AppointmentsAdapter(list,
            // 1. EDITAR (Lápiz) -> Abre la pantalla de edición
            onEditClick = { appointment ->
                val intent = Intent(this, AppointmentActivity::class.java)
                // CORRECCIÓN: Usamos "EXTRA_ID" para que coincida con AppointmentActivity
                intent.putExtra("EXTRA_ID", appointment.id)
                startActivity(intent)
            },

            // 2. ELIMINAR FÍSICAMENTE (Basura Roja) -> Borra de la BD
            onDeleteClick = { appointment ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar Cita")
                    .setMessage("¿Deseas borrar permanentemente este registro?")
                    .setPositiveButton("Borrar") { _, _ ->
                        dbHelper.deleteAppointment(appointment.id)
                        Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show()
                        loadList()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            },

            // 3. CONFIRMAR (Check Azul) -> Cambia estado a CONFIRMADO y Notifica
            onConfirmClick = { appointment ->
                dbHelper.updateStatus(appointment.id, "CONFIRMADO")
                Toast.makeText(this, "Cita confirmada", Toast.LENGTH_SHORT).show()
                sendSystemNotification(
                    "Cita Aceptada ✅",
                    "Se confirmó la cita de ${appointment.clientName}."
                )
                loadList()
            },

            // 4. CANCELAR (X Naranja) -> Cambia estado a CANCELADO y Notifica
            onCancelClick = { appointment ->
                AlertDialog.Builder(this)
                    .setTitle("Rechazar Cita")
                    .setMessage("¿Deseas cancelar/rechazar esta cita?")
                    .setPositiveButton("Sí, Cancelar") { _, _ ->
                        dbHelper.updateStatus(appointment.id, "CANCELADO")
                        sendSystemNotification(
                            "Cita Cancelada ⚠️",
                            "La cita de ${appointment.clientName} ha sido cancelada."
                        )
                        loadList()
                    }
                    .setNegativeButton("Volver", null)
                    .show()
            }
        )

        // Manejo de "Lista vacía"
        val emptyText = findViewById<TextView>(R.id.tvEmptyState)
        if (emptyText != null) {
            emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    // --- FUNCIONES DE NOTIFICACIÓN ---

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun sendSystemNotification(title: String, content: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Admin Notificaciones", NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
