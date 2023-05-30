package com.bayride.presentation.services

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import javax.inject.Inject

class BayRideFirebaseMessagingService :
    FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken: $token")
       // secureStorageManager?.Device_token = token
    }
}

