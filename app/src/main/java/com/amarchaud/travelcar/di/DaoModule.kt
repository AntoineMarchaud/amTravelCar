package com.amarchaud.travelcar.di

import android.content.Context
import androidx.room.Room
import com.amarchaud.travelcar.data.db.AppDb
import com.amarchaud.travelcar.data.db.CarDao
import com.amarchaud.travelcar.data.db.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): AppDb {
        return Room.databaseBuilder(appContext, AppDb::class.java, "travel_car_db")
            .build()
    }

    @Singleton
    @Provides
    fun provideCarDao(appDb: AppDb): CarDao {
        return appDb.getCarDao()
    }

    @Singleton
    @Provides
    fun provideUserDao(appDb: AppDb): UserDao {
        return appDb.getUserDao()
    }
}