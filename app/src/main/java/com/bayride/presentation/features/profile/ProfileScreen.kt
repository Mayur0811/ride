package com.bayride.presentation.features.profile

import android.os.Bundle
import android.view.Gravity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.sharedpreference.saveModelObjectToSharedPreference
import com.bayride.common.utils.Constants
import com.bayride.common.views.getDriverProFile
import com.bayride.common.views.getLoginDetails
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.databinding.FragmentProfileScreenBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileScreen : BaseFragment<FragmentProfileScreenBinding>() {
    val viewModel by viewModels<ProfileScreenViewModel>()

    override fun initData(data: Bundle?) {}

    override fun initViews() {}

    override fun initActions() {
        if (getEncryptedSharedPreferences(requireContext())?.getInt("type", 3) == 1) {
            binding.vehicleDetails.visible(false)
            binding.uploadDrivingLicense.visible(false)
        } else {
            binding.vehicleDetails.visible(true)
            binding.uploadDrivingLicense.visible(true)
            viewModel.gertDriverProfile(userId = getLoginDetails(requireContext())?.info?.user_id)
        }
        safetyClick.setViewClickSafetyListener(binding.appCompatImageView) {
            activity?.findViewById<DrawerLayout>(R.id.drawer_layout)?.openDrawer(Gravity.START)
        }
        safetyClick.setViewClickSafetyListener(binding.myInformationLayout) {
            findNavController().navigate(R.id.myInformationScreen)
        }
        safetyClick.setViewClickSafetyListener(binding.signatureLayout) {

            findNavController().navigate(R.id.signatureScreenProfile)
        }
        safetyClick.setViewClickSafetyListener(binding.addPaymentMethodLayout) {
            getEncryptedSharedPreferences(requireContext())?.edit()
                ?.putBoolean(Constants.ADD_PAYMENT_METHOD_SCREEN, false)?.apply()
            findNavController().navigate(R.id.addPaymentMethodScreenProfile)
        }
        safetyClick.setViewClickSafetyListener(binding.vehicleDetails) {
            findNavController().navigate(R.id.addVehicleDetailsScreenProfile)
        }
        safetyClick.setViewClickSafetyListener(binding.uploadDrivingLicense) {
            findNavController().navigate(
                ProfileScreenDirections.actionProfileScreenToPhotoUploadScreenProfile(
                    title = resources.getString(R.string.drive_upload_driving_licences),
                    subTitle = resources.getString(R.string.drive_upload_driving_licences)
                )
            )
        }
    }

    override fun initObservers() {
//        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.loading }) {
//            if (it == true) showLoading() else hideLoading()
//        }
        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.profileEntity }) {
            if (it != null) {
                saveModelObjectToSharedPreference(
                    requireContext(), Constants.BAYRIDE_DRIVER_MODEL,
                    Constants.PROFILE,
                    it
                )
            }
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                ProfileScreenSuccessFullyEvent -> {
                }
                is ProfileScreenErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.toString() + " " + event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
                is ProfileScreenFailedEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
            }
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }
}