package com.bayride.di

import com.bayride.BuildConfig
import com.bayride.NetworkConfig
import com.bayride.data.datasources.remote.api.ApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import com.bayride.data.datasources.interceptor.AuthInterceptor
import com.bayride.data.datasources.local.ISecureStorageManager
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@ExperimentalSerializationApi
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    private val jsonConfig = Json {
        isLenient = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(securityManager: ISecureStorageManager): OkHttpClient =
        with(OkHttpClient.Builder()) {
            callTimeout(NetworkConfig.CALL_TIMEOUT, TimeUnit.MINUTES)
            connectTimeout(NetworkConfig.CONNECT_TIMEOUT, TimeUnit.MINUTES)
            readTimeout(NetworkConfig.READ_TIMEOUT, TimeUnit.MINUTES)
            writeTimeout(NetworkConfig.WRITE_TIMEOUT, TimeUnit.MINUTES)
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                    .apply { level = HttpLoggingInterceptor.Level.BODY }
                addInterceptor(loggingInterceptor)
            }
            //securityManager: ISecureStorageManager
            addNetworkInterceptor(AuthInterceptor(securityManager))
            build()
        }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit.Builder {
        val responseContentType = "application/json".toMediaType()
        val converterFactory = jsonConfig.asConverterFactory(responseContentType)
        return Retrofit.Builder()
            .addConverterFactory(converterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    }

    @Provides
    @Singleton
    fun provideApiService(retrofitBuilder: Retrofit.Builder): ApiService {
        return retrofitBuilder
            .baseUrl(NetworkConfig.API_DOMAIN_DEFAULT)
            .build()
            .create(ApiService::class.java)
    }
}