package com.bayride.presentation.features.driver.acceptCrypto

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.views.getDriver
import com.bayride.common.views.showAlertDialog
import com.bayride.databinding.AcceptCryptoScreenBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AcceptCryptoScreen : BaseFragment<AcceptCryptoScreenBinding>() {

    val viewModel: AcceptCryptoViewModel by viewModels()

    companion object {
        const val ACCEPT_CRYPTO = "ACCEPT_CRYPTO"
    }

    private var acceptFlag = 1

    override fun initData(data: Bundle?) {

    }

    override fun initViews() {
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))
        when (getDriver(requireContext())?.accepts_crypto) {
            1 -> {
                binding.btnYes.isChecked = true
            }
            0 -> {
                binding.btnNo.isChecked = true
            }
            else -> {
                binding.btnYes.isChecked = true
            }
        }
        binding.yesLayout.setOnClickListener {
            binding.btnYes.isChecked = true
            binding.btnNo.isChecked = false
            acceptFlag = 1
        }
        binding.noLayout.setOnClickListener {
            binding.btnNo.isChecked = true
            binding.btnYes.isChecked = false
            acceptFlag = 0

        }
        binding.btnYes.setOnCheckedChangeListener { _, p1 ->
            if (p1) {
                acceptFlag = 1
                binding.btnNo.isChecked = false
            }
        }
        binding.btnNo.setOnCheckedChangeListener { _, p1 ->
            if (p1) {
                acceptFlag = 0
                binding.btnYes.isChecked = false
            }
        }
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }

    }

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.btnSave) {
            viewModel.signUpEdit(is_crypto = acceptFlag)
        }

    }

    override fun initObservers() {
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it) showLoading() else hideLoading()
        }
        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                AcceptCryptoSuccessEvent -> {
                    findNavController().apply {
                        previousBackStackEntry?.savedStateHandle?.set(
                            ACCEPT_CRYPTO,
                            acceptFlag
                        )
                        popBackStack()
                    }
                }
                is AcceptCryptoErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.Message,
                        button = getString(R.string.btn_ok)
                    )
                }
                is AcceptCryptoFailedEvent -> {
                    requireContext().showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
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