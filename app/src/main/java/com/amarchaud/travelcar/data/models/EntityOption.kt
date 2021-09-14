package com.amarchaud.travelcar.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(
    tableName = "option"
)
data class EntityOption(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") @NotNull val id: Long = 0,
    @ColumnInfo(name = "option") @NotNull val option: String,
)