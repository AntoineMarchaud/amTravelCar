package com.amarchaud.travelcar.domain.models

import android.net.Uri

data class AppCar(
    val make: String,
    val model: String,
    val year: Int,
    val picture: Uri?,
    val equipments: List<String>?
)