package com.bayride.di

import com.bayride.data.datasources.remote.*
import com.bayride.data.datasources.remote.IPassengerRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {

    @Binds
    abstract fun provideAuthenticateRemoteDataSource(authenticationRemoteDataSource: AuthenticationRemoteDataSource): IAuthenticationRemoteDataSource

    @Binds
    abstract fun provideDriverRemoteDataSource(driverRemoteDataSource: DriverRemoteDataSource): IDriverRemoteDataSource


    @Binds
    abstract fun providePassengerRemoteDataSource(passengerRemoteDataSource: PassengerRemoteDataSource): IPassengerRemoteDataSource


}