package com.amarchaud.travelcar.utils.data_adapter

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

sealed class LocalDateAdapter {

    /**
     * For DB
     */
    object LocalDateDbConverter : LocalDateAdapter() {

        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        @TypeConverter
        fun localDateToString(localDate: LocalDate?): String? { // call when insert data in db
            return localDate?.let { formatter.format(it) }
        }

        @TypeConverter
        fun stringToLocalDate(str: String?): LocalDate? { // call when loading db
            return str?.let { LocalDate.parse(it, formatter) }
        }
    }
}