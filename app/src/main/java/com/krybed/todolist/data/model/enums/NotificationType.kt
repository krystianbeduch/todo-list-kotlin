package com.krybed.todolist.data.model.enums

import android.content.Context
import com.krybed.todolist.R

enum class NotificationType {
    UPCOMING,
    OVERDUE,
    NONE;

    fun getTextToNotification(context: Context): String =
        when (this) {
            UPCOMING -> context.getString(R.string.text_notification_upcoming) + "\n"
            OVERDUE -> context.getString(R.string.text_notification_overdue) + "\n"
            NONE -> ""
        }
}