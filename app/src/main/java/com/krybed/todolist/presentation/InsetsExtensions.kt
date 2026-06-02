package com.krybed.todolist.presentation

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// InsetsExtensions.kt
fun View.applyRecyclerViewInsets(bottomNavHeightDp: Int = 56) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout()
        )
        val bottomNavPx = (bottomNavHeightDp * resources.displayMetrics.density).toInt()
        view.setPadding(
            view.paddingLeft,
            bars.top,
            view.paddingRight,
            bars.bottom + bottomNavPx
        )
        insets // NIE konsumuj! Zwróć oryginalne insets
    }
}