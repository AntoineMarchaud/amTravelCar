package com.amarchaud.travelcar.domain.db.user

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.threeten.bp.LocalDate

@Entity(
    tableName = "user"
)
data class EntityUser(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") @NotNull val id: Long = 0,
    @ColumnInfo(name = "photo_uri") @Nullable var photoUri: Uri?,
    @ColumnInfo(name = "first_name") @Nullable var firstName: String?,
    @ColumnInfo(name = "last_name") @Nullable var lastName: String?,
    @ColumnInfo(name = "address") @Nullable var address: String?,
    @ColumnInfo(name = "birthday") @Nullable var birthday: LocalDate?
)