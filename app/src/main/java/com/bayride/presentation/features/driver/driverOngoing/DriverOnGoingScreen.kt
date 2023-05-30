package com.bayride.presentation.features.driver.driverOngoing

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.databinding.DriverOngoingScreenBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.driver.onGoingScreendialog.OnGoingBottomSheet
import com.bayride.presentation.features.homedetails.driverRequest.DriverRequestBottomSheet
import com.bayride.presentation.features.selection.SelectionScreenArgs
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class DriverOnGoingScreen : BaseFragment<DriverOngoingScreenBinding>() {
    var file: File? = null

    val viewModel: DriverOnGoingViewModel by viewModels()
    override fun initData(data: Bundle?) {
        val args = data?.let { DriverOnGoingScreenArgs.fromBundle(data) } ?: run {
            findNavController().popBackStack()
            return
        }
        viewModel.loadData(args)
    }

    override fun initViews() {
        val bookingHistoryInfo = viewModel.currentState.bookingHistoryInfo
        val driverOngoingFairDetails = viewModel.currentState.driverOngoingFairDetails
        val bottomSheet =  OnGoingBottomSheet(bookingHistoryInfo,driverOngoingFairDetails)
        activity?.let {
            bottomSheet?.show(
                requireFragmentManager(),
                DriverRequestBottomSheet.TAG
            )
        }
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }
    }

    override fun initActions() {

    }

    override fun initObservers() {

    }


}

