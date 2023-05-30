package com.bayride.presentation.features.splashScreen

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bayride.presentation.homeActivity.HomeActivity
import com.bayride.common.permission.showPermanentlyDeniedDialog
import com.bayride.common.permission.showRationaleDialog
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.utils.Constants
import com.bayride.databinding.FragmentSplashScreenBinding
import com.bayride.presentation.base.BaseFragment
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreen : BaseFragment<FragmentSplashScreenBinding>(), PermissionRequest.Listener {
    var fusedLocationClient: FusedLocationProviderClient? = null
    val viewModel: SplashViewModel by viewModels()

    override fun initData(data: Bundle?) {

    }

    override fun initViews() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        request.addListener(this)
        request.send()


    }

    override fun initActions() {

    }

    override fun initObservers() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
        activity?.finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getLastLocation() {
        if (isLocationEnabled()) {
            fusedLocationClient?.lastLocation?.addOnCompleteListener {
                if (it.result != null) {
                    val location: Location = it.result
                    viewModel.getCurrentLocation(location)
                    viewModel.getDeviceToken(
                        Settings.Secure.getString(
                            context?.contentResolver,
                            Settings.Secure.ANDROID_ID
                        )
                    )

                    Handler().postDelayed({
                        val activity = activity
                        if (isAdded && activity != null) {
                            if (getEncryptedSharedPreferences(requireContext())?.getBoolean(
                                    Constants.isLogin,
                                    false
                                ) == true
                            ) {
                                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                            } else {
                                if (getEncryptedSharedPreferences(requireContext())?.getBoolean(
                                        "isSignup",
                                        false
                                    ) == false
                                ) {
                                    lifecycleScope.launchWhenResumed {
                                        findNavController().navigate(SplashScreenDirections.actionSplashScreenToWelcomeScreen())
                                    }
                                } else {
                                    startActivity(Intent(activity, HomeActivity::class.java))
                                }
                            }
                        }
                    }, 2000)

                } else {
                    requestNewLocationData()
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Please turn on" + " your location...",
                Toast.LENGTH_LONG
            )
                .show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        Looper.myLooper()?.let {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, it)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            Log.d(TAG, "onLocationResult: " + lastLocation.longitude + " " + lastLocation.longitude)
        }
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> requireContext().showPermanentlyDeniedDialog(result)
            result.anyShouldShowRationale() -> requireContext().showRationaleDialog(result, request)
            result.allGranted() -> {
                getLastLocation()



            }
        }
    }

    private val request by lazy {
        permissionsBuilder(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).build()
    }


}







