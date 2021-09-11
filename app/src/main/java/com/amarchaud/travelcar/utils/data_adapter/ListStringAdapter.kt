package com.amarchaud.travelcar.utils.data_adapter

import androidx.room.TypeConverter
import com.google.gson.Gson


sealed class ListStringAdapter {

    /**
     * For Db
     */
    object ListStringDbConverter : ListStringAdapter() {
        @TypeConverter
        fun listStrToString(value: List<String>?): String? { // call when insert data in db
            val gson = Gson()
            return gson.toJson(value)
        }

        @TypeConverter
        fun stringToListStr(value: String?): List<String>? { // call when loading db
            return Gson().fromJson(value, Array<String>::class.java)?.toList()
        }
    }
}