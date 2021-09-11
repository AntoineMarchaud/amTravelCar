package com.amarchaud.travelcar.domain.db.car

import androidx.room.*
import org.jetbrains.annotations.NotNull

@Entity(
    tableName = "option"
)
data class EntityOption(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") @NotNull val id: Long = 0,
    @ColumnInfo(name = "option") @NotNull val option: String,
)