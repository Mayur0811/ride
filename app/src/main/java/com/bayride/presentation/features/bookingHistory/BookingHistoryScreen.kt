package com.bayride.presentation.features.bookingHistory

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.databinding.FragmentBookingHistoryBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingHistoryScreen : BaseFragment<FragmentBookingHistoryBinding>() {

    private val viewModel by viewModels<BookingHistoryViewModel>()
    lateinit var adapter: BookingHistoryAdapter

    override fun initData(data: Bundle?) {

    }

    override fun initViews() {
        adapter = BookingHistoryAdapter(requireActivity())
        viewModel.getBookingHistoryUser()
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
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
                binding.bookingHistoryRecycleView.adapter = adapter
                adapter.updateData(it.info)
            }
        }
        viewModel.liveEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is BookingHistorySuccessEvent -> {
                    if (event.bookingHistoryEntity.Status == 0) {
                        context?.showAlertDialog(
                            title = getString(R.string.Error),
                            message = event.bookingHistoryEntity.Message,
                            button = getString(R.string.btn_ok)
                        )
                    }
                }
                is BookingHistoryErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.failed),
                        message = event.errorCode.toString() + " " + event.errorMessage,
                        button = getString(R.string.btn_ok)
                    )
                }
                is BookingHistoryFailEvent -> {
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