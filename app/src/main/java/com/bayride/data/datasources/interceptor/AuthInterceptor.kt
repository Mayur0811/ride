package com.bayride.data.datasources.interceptor

import android.util.Log
import com.bayride.BuildConfig
import com.bayride.data.datasources.local.ISecureStorageManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val secureStorageManager: ISecureStorageManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = secureStorageManager.token
        if (BuildConfig.DEBUG)
            Log.d("TAG", "Header Token: Token $token")
        if (token.isNotEmpty()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request.newBuilder()
                .build()
        }
        return chain.proceed(request)
    }
}