package com.bayride.presentation.features.home

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.utils.Constants
import com.bayride.common.utils.Constants.Companion.REMOVE_BLUR
import com.bayride.common.utils.Constants.Companion.ongoing
import com.bayride.common.views.blur
import com.bayride.common.views.getLoginDetails
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.databinding.HomeScreenBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.driver.filter.FilterScreen
import com.bayride.presentation.features.ongoing.OngoingScreen
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.SphericalUtil
import com.google.maps.model.TravelMode
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderScriptBlur
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@AndroidEntryPoint
class HomeScreen : BaseFragment<HomeScreenBinding>(), OnMapReadyCallback,
    FilterScreen.BottomSheetListener,
    OngoingScreen.BottomSheetListener, LocationListener, FilterScreen.OnFilterUserList {
    lateinit var googleMap: GoogleMap
    var file: File? = null
    lateinit var locationManager: LocationManager
    lateinit var myLocationMarker: Marker
    lateinit var lastLocation: Location
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    val viewModel: HomeScreenViewModel by viewModels()
    var markers: ArrayList<Marker> = arrayListOf()
    var myLayout: View? = null

    @Inject
    lateinit var secureStorageManager: SecureStorageManager
    override fun initData(data: Bundle?) {

    }

    override fun initViews() {
        //getLastLocation()
        val inflater = layoutInflater
        myLayout = inflater.inflate(R.layout.fair_user_item, binding.framLayout, false)
        viewModel.loadFair(requireContext())
        getLoginDetails(requireContext())?.info
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        val decorView: View? = activity?.window?.decorView
        val rootView = decorView?.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        binding.blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(requireContext()))
            .setBlurRadius(5F)



        updateStatusBarColor(ContextCompat.getColor(requireActivity(), android.R.color.white))
        val supportMapFragment = binding.map1.getFragment<SupportMapFragment>()
        supportMapFragment.getMapAsync(this)

        binding.search.queryHint = "Enter Destination"
        (binding.search.findViewById<View>(
            binding.search.context.resources.getIdentifier(
                "android:id/search_plate",
                null,
                null
            )
        ))?.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun initActions() {
        file = File(
            requireContext().filesDir.toString() + File.separator,
            "map.png"
        )
        safetyClick.setViewClickSafetyListener(binding.btnStartRide) {
            findNavController().navigate(HomeScreenDirections.actionHomeScreen2ToAddInformationScreen3())
        }
        binding.search.setOnClickListener {
            binding.search.onActionViewExpanded()
        }
        binding.appCompatImageView.setOnClickListener {
            activity?.findViewById<DrawerLayout>(R.id.drawer_layout)?.openDrawer(Gravity.START)
        }
        binding.filterIcon.setOnClickListener {
            findNavController().navigate(R.id.filterScreen2)
            binding.blurView.isVisible = true
            binding.blurImage.isVisible = true
            val bottomsheetFilter = FilterScreen()
            bottomsheetFilter.setOnFilterListener(this)
            val bottomSheetOnGoing = OngoingScreen()
            bottomSheetOnGoing.setBottomSheetListener(this)


        }
    }

    override fun initObservers() {
        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.loading }) {
            if (it == true) showLoading() else hideLoading()
        }
        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.fairload }) {
            if (it == true) {
                if (getEncryptedSharedPreferences(requireContext())?.getInt("type", 3) == 1) {
                    if (ongoing) {
                        binding.search.visible(false)
                        binding.btnStartRide.visible(false)
                        ongoing = false
                        Handler().postDelayed({
                            findNavController().navigate(HomeScreenDirections.actionHomeScreen2ToOngoingScreenHomeNavGraph())
                        }, 1000)
                    }
                } else {
                    binding.search.visible(false)
                    binding.btnStartRide.visible(false)
                    binding.filterIcon.visible(true)
                }
            }
        }
        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.isDriver }) {
            if (it == true) {
                binding.search.visible(false)
                binding.btnStartRide.visible(false)
                binding.filterIcon.visible(true)
                viewModel.getDriverOnGoingDetails()
            }
        }
        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.fairList }) {
            if (it != null) {
                if (getEncryptedSharedPreferences(requireContext())?.getInt("type", 3) == 1) {
                    if (it.Info?.fare_status == 2) {
                        binding.search.visible(false)
                        binding.btnStartRide.visible(false)
                        ongoing = false
                        Handler().postDelayed({
                            findNavController().navigate(HomeScreenDirections.actionHomeScreen2ToOngoingScreenHomeNavGraph())
                        }, 1000)
                    } else if (it.Info?.fare_status == 0) {
                        findNavController().navigate(HomeScreenDirections.actionHomeScreen2ToHomeDetailsScreen())
                    }
                }
            }
        }

        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.fairUserList }) {

        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                HomeScreeSuccessEvent -> {

                }
                ListFairUserSuccess -> {
                    val userList = viewModel.currentState.fairUserList
                    googleMap.isMyLocationEnabled = true
                    locationManager =
                        requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        val location =
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            userList?.forEachIndexed { pos, users ->
                                googleMap.addMarker(
                                    MarkerOptions()
                                        .position(
                                            LatLng(
                                                users.from_lat ?: 0.0,
                                                users.from_long ?: 0.0
                                            )
                                        )
                                        .anchor(0.5f, 0.5f)
                                        .title(users.user_name)
                                        .snippet(users.fare_id.toString())
                                        .icon(
                                            getMarkerBitmapFromView(Constants.imageDomain + users.user_profile_pic)?.let { bmp ->
                                                BitmapDescriptorFactory.fromBitmap(bmp)
                                            }

                                        )
                                )
                                googleMap.setOnMarkerClickListener {
                                    if(it == myLocationMarker){
                                        Toast.makeText(requireActivity(), "${myLocationMarker.title}", Toast.LENGTH_SHORT).show()
                                    }else {
                                        secureStorageManager.driverFairId = it.snippet?.toInt() ?: 0
                                        findNavController().navigate(R.id.sendOfferScreen)
                                    }
                                    true
                                }
//                                createMarker(
//                                    users.to_lat ?: 0.0,
//                                    users.to_long ?: 0.0,
//                                    users.user_name,
//                                    null,
//                                    myLayout?.let { createDrawableFromView(requireContext(), it) }
//                                )?.let {
//                                    markers.add(
//                                        it
//                                    )
//                                }
//                                googleMap.addMarker(
//                                    MarkerOptions().position(
//                                        LatLng(
//                                            users.to_lat ?: 0.0,
//                                            users.to_long ?: 0.0
//                                        )
//                                    ).title(users.user_name)
//                                )!!


//                                myLayout?.findViewById<CircleImageView>(R.id.profile)?.let {
//                                    Glide.with(requireContext())
//                                        .load(Constants.imageDomain + users.user_profile_pic).into(
//                                        it
//                                    )
//                                }

                                googleMap.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(
                                            users.to_lat ?: 0.0,
                                            users.to_long ?: 0.0
                                        ), 5f
                                    )
                                )
                            }

                        }
                    }
                }
                DriverOnGoingFairDetailsSuccess -> {
                    if (viewModel.currentState.driverOngoingFairDetails?.Status == 1) {
                        if (viewModel.currentState.driverOngoingFairDetails?.info != null) {
                            val driverOngoingFairDetails =
                                viewModel.currentState.driverOngoingFairDetails
                            findNavController().navigate(
                                HomeScreenDirections.actionHomeScreen2ToDriverOnGoingScreen(
                                    driverBookingInfo = null,
                                    driverOngoingFairDetails
                                )
                            )
                        }
                    } else {
                        context?.showAlertDialog(
                            title = getString(R.string.Error),
                            message = viewModel.currentState.driverOngoingFairDetails?.Message.toString(),
                            cancelable = true,
                            button = getString(R.string.btn_ok)
                        )
                    }
                }
                is HomeScreeErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
                is HomeScreeFailedEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
            }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<Pair<Boolean, Pair<Int, Pair<Double, Double>>>>(REMOVE_BLUR)
                .observe(viewLifecycleOwner) { (key, value) ->
                    binding.blurView.isVisible = false
                    binding.blurImage.isVisible = false
                    viewModel.getFairUserList(value.first, value.second.first, value.second.second)
                }
        }
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            remove<Boolean>(REMOVE_BLUR)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        activity?.finishAffinity()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        locationManager =
            requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        requestPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) { areGrantedAll, _ ->
            if (getEncryptedSharedPreferences(requireContext())?.getInt("type", 3) == 1) {
                if (areGrantedAll) {
                    googleMap.isMyLocationEnabled = true
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        val location =
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            myLocationMarker = googleMap.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        location.latitude,
                                        location.longitude
                                    )
                                ).title("you are here").icon(
                                    getBitmapDescriptorFromVector(
                                        requireActivity(),
                                        R.drawable.ic_marker_green
                                    )
                                )
                            )!!
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        location.latitude,
                                        location.longitude
                                    ), 17f
                                )
                            )
                        }
                        locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            0f,
                            this
                        )

                    }
                    getRoute()
                }
            } else {
                viewModel.getFairUserList()

//                        myLocationMarker = googleMap.addMarker(
//                            MarkerOptions().position(
//                                LatLng(
//                                    location.latitude,
//                                    location.longitude
//                                )
//                            ).title("you are here").icon(
//                                getBitmapDescriptorFromVector(
//                                    requireActivity(),
//                                    R.drawable.ic_marker_green
//                                )
//                            )
//                        )!!
//                        googleMap.animateCamera(
//                            CameraUpdateFactory.newLatLngZoom(
//                                LatLng(
//                                    location.latitude,
//                                    location.longitude
//                                ), 17f
//                            )
//                        )
            }
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000,
                0f,
                this
            )

        }

        googleMap.setOnMapLoadedCallback { snapShot() }
    }


    fun snapShot() {

        val callback: GoogleMap.SnapshotReadyCallback =
            GoogleMap.SnapshotReadyCallback {
                var bitmap: Bitmap? = null
                bitmap = it
                try {
                    val fout = FileOutputStream(file)
                    bitmap!!.compress(Bitmap.CompressFormat.PNG, 90, fout)
                    binding.blurImage.setImageBitmap(blur(requireContext(), bitmap))

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        googleMap.snapshot(callback)

    }

    override fun onFilter(show: Boolean, radius: Float) {
        binding.blurImage.visible(show)
        binding.blurView.visible(show)
        binding.blurView.setBlurRadius(radius)
    }

    override fun onLocationChanged(location: Location) {

        if (::googleMap.isInitialized) {
            Log.e("location", "${location.latitude},${location.longitude}")
            if (!::lastLocation.isInitialized) {
                lastLocation = location
            }
            if (lastLocation != location) {
                lastLocation = location
                if (::myLocationMarker.isInitialized) {
                    myLocationMarker.remove()
                }
                myLocationMarker = googleMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    ).title("${location.latitude}, ${location.longitude}")
                        .icon(
                            getBitmapDescriptorFromVector(
                                requireContext(),
                                R.drawable.ic_marker_green
                            )
                        )

                )!!
//                googleMap.animateCamera(
//                    CameraUpdateFactory.newLatLngZoom(
//                        LatLng(
//                            location.latitude,
//                            location.longitude
//                        ), 17f
//                    )
//                )
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        super.onStatusChanged(provider, status, extras)
    }

    fun getRoute() {
        var path: MutableList<LatLng> = ArrayList()
        //Execute Directions API request
        //Execute Directions API request
        //AIzaSyDlZZmuIqfTfSeN70SpOvvLF_JKYseKIsE
        //AIzaSyD5NOOpYKObjlZbt_-4CUOMi14mObzbGNo
        val context = GeoApiContext.Builder()
            .apiKey("AIzaSyDlZZmuIqfTfSeN70SpOvvLF_JKYseKIsE")
            .build()

        val req =
            DirectionsApi.getDirections(
                context,
                "21.2402793,72.875444",
                "21.2113407,72.8449332"
            )
                .mode(
                    TravelMode.DRIVING
                )
        try {
            val res = req.await()
            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.size > 0) {
                val route = res.routes[0]
                if (route.legs != null) {
                    for (i in route.legs.indices) {
                        val leg = route.legs[i]
                        if (leg.steps != null) {
                            for (j in leg.steps.indices) {
                                val step = leg.steps[j]
                                if (step.steps != null && step.steps.size > 0) {
                                    for (k in step.steps.indices) {
                                        val step1 = step.steps[k]
                                        val points1 = step1.polyline
                                        if (points1 != null) {
                                            val coords1 = points1.decodePath()
                                            for (coord1 in coords1) {
                                                path.add(LatLng(coord1.lat, coord1.lng))
                                            }
                                        }
                                    }
                                } else {
                                    val points = step.polyline
                                    if (points != null) {
                                        val coords = points.decodePath()
                                        for (coord in coords) {
                                            path.add(LatLng(coord.lat, coord.lng))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ex: java.lang.Exception) {
            Log.e(TAG, ex.localizedMessage)
        }
        if (path.size != 0) {
            val destinationMarker =
                MarkerOptions().position(LatLng(21.2113407, 72.8449332))
                    .icon(
                        getBitmapDescriptorFromVector(
                            requireActivity(),
                            R.drawable.ic_marker_green
                        )
                    )
            destinationMarker.title("${destinationMarker.position.latitude},${destinationMarker.position.longitude}")
            val carMarker =
                MarkerOptions().position(LatLng(21.2402793, 72.875444))
                    .icon(
                        getBitmapDescriptorFromVector(
                            requireActivity(),
                            R.drawable.ic_car_green_icon
                        )
                    ).flat(true)
            carMarker.title("${carMarker.position.latitude},${carMarker.position.longitude}")
            carMarker.rotation(
                calculateBearing(
                    carMarker.position.latitude,
                    carMarker.position.longitude,
                    destinationMarker.position.latitude,
                    destinationMarker.position.longitude
                )
            )
            googleMap.addMarker(carMarker)
            googleMap.addMarker(destinationMarker)
            if (path.size > 0) {
                val opts = PolylineOptions().addAll(path)
                    .color(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.txtColor
                        )
                    ).width(8f)
                googleMap.addPolyline(opts)
            }

            googleMap.uiSettings.isZoomControlsEnabled = true

            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    path[0],
                    10f
                ), 6000, null
            )
        }
        /*val call =
            Retrofit.Builder().baseUrl("https://api.mapbox.com/directions/v5/mapbox/driving/")
                .addConverterFactory(
                    GsonConverterFactory.create()
                ).client(
                    OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().apply {
                            setLevel(
                                HttpLoggingInterceptor.Level.BODY
                            )
                        })
                        .build()
                ).build().create(GetDataService::class.java).getRouteData()
        call.enqueue(object : Callback<RoutesData> {
            override fun onResponse(call: Call<RoutesData>, response: Response<RoutesData>) {
                response.body()?.routes?.get(0)?.geometry?.coordinates?.forEach {
                    path.add(LatLng(it[1], it[0]))
                }
                val destinationMarker=MarkerOptions().position(LatLng(21.2113407,72.8449332)).icon(getBitmapDescriptorFromVector(requireActivity(),R.drawable.ic_marker_green))
                destinationMarker.title("${destinationMarker.position.latitude},${destinationMarker.position.longitude}")
                val carMarker=MarkerOptions().position(LatLng(21.2402793,72.875444)).icon(getBitmapDescriptorFromVector(requireActivity(),R.drawable.ic_car_green_icon)).flat(true)
                carMarker.title("${carMarker.position.latitude},${carMarker.position.longitude}")
                carMarker.rotation(calculateBearing(carMarker.position.latitude,carMarker.position.longitude,destinationMarker.position.latitude,destinationMarker.position.longitude))
                googleMap.addMarker(carMarker)
                googleMap.addMarker(destinationMarker)
                if (path.size > 0) {
                    val opts = PolylineOptions().addAll(path)
                        .color(ContextCompat.getColor(requireContext(), R.color.txtColor)).width(8f)
                    googleMap.addPolyline(opts)
                }

                googleMap.uiSettings.isZoomControlsEnabled = true

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(path[0], 10f),6000,null)
            }

            override fun onFailure(call: Call<RoutesData>, t: Throwable) {
                Log.e("error", "${t.localizedMessage}")
            }

        })*/
    }

    fun getBitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor? {

        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun calculateBearing(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val sourceLatLng = LatLng(lat1, lng1)
        val destinationLatLng = LatLng(lat2, lng2)
        return SphericalUtil.computeHeading(sourceLatLng, destinationLatLng).toFloat()
    }

    override fun onBookingSuccess(show: Boolean, radius: Float) {

    }

    private fun getMarkerBitmapFromView(url: String): Bitmap? {
        val customMarkerView: View? =
            (context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)?.inflate(
                R.layout.fair_user_item,
                null
            )
        val markerImageView: ImageView =
            customMarkerView?.findViewById(R.id.profile) as ImageView

        Glide.with(requireContext()).load(url).placeholder(R.drawable.ic_user_avatar)
            .into(markerImageView)
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(
            0,
            0,
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight
        )
        customMarkerView.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.measuredWidth, customMarkerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.background
        drawable?.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    data class RoutesData(@SerializedName("routes") var routes: ArrayList<Routes> = arrayListOf())
    data class Routes(@SerializedName("geometry") val geometry: Geometrics)
    data class Geometrics(@SerializedName("coordinates") val coordinates: List<List<Double>>)

    override fun onFilterData(user_let: Double, user_long: Double, miles: Int) {
        viewModel.getFairUserList(miles, user_let, user_long)
    }
}

