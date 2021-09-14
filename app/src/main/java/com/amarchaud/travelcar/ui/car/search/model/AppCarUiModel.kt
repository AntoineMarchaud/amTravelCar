package com.amarchaud.travelcar.ui.car.search.model

import android.net.Uri
import android.os.Parcelable
import com.amarchaud.travelcar.domain.models.AppCar
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppCarUiModel(
    val make: String,
    val model: String,
    val year: Int,
    val picture: Uri?,
    val equipments: List<String>?
) : Parcelable

fun AppCar.toUiModel() = AppCarUiModel(
    make = this.make,
    model = this.model,
    year = this.year,
    picture = this.picture,
    equipments = this.equipments,
)