package com.bayride.presentation.features.creditCardPayment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.views.getLoginDetails
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.databinding.FragmentCreditCardPaymentScreenBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreditCardPaymentScreen : BaseFragment<FragmentCreditCardPaymentScreenBinding>() {
    val viewModel: CreditCardViewModel by viewModels()

    override fun initData(data: Bundle?) {

    }

    override fun initViews() {
        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.loading }) {
            if (it == true) showLoading() else hideLoading()
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                CreditCardSuccess -> {
                    findNavController().navigate(R.id.bookingSuccessfullyScreen)
                }
                is CreditCardErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.toString() + " " + event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
                is CreditCardFailedEvent -> {
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

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }
        safetyClick.setViewClickSafetyListener(binding.confirmPayment) {
            //viewModel.fairBooked(80, 2, 1)
        }
    }

    override fun initObservers() {
        binding.edCardNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                val space = ' '
                if (s.length > 0 && s.length % 5 == 0) {
                    val c = s[s.length - 1]
                    if (space == c) {
                        s.delete(s.length - 1, s.length)
                    }
                }
                if (s.length > 0 && s.length % 5 == 0) {
                    val c = s[s.length - 1]
                    if (Character.isDigit(c) && TextUtils.split(
                            s.toString(),
                            space.toString()
                        ).size <= 3
                    ) {
                        s.insert(s.length - 1, space.toString())
                    }
                }
                binding.cardNumber.text = s.toString()
            }
        })
        binding.edCardHolderName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! < 20) {
                    binding.cardHolderName.text = s.toString()
                }
            }

        })
        binding.edValidThru.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                val slash = '/'
                if (s.length > 0 && s.length % 3 == 0) {
                    val c = s[s.length - 1]
                    if (slash == c) {
                        s.delete(s.length - 1, s.length)
                    }
                }
                if (s.length > 0 && s.length % 3 == 0) {
                    val c = s[s.length - 1]
                    if (Character.isDigit(c) && TextUtils.split(
                            s.toString(),
                            slash.toString()
                        ).size <= 3
                    ) {
                        s.insert(s.length - 1, slash.toString())
                    }
                }
                binding.validThru.text = "Thru $s"
            }
        })
    }
}