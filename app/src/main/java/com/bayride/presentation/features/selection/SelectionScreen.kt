package com.bayride.presentation.features.selection

import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.utils.AsteriskPasswordTransformationMethod
import com.bayride.common.utils.Constants
import com.bayride.common.utils.FileUtils
import com.bayride.common.views.*
import com.bayride.data.datasources.remote.entities.SelectionScreenInputType.*
import com.bayride.data.models.exceptions.AuthenticationException
import com.bayride.data.models.exceptions.EmptyEmailException
import com.bayride.data.models.exceptions.InvalidEmailException
import com.bayride.databinding.FragmentSelectionScreenBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.countrycodedialog.CountryCodeDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class SelectionScreen : BaseFragment<FragmentSelectionScreenBinding>() {
    private val viewModel: SelectionViewModel by viewModels()

    companion object {
        const val SELECTION_OPTION_KEY = "SELECTION_OPTION_KEY"
    }

    override fun initData(data: Bundle?) {
        val args = data?.let { SelectionScreenArgs.fromBundle(data) } ?: run {
            findNavController().popBackStack()
            return
        }
        viewModel.loadData(args, requireContext())
    }

    override fun initViews() {
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        binding.countryPicker.setFlagSize(60)
//        binding.countryPicker.onFocusChangeListener =
//            View.OnFocusChangeListener { p0, p1 ->  false }
        safetyClick.setViewClickSafetyListener(binding.showConfirmPassword)
        {
            binding.edConfirmPassword.transformationMethod =
                if (binding.edConfirmPassword.transformationMethod is AsteriskPasswordTransformationMethod) {
                    binding.showConfirmPassword.text = "Hide"
                    null
                } else {
                    binding.showConfirmPassword.text = "View"
                    AsteriskPasswordTransformationMethod()
                }
            binding.edConfirmPassword.setSelection(binding.edConfirmPassword.text.length)
        }
        safetyClick.setViewClickSafetyListener(binding.showConfirmPassword1)
        {
            binding.editBox.transformationMethod =
                if (binding.editBox.transformationMethod is AsteriskPasswordTransformationMethod) {
                    binding.showConfirmPassword1.text = "Hide"
                    null
                } else {
                    binding.showConfirmPassword1.text = "View"
                    AsteriskPasswordTransformationMethod()
                }
            binding.editBox.setSelection(binding.editBox.text.length)
        }
        when (viewModel.currentState.name) {
            Constants.NAME -> {
                binding.editBox.setText(getPassenger(requireContext())?.name)
            }
            Constants.CREATE_USERNAME -> {
                binding.editBox.setText(getPassenger(requireContext())?.username)

            }
            Constants.EMAIL -> {
                binding.editBox.setText(getPassenger(requireContext())?.email)

            }
            Constants.PHONE_NUMBER -> {
                binding.codePicker.visible(true)
                (getPassenger(requireContext())?.country_code)?.toInt()
                    ?.let { binding.countryPicker.setCountryForPhoneCode(it) }
                binding.editBox.setText(getPassenger(requireContext())?.phoneNumber)

            }
            Constants.ADDRESS -> {
                binding.editBox.setText(getPassenger(requireContext())?.address)

            }
            Constants.CREATE_PASSWORD -> {
                binding.editBox.setText(getPassenger(requireContext())?.createPassword?.first)
                binding.edConfirmPassword.setText(getPassenger(requireContext())?.createPassword?.first)
            }
        }
    }

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }
        // binding.flagEmoji.setImageResource(getFlag("in"))
        safetyClick.setViewClickSafetyListener(binding.codePicker) {
            val dialogPopUp =
                CountryCodeDialog()
            activity?.supportFragmentManager.let {
                if (it != null) {
                    //    dialogPopUp.show(it, "")
                }
            }

            dialogPopUp.setPickUpDialogListener(object : Constants.OnCountryCodeListener {
                override fun countryCode(code: String, flag: String) {
                    // binding.flagEmoji.setImageResource(getFlag(flag))
                }

            })


        }
        safetyClick.setViewClickSafetyListener(binding.btnSave) {
            val filedBox = binding.editBox.text.toString()
            it.hideKeyBoard()
            val confirmPassword = binding.edConfirmPassword.text.toString()
            if (filedBox.isEmpty()) {
                requireContext().showAlertDialog(
                    title = getString(R.string.Error),
                    message = getString(R.string.enter_required_field),
                    button = getString(R.string.btn_ok)
                )
                return@setViewClickSafetyListener
            }
            when (viewModel.currentState.name) {
                getString(R.string.passenger_Name) -> {
                    viewModel.signUpEdit(
                        user_first_name = binding.editBox.text.toString()
                    )
                }
                getString(R.string.passenger_create_username) -> {
                    viewModel.signUpEdit(
                        user_name = binding.editBox.text.toString()
                    )
                }
                resources.getString(R.string.passenger_create_password) -> {
                    isAlphaNumeric(filedBox).let { it1 ->
                        if (!it1.first) {
                            context?.showAlertDialog(
                                title = getString(R.string.Error),
                                message = it1.second,
                                button = getString(R.string.btn_ok)
                            )
                            return@setViewClickSafetyListener
                        }
                    }

                    if (filedBox != confirmPassword) {
                        Toast.makeText(context, "password not match", Toast.LENGTH_SHORT).show()
                        return@setViewClickSafetyListener
                    }

                    viewModel.signUpEdit(
                        user_password = binding.editBox.text.toString()
                    )
                }
                getString(R.string.passenger_phone_number) -> {
                    if (!isValidPhoneNumber(binding.editBox.text.toString())) {
                        context?.showAlertDialog(
                            title = getString(R.string.Error),
                            message = "Please enter valid mobile number",
                            button = getString(R.string.btn_ok)
                        )
                        return@setViewClickSafetyListener
                    }


                    viewModel.signUpEdit(
                        user_phone_number = binding.editBox.text.toString(),
                        country_code = binding.countryPicker.selectedCountryCode
                    )

                }
                getString(R.string.passenger_address) -> {
                    if (binding.editBox.text.toString().isEmpty()) {
                        context?.showAlertDialog(
                            title = getString(R.string.Error),
                            message = "Please enter address",
                            button = getString(R.string.btn_ok)
                        )
                        return@setViewClickSafetyListener
                    }
                    viewModel.signUpEdit(
                        user_address = binding.editBox.text.toString()
                    )
                }
                resources.getString(R.string.passenger_email) -> {
                    viewModel.signUpEmail(1, filedBox.trim(), 1)
                }
                else -> {
                    if (filedBox.isNotEmpty()) {
                        findNavController().apply {
                            previousBackStackEntry?.savedStateHandle?.set(
                                SELECTION_OPTION_KEY,
                                Pair(
                                    viewModel.currentState.name,
                                    Pair(
                                        binding.countryPicker.selectedCountryCode,
                                        Pair(filedBox, confirmPassword)
                                    )
                                )
                            )
                            popBackStack()
                        }

                    }
                }
            }


        }
    }

    override fun initObservers() {

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.name }
        ) { title ->
            binding.title.text = title
            if (title == resources.getString(R.string.passenger_create_password)) {
                binding.txtConfirm.visible(true)
                binding.edConfirmPassword.visible(true)
                binding.showConfirmPassword1.visible(true)
                binding.showConfirmPassword.visible(true)
            } else {
                if (title == resources.getString(R.string.passenger_phone_number)) {
                    binding.editBox.filters += InputFilter.LengthFilter(10)
                }
            }
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.fieldTitle }
        ) { fieldTitle ->
            binding.txtEdname.text = fieldTitle

        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it == true) showLoading() else hideLoading()
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.hint }
        ) { hintText -> binding.editBox.hint = hintText }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.inputType }
        ) { inputType ->
            binding.editBox.inputType = when (inputType) {
                NUMBER -> {
                    InputType.TYPE_CLASS_NUMBER
                }
                TEXT -> {
                    InputType.TYPE_CLASS_TEXT
                }
                PASSWORD -> {
                    binding.edConfirmPassword.transformationMethod =
                        AsteriskPasswordTransformationMethod()
                    binding.editBox.transformationMethod = AsteriskPasswordTransformationMethod()
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                else -> {
                    InputType.TYPE_CLASS_TEXT
                }
            }
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                SelectionSuccessFullEvent -> {
                    findNavController().apply {
                        previousBackStackEntry?.savedStateHandle?.set(
                            SELECTION_OPTION_KEY,
                            Pair(
                                viewModel.currentState.name,
                                Pair(
                                    binding.countryPicker.selectedCountryCode,
                                    Pair(binding.editBox.text.toString(), null)
                                )
                            )
                        )
                        popBackStack()
                    }
                }
                is SelectionSuccessEvent -> {
                    if (event.signUpEmailResponse.Status == 1) {
                        findNavController().apply {
                            previousBackStackEntry?.savedStateHandle?.set(
                                SELECTION_OPTION_KEY,
                                Pair(
                                    viewModel.currentState.name,
                                    Pair(
                                        binding.countryPicker.selectedCountryCode,
                                        Pair(binding.editBox.text.toString(), null)
                                    )
                                )
                            )
                            popBackStack()
                        }
                    } else {
                        requireContext().showAlertDialog(
                            title = getString(R.string.Error),
                            message = event.signUpEmailResponse.Message.toString(),
                            button = getString(R.string.btn_ok)
                        )
                    }
                }
                is SelectionFailedEvent -> {
                    when (event.error as? AuthenticationException) {
                        is EmptyEmailException -> {
                            requireContext().showAlertDialog(
                                title = getString(R.string.Error),
                                message = getString(R.string.enter_required_field),
                                button = getString(R.string.btn_ok)
                            )
                        }
                        is InvalidEmailException -> {
                            requireContext().showAlertDialog(
                                title = getString(R.string.Error),
                                message = "Enter valid Email",
                                button = getString(R.string.btn_ok)
                            )
                        }
                        else -> {
                            requireContext().showAlertDialog(
                                title = getString(R.string.Error),
                                message = event.error.message.toString(),
                                button = getString(R.string.btn_ok)
                            )
                        }
                    }

                }
                is SelectionErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.Message,
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