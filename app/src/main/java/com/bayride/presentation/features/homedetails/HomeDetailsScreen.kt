package com.bayride.presentation.features.homedetails

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.utils.Constants.Companion.REMOVE_BLUR
import com.bayride.common.views.visible
import com.bayride.databinding.HomeDetailsScreenBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.homedetails.driverRequest.DriverRequestBottomSheet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderScriptBlur
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class HomeDetailsScreen : BaseFragment<HomeDetailsScreenBinding>(), OnMapReadyCallback,
    DriverRequestBottomSheet.BottomSheetListener {
    private var googleMap: GoogleMap? = null
    var file: File? = null

    override fun initData(data: Bundle?) {
    }

    override fun initViews() {
        // findNavController().navigate(R.id.bottomSheetFragment)
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))

        file = File(
            requireContext().filesDir.toString() + File.separator,
            "map.png"
        )
        if (file?.exists() == true) {
            binding.blurImageView.visibility = View.VISIBLE
            binding.blurImageView.setImageURI(Uri.parse(file?.absolutePath))
        }
        //  activity?.findViewById<RelativeLayout>(R.id.relativeLayout)?.visible(true)
        val supportMapFragment = binding.map.getFragment<SupportMapFragment>()
        supportMapFragment.getMapAsync(this)

        val decorView: View? = requireActivity().window?.decorView
        val rootView = decorView?.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        binding.blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(requireActivity()))
            .setBlurRadius(5F)
            .setBlurAutoUpdate(true)
        val driverRequestBottomSheet = DriverRequestBottomSheet()
        driverRequestBottomSheet.setBottomSheetListener(this)

    }

    override fun initActions() {
        val bottomSheet = DriverRequestBottomSheet()
        activity?.let {
            bottomSheet.show(
                requireFragmentManager(),
                DriverRequestBottomSheet.TAG
            )
        }
//        bottomSheet.isCancelable = false
        bottomSheet.setBottomSheetListener(this)

        safetyClick.setViewClickSafetyListener(binding.appCompatImageView) {
            activity?.findViewById<DrawerLayout>(R.id.drawer_layout)?.openDrawer(Gravity.START)
        }

    }

    override fun initObservers() {
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<Boolean>(REMOVE_BLUR)
                .observe(viewLifecycleOwner) {
                    binding.blurView.isVisible = false
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            remove<Boolean>(REMOVE_BLUR)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        activity?.finish()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val sydney = LatLng(-26.888033, 75.802754)
        googleMap?.addMarker(MarkerOptions().position(sydney).title("Kailash Tower"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        googleMap?.setOnMapLoadedCallback { snapShot() }
        googleMap?.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener {
            val bottomSheet = DriverRequestBottomSheet()
            activity?.let {
                bottomSheet.show(
                    requireFragmentManager(),
                    DriverRequestBottomSheet.TAG
                )
            }
//            bottomSheet.isCancelable = false
            bottomSheet.setBottomSheetListener(this)
            return@OnMarkerClickListener true
        })
    }

    private fun snapShot() {
        val callback: GoogleMap.SnapshotReadyCallback =
            object : GoogleMap.SnapshotReadyCallback {
                var bitmap: Bitmap? = null
                override fun onSnapshotReady(snapshot: Bitmap?) {
                    bitmap = snapshot
                    try {
                        val fout = FileOutputStream(file)
                        bitmap!!.compress(Bitmap.CompressFormat.PNG, 90, fout)
                        binding.blurImageView.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        googleMap?.snapshot(callback)


    }

    override fun onBottomSheetShow(show: Boolean, radius: Float) {
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), android.R.color.white))
//        if(show){
//            binding.blurView.isVisible = true
//            binding.blurImageView.isVisible = true
//
//        }

        try {
            binding.blurImageView.visible(show)
            binding.blurView.visible(show)
            binding.blurView.setBlurRadius(radius)

        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }
}