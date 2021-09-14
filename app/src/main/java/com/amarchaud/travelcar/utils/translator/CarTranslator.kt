package com.amarchaud.travelcar.utils.translator

import com.amarchaud.travelcar.data.models.ApiCar
import com.amarchaud.travelcar.data.models.CarWithOptions
import com.amarchaud.travelcar.data.models.EntityCar
import com.amarchaud.travelcar.domain.models.AppCar

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