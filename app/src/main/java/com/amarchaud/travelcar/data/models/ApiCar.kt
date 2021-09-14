package com.amarchaud.travelcar.data.models

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ApiCar(
    @Json(name = "make")
    val make: String,
    @Json(name = "model")
    val model: String,
    @Json(name = "year")
    val year: Int,
    @Json(name = "picture")
    val picture: Uri?,
    @Json(name = "equipments")
    val equipments: List<String>?
)