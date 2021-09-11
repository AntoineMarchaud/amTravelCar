package com.amarchaud.travelcar.domain.db.car

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "car_option_cross_ref",
    primaryKeys = ["car_id", "option_id"],
    foreignKeys = [
        ForeignKey(
            entity = EntityCar::class,
            parentColumns = ["id"],
            childColumns = ["car_id"],
            onDelete = ForeignKey.NO_ACTION),
        ForeignKey(
            entity = EntityOption::class,
            parentColumns = ["id"],
            childColumns = ["option_id"],
            onDelete = ForeignKey.NO_ACTION)
    ]
)
data class EntityCarOptionCrossRef(
    @ColumnInfo(name = "car_id", index = true) val carId: Long,
    @ColumnInfo(name = "option_id", index = true) val optionId: Long
)