package com.amarchaud.travelcar.data.repository.user

import com.amarchaud.travelcar.domain.local.user.AppUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserFlow() : Flow<AppUser?>
    suspend fun getUser() : AppUser?
    suspend fun manageUser(appUser: AppUser)
    suspend fun deleteUser()
}