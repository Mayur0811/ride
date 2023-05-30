package com.bayride.presentation.features.driver.acceptPets

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.sharedpreference.saveModelObjectToSharedPreference
import com.bayride.common.utils.Constants
import com.bayride.common.views.showAlertDialog
import com.bayride.databinding.AcceptPetsScreenBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AcceptPetsScreen : BaseFragment<AcceptPetsScreenBinding>() {

    val viewModel by viewModels<AcceptPetsViewModel>()

    companion object {
        const val ACCEPT_PETS = "ACCEPT_PETS"
    }

    private var acceptFlag = 1

    override fun initData(data: Bundle?) {
    }

    override fun initViews() {
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))
        binding.yesLayout.setOnClickListener {
            binding.btnYes.isChecked = true
            binding.btnNo.isChecked = false
            acceptFlag = 1
        }
        binding.noLayout.setOnClickListener {
            binding.btnYes.isChecked = false
            binding.btnNo.isChecked = true
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
            viewModel.signUpEdit(is_accept_pets = acceptFlag)
        }
    }

    override fun initObservers() {
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }) {
            if (it) showLoading() else hideLoading()
        }
        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                is AcceptPetsSuccessEvent -> {
                    saveModelObjectToSharedPreference(
                        requireContext(),
                        Constants.LOGIN_DETAILS,
                        Constants.LOGIN,
                        event.sigUpResponse
                    )
                    getEncryptedSharedPreferences(requireContext())?.edit()
                        ?.putBoolean("isSignup", false)?.apply()
                    event.sigUpResponse.info?.user_role?.let {
                        getEncryptedSharedPreferences(requireContext())?.edit()?.putInt(
                            "type",
                            it
                        )?.apply()
                    }
                    findNavController().apply {
                        previousBackStackEntry?.savedStateHandle?.set(
                            ACCEPT_PETS,
                            acceptFlag
                        )
                        popBackStack()
                    }
                }
                is AcceptPetsErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.Message,
                        button = getString(R.string.btn_ok)
                    )
                }
                is AcceptPetsFailedEvent -> {
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