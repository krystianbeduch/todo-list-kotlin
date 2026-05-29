package com.krybed.todolist.data.model.enums

import android.content.Context
import androidx.annotation.StringRes
import com.krybed.todolist.R

enum class Priority (
    val value: Int,
    @StringRes val stringResId: Int
) {
    HIGH(0, R.string.high_priority),
    MEDIUM(1, R.string.medium_priority),
    LOW(2, R.string.low_priority);

    companion object {
        fun fromInt(value: Int): Priority =
            entries.firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("Unknown priority: $value")

        fun fromDisplayName(context: Context, displayName: String): Priority =
            entries.firstOrNull {
                context.getString(it.stringResId) == displayName
            }
                ?: throw IllegalArgumentException("Unkown display name: $displayName")

        fun getPriorityIndex(priority: Priority): Int = priority.value
    }
}