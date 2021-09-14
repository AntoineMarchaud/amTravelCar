package com.amarchaud.travelcar.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amarchaud.travelcar.data.models.EntityCar
import com.amarchaud.travelcar.data.models.EntityCarOptionCrossRef
import com.amarchaud.travelcar.data.models.EntityOption
import com.amarchaud.travelcar.data.models.EntityUser
import com.amarchaud.travelcar.utils.UriAdapter
import com.amarchaud.travelcar.utils.data_adapter.ListStringAdapter
import com.amarchaud.travelcar.utils.data_adapter.LocalDateAdapter


@Database(
    entities = [
        EntityCar::class, EntityOption::class, EntityCarOptionCrossRef::class,
        EntityUser::class
    ], version = 1, exportSchema = false
)
@TypeConverters(
    UriAdapter.UriDbConverter::class,
    ListStringAdapter.ListStringDbConverter::class,
    LocalDateAdapter.LocalDateDbConverter::class
)
abstract class AppDb : RoomDatabase() {
    abstract fun getCarDao(): TravelCarDao
    abstract fun getUserDao() : UserDao
}