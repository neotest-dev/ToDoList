package com.azflodev.todolist

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlarmNotification : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("NOTIFICATION_TITLE") ?: "Título por defecto"
        val description = intent?.getStringExtra("NOTIFICATION_DESC") ?: "Descripción no disponible"
        val dateTime = intent?.getStringExtra("NOTIFICATION_DATE_TIME") ?: "Fecha y hora no disponibles"
        val priority = intent?.getStringExtra("NOTIFICATION_PRIORITY") ?: "Prioridad no disponible"
        val notificationId = intent?.getIntExtra("NOTIFICATION_ID", 0) ?: 0
        createSimpleNotification(context, title, description, dateTime, priority, notificationId)
    }

    private fun createSimpleNotification(
        context: Context,
        title: String,
        description: String,
        dateTime: String,
        priority: String,
        notificationId: Int
    ) {
        val notificationIntent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, MyAdapter.MY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle("Tarea: $title")
            .setContentText("Descripción: $description\nFecha y Hora: $dateTime\nPrioridad: $priority")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Descripción: $description\nFecha y Hora: $dateTime\nPrioridad: $priority"))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification) // Usar el ID único para cada notificación
    }
}