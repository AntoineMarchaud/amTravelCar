package com.amarchaud.travelcar.domain.db.car

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@Entity(
    tableName = "car"
)
data class EntityCar(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") @NotNull val id: Long = 0,
    @ColumnInfo(name = "make") @NotNull val make: String,
    @ColumnInfo(name = "model") @NotNull val model: String,
    @ColumnInfo(name = "year") @NotNull var year: Int,
    @ColumnInfo(name = "picture") @Nullable var picture: Uri?,
    @ColumnInfo(name = "equipments") @NotNull var equipments: List<String>?, // the easy way, but not cool for migrations, and can be very big !
)