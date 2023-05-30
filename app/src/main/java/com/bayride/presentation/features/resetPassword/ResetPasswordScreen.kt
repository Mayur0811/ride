package com.bayride.presentation.features.resetPassword

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.utils.AsteriskPasswordTransformationMethod
import com.bayride.common.views.isAlphaNumeric
import com.bayride.common.views.showAlertDialog
import com.bayride.data.models.exceptions.AuthenticationException
import com.bayride.data.models.exceptions.EmptyEmailException
import com.bayride.data.models.exceptions.EmptyPasswordException
import com.bayride.data.models.exceptions.InvalidPasswordNotMatchException
import com.bayride.databinding.FragmentResetPasswordBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.forgotPassword.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordScreen : BaseFragment<FragmentResetPasswordBinding>() {

    val viewModel: ResetPasswordViewModel by viewModels()

    override fun initData(data: Bundle?) {
        val args = data?.let { ResetPasswordScreenArgs.fromBundle(data) } ?: run {
            findNavController().popBackStack()
            return
        }

        viewModel.loadData(args)

    }

    override fun initViews() {
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.lightGreen))
        binding.edNewPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.edConfirmPassword.transformationMethod = AsteriskPasswordTransformationMethod()
    }

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.btnSubmit)
        {
            val newPassword = binding.edNewPassword.text.toString()
            val confirmPassword = binding.edConfirmPassword.text.toString()

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                context?.showAlertDialog(
                    title = getString(R.string.Error),
                    message = getString(R.string.enter_required_field),
                    button = getString(R.string.btn_ok)
                )
                return@setViewClickSafetyListener
            }
            isAlphaNumeric(newPassword).let { it1 ->
                if (!it1.first) {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = it1.second,
                        button = getString(R.string.btn_ok)
                    )
                    return@setViewClickSafetyListener
                }
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(context, "password not match", Toast.LENGTH_SHORT).show()
                return@setViewClickSafetyListener
            }

            viewModel.resetPassword(
                binding.enterOtp.text.toString(),
                binding.edNewPassword.text.toString().trim(),
                binding.edConfirmPassword.text.toString().trim()
            )

        }
        safetyClick.setViewClickSafetyListener(binding.resendOtp) {
            viewModel.forgotPassword(viewModel.currentState.email.toString().trim())
        }
        safetyClick.setViewClickSafetyListener(binding.showNewPassword) {
            binding.edNewPassword.transformationMethod =
                if (binding.edNewPassword.transformationMethod is AsteriskPasswordTransformationMethod) {
                    binding.showNewPassword.text = "Hide"
                    null
                } else {
                    binding.showNewPassword.text = "View"
                    AsteriskPasswordTransformationMethod()
                }
            binding.edNewPassword.setSelection(binding.edNewPassword.text.length)
        }
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
                is ResetPasswordSuccessEvent -> {
                    if (event.responsePassword.Status == 1) {
                        requireContext().showAlertDialog(
                            title = getString(R.string.Success),
                            message = event.responsePassword.Message.toString(),
                            button = getString(R.string.btn_ok),
                            clickListener = {
                                findNavController().navigate(R.id.loginScreen)
                            }
                        )
                    } else {
                        requireContext().showAlertDialog(
                            title = getString(R.string.failed),
                            message = event.responsePassword.Message.toString(),
                            button = getString(R.string.btn_ok),
                        )
                    }
                }

                is ResetPasswordFailEvent -> {
                    when (event.error as? AuthenticationException) {
                        is EmptyEmailException -> {
                            requireContext().showAlertDialog(
                                title = getString(R.string.Error),
                                message = getString(R.string.enter_required_field),
                                button = getString(R.string.btn_ok)
                            )
                        }
                        is EmptyPasswordException -> {
                            context?.showAlertDialog(
                                title = getString(R.string.Error),
                                message = getString(R.string.enter_required_field),
                                button = getString(R.string.btn_ok)
                            )

                        }
                        is InvalidPasswordNotMatchException -> {
                            context?.showAlertDialog(
                                title = getString(R.string.failed),
                                message = getString(R.string.passwords_not_match),
                                button = getString(R.string.btn_ok),
                            )
                        }
                        else -> {
                            if (event.error.message == null) {
                                context?.showAlertDialog(
                                    title = getString(R.string.failed),
                                    message = getString(R.string.unknown_error),
                                    button = getString(R.string.btn_ok)
                                )
                            }
                        }
                    }
                }
                is ResetPasswordErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.failed),
                        message = getString(R.string.otp_verification_fail),
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