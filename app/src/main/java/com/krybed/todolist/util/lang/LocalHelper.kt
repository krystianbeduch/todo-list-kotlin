package com.krybed.todolist.util.lang

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.krybed.todolist.R
import java.util.Locale

object LocalHelper {

    private const val PREFS_NAME = "settings"
    private const val KEY_LANGUAGE = "language"

    fun setLocale(ctx: Context, languageCode: String): Context {
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(ctx.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        val prefs: SharedPreferences =
            ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_LANGUAGE, languageCode) }

        return ctx.createConfigurationContext(config)
    }

    fun applySavedLocale(ctx: Context): Context {
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val language = prefs.getString(KEY_LANGUAGE, Locale.getDefault().language)
            ?: Locale.getDefault().language
        return setLocale(ctx, language)
    }

    fun showChangeLanguageDialog(ctx: Context) {
        AlertDialog.Builder(ctx)
            .setTitle(ctx.getString(R.string.select_lang))
            .setItems(arrayOf("Polski", "English")) { _, which ->
                val selectedLanguage = if (which == 0) "pl-PL" else "en-GB"
                changeLanguage(ctx, selectedLanguage)
            }
            .setNegativeButton(ctx.getString(R.string.cancel), null)
            .show()
    }

    fun changeLanguage(ctx: Context, languageCode: String) {
        setLocale(ctx, languageCode)
        if (ctx is Activity) {
            ctx.recreate()
        }
    }

    fun getSavedLanguage(ctx: Context): String {
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, Locale.getDefault().language)
            ?: Locale.getDefault().language
    }
}