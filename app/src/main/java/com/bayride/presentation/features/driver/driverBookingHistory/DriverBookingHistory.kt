package com.bayride.presentation.features.driver.driverBookingHistory

import android.os.Bundle
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.utils.getCompletedData
import com.bayride.common.views.showAlertDialog
import com.bayride.databinding.DriverBookingHistoryBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.driver.driverBookingHistory.adapter.OnCompleteAdapter
import com.bayride.presentation.features.driver.driverBookingHistory.adapter.OnGoingAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverBookingHistory : BaseFragment<DriverBookingHistoryBinding>() {
    private lateinit var onGoingAdapter: OnGoingAdapter
    private var completeAdapter: OnCompleteAdapter = OnCompleteAdapter()
    private val viewModel by viewModels<DriverBookingHistoryViewModel>()

    override fun initData(data: Bundle?) {
    }

    override fun initViews() {
        viewModel.getBookingHistoryDriver()
        onGoingAdapter = OnGoingAdapter(requireActivity())

        onGoingAdapter.onGoingItemClick = { view, bookingInfo ->
//            findNavController().navigate(
//                DriverBookingHistoryDirections.actionDriverBookingHistoryToDriverOnGoingScreen(
//                    driverBookingInfo = bookingInfo,
//                    driverOnGoingFairDetails = null
//                )
//            )
        }
        binding.toggle.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.btnOngoing.id -> {
                    binding.btnOngoing.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.white
                        )
                    )
                    binding.btnComplete.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.txtColor
                        )
                    )
                    binding.btnOngoing.background = context?.getDrawable(R.drawable.selected_tab)
                    binding.btnComplete.background = null
                    binding.rvHistory.adapter = onGoingAdapter
                }
                binding.btnComplete.id -> {
                    binding.btnComplete.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.white
                        )
                    )
                    binding.btnOngoing.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.txtColor
                        )
                    )
                    binding.btnComplete.background = context?.getDrawable(R.drawable.selected_tab)
                    binding.btnOngoing.background = null
                    context?.let { getCompletedData(it) }
                        ?.let { completeAdapter.setCompleteList(it) }
                    binding.rvHistory.adapter = completeAdapter

//                    context?.let { getListDrive(it) }?.let { adapter.setDetailsFillList(null, it) }
//                    binding.detailsList.adapter = adapter
                }
            }
        }

        safetyClick.setViewClickSafetyListener(binding.appCompatImageView) {
            activity?.findViewById<DrawerLayout>(R.id.drawer_layout)?.openDrawer(Gravity.START)

        }

    }

    override fun initActions() {
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
            selector = { it.bookingHistoryEntity }
        ) {
            if (it?.info != null) {
                binding.rvHistory.adapter = onGoingAdapter
                onGoingAdapter.setOnGoingList(it.info)
            }
        }

        viewModel.liveEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is DriverBookingHistorySuccessEvent -> {
                    if (event.bookingHistoryEntity.Status == 0) {
                        context?.showAlertDialog(
                            title = getString(R.string.Error),
                            message = event.bookingHistoryEntity.Message,
                            button = getString(R.string.btn_ok)
                        )
                    }
                }
                is DriverBookingHistoryErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.failed),
                        message = event.errorCode.toString() + " " + event.errorMessage,
                        button = getString(R.string.btn_ok)
                    )
                }
                is DriverBookingHistoryFailEvent -> {
                    event.error.message?.let {
                        context?.showAlertDialog(
                            title = getString(R.string.Error),
                            message = it,
                            button = getString(R.string.btn_ok)
                        )
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }
}