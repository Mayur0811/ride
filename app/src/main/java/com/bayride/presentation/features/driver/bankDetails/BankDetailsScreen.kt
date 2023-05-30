package com.bayride.presentation.features.driver.bankDetails

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.presentation.homeActivity.HomeActivity
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.sharedpreference.saveDriverObjectToSharedPreference
import com.bayride.common.sharedpreference.saveModelObjectToSharedPreference
import com.bayride.common.utils.Constants
import com.bayride.common.views.getDriver
import com.bayride.common.views.hideKeyBoard
import com.bayride.common.views.showAlertDialog
import com.bayride.data.models.dto.BankDetails
import com.bayride.data.models.dto.Driver
import com.bayride.databinding.BankDetailsScreenBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BankDetailsScreen : BaseFragment<BankDetailsScreenBinding>() {
    val viewModel: BankDetailsViewModel by viewModels()

    override fun initData(data: Bundle?) {

    }

    override fun initViews() {
        binding.edAccountName.setText(getDriver(requireContext())?.bankDetails?.account_name)
        binding.edAccountNumber.setText(getDriver(requireContext())?.bankDetails?.account_number)
        binding.edIfscCode.setText(getDriver(requireContext())?.bankDetails?.ifsc_code)
        binding.edbankName.setText(getDriver(requireContext())?.bankDetails?.bankName)
        binding.edBranch.setText(getDriver(requireContext())?.bankDetails?.branch)

        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }

    }

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.btnSave) {
            it.hideKeyBoard()
            if (binding.edAccountName.text.toString()
                    .isEmpty() || binding.edAccountNumber.text.toString()
                    .isEmpty() || binding.edIfscCode.text.toString()
                    .isEmpty() || binding.edbankName.text.toString()
                    .isEmpty() || binding.edBranch.text.toString().isEmpty()
            ) {
                context?.showAlertDialog(
                    title = getString(R.string.Error),
                    message = getString(R.string.enter_required_field),
                    button = getString(R.string.btn_ok)
                )
                return@setViewClickSafetyListener
            }

            val bankDetails = BankDetails(
                binding.edAccountName.text.toString(),
                binding.edAccountNumber.text.toString(),
                binding.edIfscCode.text.toString(),
                binding.edbankName.text.toString(),
                binding.edBranch.text.toString()
            )
            getEncryptedSharedPreferences(requireContext())?.edit()?.putBoolean("isSignup", true)
                ?.apply()
            saveDriverInformation(Driver(bankDetails = bankDetails))
            saveBankDetails(bankDetails)
            saveDriverObjectToSharedPreference(
                requireContext(), Constants.BAYRIDE_DRIVER_MODEL,
                Constants.DRIVER,
                Driver(),
                false
            )
            viewModel.addBankDetails(
                binding.edAccountNumber.text.toString(),
                binding.edAccountName.text.toString(),
                binding.edIfscCode.text.toString(),
                binding.edbankName.text.toString(),
                binding.edBranch.text.toString()
            )


//            val phoneNumber =
//                getDriver(requireContext())?.contactInformation?.phone_number.toString()
//            val userName = getDriver(requireContext())?.contactInformation?.username.toString()
//            val userFirstName = getDriver(requireContext())?.contactInformation?.name.toString()
//            val userPassword = getDriver(requireContext())?.contactInformation?.password.toString()
//            val userProfile = FileUtils.getRealPath(
//                requireContext(),
//                Uri.parse(getDriver(requireContext())?.contactInformation?.profile_pic.toString())
//            )?.let { it1 ->
//                File(
//                    it1
//                )
//            }
//            val userAddress = getDriver(requireContext())?.contactInformation?.address
//            val acceptPets = getDriver(requireContext())?.accepts_pets
//            val acceptCrypto = getDriver(requireContext())?.accepts_crypto

        }

    }

    override fun initObservers() {
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it == true) showLoading() else hideLoading()
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                is BankDetailsSuccessEvent -> {
                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                }
                is BankDetailsErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
                is BankDetailsFailedEvent -> {
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

    private fun saveDriverInformation(driver: Driver) {
        saveDriverObjectToSharedPreference(
            requireContext(), Constants.BAYRIDE_DRIVER_MODEL,
            Constants.DRIVER,
            driver,
            true
        )
    }

    private fun saveBankDetails(bankDetails: BankDetails) {
        saveModelObjectToSharedPreference(
            requireContext(),
            Constants.DRIVER_BANK_DETAILS,
            Constants.BANK_DETAILS,
            bankDetails
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }
}