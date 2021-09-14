package com.amarchaud.travelcar.app.domain

import android.net.Uri
import com.amarchaud.travelcar.data.models.ApiCar
import com.amarchaud.travelcar.domain.models.AppCar
import java.util.UUID
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