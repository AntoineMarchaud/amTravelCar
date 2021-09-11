package com.amarchaud.travelcar.utils.translator

import com.amarchaud.travelcar.domain.db.car.CarWithOptions
import com.amarchaud.travelcar.domain.db.car.EntityCar
import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.domain.remote.car.ApiCar

object CarTranslator {

    fun ApiCar.toAppCar() = AppCar(
        make = this.make,
        model = this.model,
        year = this.year,
        picture = this.picture,
        equipments = this.equipments
    )

    fun CarWithOptions.toAppCar() = AppCar(
        make = this.car.make,
        model = this.car.model,
        year = this.car.year,
        picture = this.car.picture,
        equipments = this.options?.map { it.option },
    )

    fun AppCar.toEntityCar() = EntityCar(
        make = this.make,
        model = this.model,
        year = this.year,
        picture = this.picture,
        equipments = this.equipments
    )

    fun EntityCar.toAppCar() = AppCar(
        make = this.make,
        model = this.model,
        year = this.year,
        picture = this.picture,
        equipments = this.equipments
    )
}