package com.krybed.todolist.util.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.krybed.todolist.R
import com.krybed.todolist.domain.model.Task
import com.krybed.todolist.util.converter.Converters

object NotificationUtils {
    const val CHANNEL_ID = "tasks_channel_kt"

    fun createNotificationChannel(ctx: Context) {
        val name: CharSequence = "Task Notifications"
        val description = "Notifications for due or overdue tasks"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            this.description = description
        }

        val notificationManager = ctx.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    fun showTaskNotification(ctx: Context, task: Task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // User has not consented to receive notifications
                return
            }
        }

        val builder = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(task.notificationType.getTextToNotification(ctx))
            .setContentText(
                "${task.title}: \n${Converters.formatLocalDateTimeToStringWithDayName(ctx, task.deadline)}"
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(ctx)
        notificationManager.notify(task.id, builder.build())
    }
}