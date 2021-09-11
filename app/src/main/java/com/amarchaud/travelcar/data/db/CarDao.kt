package com.amarchaud.travelcar.data.db

import androidx.room.*
import com.amarchaud.travelcar.domain.db.car.CarWithOptions
import com.amarchaud.travelcar.domain.db.car.EntityCar
import com.amarchaud.travelcar.domain.db.car.EntityCarOptionCrossRef
import com.amarchaud.travelcar.domain.db.car.EntityOption
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCar(car: EntityCar): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCars(cars: List<EntityCar>)

    @Query("SELECT * from car where make=:make AND model=:model")
    suspend fun getCarByMarkAndModel(make: String, model: String): EntityCar?

    @Update
    suspend fun updateCar(car: EntityCar)

    @Query("SELECT * from car")
    fun getCarsFlow(): Flow<List<EntityCar>?>

    @Query("SELECT * from car")
    suspend fun getCars(): List<EntityCar>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRelation(relation: EntityCarOptionCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addOption(option: EntityOption)

    @Query("SELECT * from option where option=:option")
    suspend fun getOption(option: String): EntityOption?

    @Transaction
    @Query("SELECT * from car where make=:make AND model=:model")
    suspend fun getCarWithOptionByMarkAndModel(make: String, model: String): CarWithOptions?

    @Transaction
    @Query("SELECT * from car")
    fun getCarsWithOptionsFlow(): Flow<List<CarWithOptions>?>

    @Transaction
    @Query("SELECT * from car")
    suspend fun getCarsWithOptions(): List<CarWithOptions>?
}