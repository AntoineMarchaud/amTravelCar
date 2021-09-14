package com.amarchaud.travelcar.domain.repository

import com.amarchaud.travelcar.domain.models.AppUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserFlow() : Flow<AppUser?>
    suspend fun getUser() : AppUser?
    suspend fun manageUser(appUser: AppUser)
    suspend fun deleteUser()
}