package com.bayride

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.internal.functions.Functions.emptyConsumer
import io.reactivex.rxjava3.plugins.RxJavaPlugins

@HiltAndroidApp
class BayRideApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    RxJavaPlugins.setErrorHandler(emptyConsumer())
    FirebaseApp.initializeApp(this)
  }
}