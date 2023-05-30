package com.bayride.presentation.features.driver.insuranceInformation

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.utils.Constants
import com.bayride.common.views.*
import com.bayride.data.models.dto.InsuranceInformation
import com.bayride.databinding.InsuranceInformationScreenBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.countrycodedialog.CountryCodeDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsuranceInformationScreen : BaseFragment<InsuranceInformationScreenBinding>() {

    val viewModel: InsuranceInformationViewModel by viewModels()

    var number = 1

    companion object {
        const val INSURANCE_INFORMATION = "INSURANCE_INFORMATION"
    }

    override fun initData(data: Bundle?) {
    }

    override fun initViews() {
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))
        binding.edHolderName.setText(getInsuranceInformation()?.policy_holder_name)
        binding.edPolicyNumber.setText(getInsuranceInformation()?.policy_number)
        binding.edPhoneNumber.setText(getInsuranceInformation()?.phone_number)
        getInsuranceInformation()?.country_Code?.toInt()
            ?.let { binding.countryPicker.setCountryForPhoneCode(it) }
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }
    }

    override fun initActions() {
//        binding.flagEmoji.setImageResource(getFlag("in"))
        binding.countryPicker.setFlagSize(60)

        safetyClick.setViewClickSafetyListener(binding.codePicker) {
            val dialogPopUp =
                CountryCodeDialog()
            activity?.supportFragmentManager.let {
                if (it != null) {
                    dialogPopUp.show(it, "")
                }
            }

            dialogPopUp.setPickUpDialogListener(object : Constants.OnCountryCodeListener {
                override fun countryCode(code: String, flag: String) {
                    // binding.flagEmoji.setImageResource(getFlag(flag))
                }
            })


        }
        safetyClick.setViewClickSafetyListener(binding.btnSave) {
            it.hideKeyBoard()
            if (binding.edHolderName.text.toString()
                    .isEmpty() || binding.edPolicyNumber.text.toString()
                    .isEmpty() || binding.edPhoneNumber.text.toString().isEmpty()
            ) {
                context?.showAlertDialog(
                    title = getString(R.string.Error),
                    message = getString(R.string.enter_required_field),
                    button = getString(R.string.btn_ok)
                )
                return@setViewClickSafetyListener
            }

            if (!isValidPhoneNumber(binding.edPhoneNumber.text)) {
                context?.showAlertDialog(
                    title = getString(R.string.Error),
                    message = "Please enter valid mobile number",
                    button = getString(R.string.btn_ok)
                )
                return@setViewClickSafetyListener
            }

            viewModel.EditDriverOption(2, binding.edHolderName.text.toString())
            // startActivity(Intent(requireContext(), HomeActivity::class.java))

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
                InsurancesInformationSuccessEvent -> {
                    when (number) {
                        1 -> {
                            number = 2
                            viewModel.EditDriverOption(3, binding.edPolicyNumber.text.toString())
                        }
                        2 -> {
                            number = 3
                            viewModel.EditDriverOption(4, binding.edPhoneNumber.text.toString())
                        }
                        3 -> {
                            number = 4
                            viewModel.EditDriverOption(6, binding.countryPicker.selectedCountryCode)
                        }
                        else -> {
                            findNavController().apply {
                                previousBackStackEntry?.savedStateHandle?.set(
                                    INSURANCE_INFORMATION,
                                    Pair(
                                        INSURANCE_INFORMATION, InsuranceInformation(
                                            binding.edHolderName.text.toString(),
                                            binding.edPolicyNumber.text.toString(),
                                            binding.edPhoneNumber.text.toString(),
                                            binding.countryPicker.selectedCountryCode
                                        )
                                    )
                                )
                                navigateUp()
                            }
                        }
                    }


                }
                is InsurancesInformationFailedEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        button = getString(R.string.btn_ok)
                    )
                }
                is InsurancesInformationErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.Message,
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

    private fun getInsuranceInformation(): InsuranceInformation? {
        return getDriver(requireContext())?.insuranceInformation
    }
}