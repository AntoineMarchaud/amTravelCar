package com.amarchaud.travelcar.domain.repository

import com.amarchaud.travelcar.domain.models.AppCar
import kotlinx.coroutines.flow.Flow

interface CarRepository {
    suspend fun update() : Boolean
    fun getCarsFromDbFlow() : Flow<List<AppCar>?>
    fun getCarsFlow(): Flow<List<AppCar>?>
}