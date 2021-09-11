package com.amarchaud.travelcar.utils

import android.net.Uri
import androidx.room.TypeConverter
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

sealed class UriAdapter {

    /**
     * For Api
     */
    object UriApiConverter : UriAdapter() {

        @ToJson
        fun uriToString(uri: Uri?): String? {// from me to api
            return uri?.toString()
        }

        @FromJson
        fun stringToUri(uriStr: String?): Uri? { // from api to me
            return uriStr?.let { Uri.parse(it) }
        }
    }


    /**
     * For Db
     */
    object UriDbConverter : UriAdapter() {
        @TypeConverter
        fun uriToString(uri: Uri?): String? { // call when insert data in db
            return uri?.toString()
        }

        @TypeConverter
        fun stringToUri(uriStr: String?): Uri? { // call when loading db
            return uriStr?.let { Uri.parse(it) }
        }
    }

}