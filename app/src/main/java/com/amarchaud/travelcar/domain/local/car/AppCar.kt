package com.amarchaud.travelcar.domain.local.car

import android.graphics.Color
import android.net.Uri
import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppCar(
    val make: String,
    val model: String,
    val year: Int,
    val picture: Uri?,
    val equipments: List<String>?
) : Parcelable