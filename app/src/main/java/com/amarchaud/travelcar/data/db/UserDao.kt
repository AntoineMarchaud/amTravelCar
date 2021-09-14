package com.amarchaud.travelcar.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.amarchaud.travelcar.data.models.EntityUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(entityUser: EntityUser)

    @Update
    suspend fun updateUser(entityUser: EntityUser)

    @Query("SELECT * from user")
    suspend fun getUser(): EntityUser?

    @Query("SELECT * from user")
    fun getUserFlow(): Flow<EntityUser?>

    @Query("SELECT COUNT(*) from user")
    fun isUserExist(): Flow<Int?>

    @Delete
    suspend fun deleteUser(entityUser: EntityUser)
}