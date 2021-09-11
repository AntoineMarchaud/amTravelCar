package com.amarchaud.travelcar.domain.db.car

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * use a junction table, because a option can be in several cars, and a car can have several options
 */
data class CarWithOptions(
    @Embedded var car: EntityCar,
    @Relation(
        parentColumn = "id",
        entity = EntityOption::class,
        entityColumn = "id",
        associateBy = Junction(
            value = EntityCarOptionCrossRef::class,
            parentColumn = "car_id",
            entityColumn = "option_id"
        )
    )
    var options: List<EntityOption>?
)