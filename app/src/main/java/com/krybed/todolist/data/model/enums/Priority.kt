package com.krybed.todolist.data.model.enums

import com.krybed.todolist.R

enum class Priority (
    val value: Int,
    val stringResId: Int
) {
    HIGH(0, R.string.high_priority),
    MEDIUM(1, R.string.medium_priority),
    LOW(2, R.string.low_priority);

    companion object {
        fun fromInt(value: Int): Priority =
            entries.firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("Unknown priority: $value")
    }
}