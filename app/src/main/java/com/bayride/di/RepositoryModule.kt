package com.bayride.di

import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.data.repositories.authentication.AuthenticationRepositoryImpl
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.data.repositories.driver.DriverRepositoryImpl
import com.bayride.data.repositories.passenger.PassengerRepository
import com.bayride.data.repositories.passenger.PassengerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun provideAuthenticationRepositoryImpl(authenticationRepositoryImpl: AuthenticationRepositoryImpl): AuthenticationRepository

    @Binds
    abstract fun provideDriverRepositoryImpl(driverRepositoryImpl: DriverRepositoryImpl): DriverRepository

    @Binds
    abstract fun providePassengerRepositoryImpl(passengerRepositoryImpl: PassengerRepositoryImpl): PassengerRepository
}