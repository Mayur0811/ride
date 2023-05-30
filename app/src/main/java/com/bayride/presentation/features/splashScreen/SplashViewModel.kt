package com.bayride.presentation.features.splashScreen

import android.content.ContentValues.TAG
import android.location.Location
import android.provider.Settings
import android.util.Log
import com.bayride.data.datasources.local.ISecureStorageManager
import com.bayride.presentation.base.BaseViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(var securityManager: ISecureStorageManager) :
    BaseViewModel<SplashViewState, SplashEvent>() {
    override fun initState(): SplashViewState {
        return SplashViewState()
    }

    init {
        dispatchState(currentState.copy(token = securityManager.token))
    }

    fun getCurrentLocation(location: Location) {
        securityManager.latitude = location.latitude.toString()
        securityManager.longitude = location.longitude.toString()
    }

    fun getDeviceToken(device_id: String) {
        Log.d(TAG, "userToken: ${securityManager.token}")
        securityManager.deviceId = device_id
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                val token = task.result
                securityManager.Device_token = token
                Log.d(TAG, "getDeviceToken: $token")

            })
    }


}