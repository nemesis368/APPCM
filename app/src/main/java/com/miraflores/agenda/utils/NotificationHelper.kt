package com.miraflores.agenda.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.miraflores.agenda.R

object NotificationHelper {

    private const val CHANNEL_ID = "agenda_notification_channel"
    private const val CHANNEL_NAME = "Notificaciones de Citas"
    private const val CHANNEL_DESC = "Avisos sobre el estado de tus citas médicas"

    fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Crear el Canal de Notificaciones (Obligatorio para Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Construir la notificación visual
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            // Usamos un icono del sistema para asegurar que no falle si te falta alguno propio
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // Para textos largos
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Se borra al tocarla

        // 3. Mostrar la notificación (Usamos la hora actual como ID único)
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
