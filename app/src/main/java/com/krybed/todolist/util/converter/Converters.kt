package com.krybed.todolist.util.converter

import android.content.Context
import androidx.room.TypeConverter
import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.util.lang.LocalHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object Converters {

    private val isoFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val uiFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    @TypeConverter
    fun fromStringToLocalDateTimeISO(value: String?): LocalDateTime? =
        value?.let { LocalDateTime.parse(it, isoFormatter) }

    @TypeConverter
    fun fromLocalDateTimeISOToString(dateTime: LocalDateTime?): String? =
        dateTime?.format(isoFormatter)

    @TypeConverter
    fun priorityToInt(priority: Priority?): Int? =
        priority?.value

    @TypeConverter
    fun fromIntToPriority(value: Int?): Priority? =
        value?.let { Priority.fromInt(it) }

    fun formatLocalDateTimeToStringWithDayName(
        context: Context,
        dateTime: LocalDateTime?
    ): String? =
        dateTime?.format(getFormatterWithDayName(context))

    @Throws(DateTimeParseException::class)
    fun fromStringToLocalDateTime(dateTimeText: String?): LocalDateTime {
        if (dateTimeText.isNullOrEmpty()) {
            throw DateTimeParseException("Empty date text", dateTimeText ?: "", 0)
        }
        return LocalDateTime.parse(dateTimeText, uiFormatter)
    }

    fun fromLocalDateTimeToString(dateTime: LocalDateTime?): String? =
        dateTime?.format(uiFormatter)

    private fun getFormatterWithDayName(ctx: Context): DateTimeFormatter {
        val languageCode: String = LocalHelper.getSavedLanguage(ctx)
        val locale = Locale.forLanguageTag(languageCode)
        return DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy HH:mm", locale)
    }
}