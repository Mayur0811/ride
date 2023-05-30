package com.bayride.di

import android.content.Context
import com.bayride.data.datasources.local.IPreferenceStorage
import com.bayride.data.datasources.local.ISecureStorageManager
import com.bayride.data.datasources.local.PreferenceStorage
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.memory.IMemoryDataStorage
import com.bayride.data.datasources.memory.MemoryDataStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
  @Provides
  @Singleton
  fun provideMemoryStorage(): IMemoryDataStorage = MemoryDataStorage()

  @Provides
  @Singleton
  fun providerSecureStorageManager(@ApplicationContext context: Context): ISecureStorageManager =
    SecureStorageManager(context)

  @Provides
  @Singleton
  fun providerSharedPreference(@ApplicationContext context: Context): IPreferenceStorage =
    PreferenceStorage(context)

//  @Provides
//  @Singleton
//  fun provideGlobalInteractor(): GlobalInteractor = GlobalInteractor()
}