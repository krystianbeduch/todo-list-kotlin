package com.krybed.todolist.data.model.enums

import android.content.Context
import androidx.annotation.StringRes
import com.krybed.todolist.R

enum class SortType (
    @StringRes val stringResId: Int
) {
    CREATED_DATE(R.string.sort_created_at),
    TITLE(R.string.sort_title),
    DEADLINE(R.string.sort_deadline),
    PRIORITY(R.string.sort_priority),
    STATUS(R.string.sort_status);

    companion object {
        fun fromDisplayName(context: Context, name: String): SortType =
            entries.firstOrNull {
                context.getString(it.stringResId) == name
            }
                ?: throw IllegalArgumentException("Unknown display name: $name")
    }
}