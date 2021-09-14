package com.amarchaud.travelcar.di

import com.amarchaud.travelcar.data.repository.AppCarRepository
import com.amarchaud.travelcar.data.repository.AppUserRepository
import com.amarchaud.travelcar.domain.repository.CarRepository
import com.amarchaud.travelcar.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindAppCarRepository(appRepository: AppCarRepository): CarRepository

    @Singleton
    @Binds
    abstract fun bindAppUserRepository(appRepository: AppUserRepository): UserRepository
}