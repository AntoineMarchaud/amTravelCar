package com.amarchaud.travelcar.domain.local.car

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppCar(
    val make: String,
    val model: String,
    val year: Int,
    val picture: Uri?,
    val equipments: List<String>?
) : Parcelable