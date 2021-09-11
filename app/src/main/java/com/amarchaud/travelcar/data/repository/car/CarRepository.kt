package com.amarchaud.travelcar.data.repository.car

import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.domain.local.user.AppUser
import kotlinx.coroutines.flow.Flow

interface CarRepository {
    suspend fun update() : Boolean
    fun getCarsFromDbFlow() : Flow<List<AppCar>?>
    fun getCarsFlow(): Flow<List<AppCar>?>
}