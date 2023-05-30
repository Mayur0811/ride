package com.bayride.presentation.features.driverProfile

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.utils.Constants
import com.bayride.common.utils.getVehicleType
import com.bayride.common.views.makeCall
import com.bayride.common.views.showAlertDialog
import com.bayride.databinding.DriverProfileScreenBinding
import com.bayride.presentation.base.BaseFragment
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DriverProfileScreen : BaseFragment<DriverProfileScreenBinding>() {
    private val viewModel by viewModels<DriverProfileViewModel>()

    override fun initData(data: Bundle?) {
        val args = data?.let { DriverProfileScreenArgs.fromBundle(data) } ?: run {
            findNavController().popBackStack()
            return
        }

        viewModel.loadData(args)
    }

    override fun initViews() {
        viewModel.gertDriverProfile()
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))
    }

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }

        safetyClick.setViewClickSafetyListener(binding.btnCall) {
            makeCall(binding.txtMobileNo.text.toString(), requireContext())
        }
    }

    override fun initObservers() {
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it == true) showLoading() else hideLoading()
        }
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.profileEntity }
        ) {
            if (it?.info?.driver_info?.get(0) != null) {
                binding.apply {
                    val driverProfile = Constants.imageDomain + it.info.driver_info[0].user_profile_pic
                    Glide.with(requireActivity()).load(driverProfile)
                        .placeholder(R.drawable.ic_user_avatar)
                        .into(driverProfileImage)
                    txtDriverName.text = it.info.driver_info[0].user_name
                    txtMobileNo.text = it.info.driver_info[0].user_phone_number
                    txtCarName.text =
                        it.info.driver_info[0].vehicle_brand + " " + getVehicleType(it.info.driver_info[0].vehicle_type_id)
                    txtCarNumber.text = it.info.driver_info[0].vehicle_number
                    txtCarYear.text = it.info.driver_info[0].vehicle_year
                    txtLicenceNumber.text = it.info.driver_info[0].vehicle_license_number
                    txtCarModel.text = it.info.driver_info[0].vehicle_model
                    txtCarColor.text = it.info.driver_info[0].vehicle_colour
                    txtAcceptsCryptocurrency.text = if (it.info.driver_info[0].is_crypto == 0) "No" else "Yes"
                    txtAcceptsPets.text = if (it.info.driver_info[0].is_accept_pets == 0) "No" else "Yes"
                    txtTotalReview.text = "(${it.info.driver_info[0].total_review})"
                    if (it.info.driver_info[0].real_star != null) {
                        libraryTintedWideRatingbar.rating =
                            it.info.driver_info[0].real_star?.toFloat() ?: 0f
                    }
                }
            }
        }
        viewModel.liveEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is DriverProfileSuccessEvent -> {
                    if (event.profileEntity.Status == 0) {
                        event.profileEntity.Message?.let {
                            context?.showAlertDialog(
                                title = getString(R.string.Error),
                                message = it,
                                button = getString(R.string.btn_ok)
                            )
                        }
                    }

                }
                is DriverProfileFailEvent -> {
                    event.error.message?.let {
                        context?.showAlertDialog(
                            title = getString(R.string.failed),
                            message = it,
                            button = getString(R.string.btn_ok)
                        )
                    }
                }
                is DriverProfileErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.errorCode.toString() + " " + event.errorMessage,
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