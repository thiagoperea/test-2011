package com.thiagoperea.mybills.core.converter

import java.text.SimpleDateFormat
import java.util.*

object DateConverter {

    const val FORMAT_RAW = "dd-MM-yyyy"
    const val FORMAT_DD_MM_YYYY = "dd/MM/yyyy"
    const val FORMAT_MMMM_YYYY = "MMMM/yyyy"

    /**
     * Creates Date object from String "dd-MM-yyyy"
     */
    fun getDateFromString(dateString: String, inputFormat: String = FORMAT_RAW): Date? {
        val dateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
        return dateFormat.parse(dateString)
    }

    fun getStringFromDate(date: Date?, outputFormat: String): String {
        if (date == null) {
            return ""
        }

        val dateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
        return dateFormat.format(date)
    }

    fun formatTime(hour: Int, minute: Int) = String.format("%02d:%02d", hour, minute)

    fun getTodayAsCalendar() = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}