package com.amarchaud.travelcar.app.domain

import android.net.Uri
import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.domain.remote.car.ApiCar
import java.util.*
import kotlin.random.Random

class CarFactory {

    companion object {

        fun mockApiCar() = ApiCar(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            Random.nextInt(1990, 2021),
            Uri.parse(UUID.randomUUID().toString()),
            emptyList()
        )

        fun mockCar() = AppCar(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            Random.nextInt(1990, 2021),
            Uri.parse(UUID.randomUUID().toString()),
            emptyList()
        )
    }
}