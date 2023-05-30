package com.bayride.presentation.features.changePassword

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
import com.bayride.common.utils.AsteriskPasswordTransformationMethod
import com.bayride.common.views.isAlphaNumeric
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.data.models.exceptions.*
import com.bayride.databinding.FragmentChangePasswordBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.signup.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordScreen : BaseFragment<FragmentChangePasswordBinding>() {
    val viewModel: ChangePasswordViewModel by viewModels()


    override fun initData(data: Bundle?) {

    }

    override fun initViews() {
        binding.edCurrentPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.edNewPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.edConfirmPassword.transformationMethod = AsteriskPasswordTransformationMethod()
    }

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.appCompatImageView) {
            activity?.findViewById<DrawerLayout>(R.id.drawer_layout)?.openDrawer(Gravity.START)
        }
        safetyClick.setViewClickSafetyListener(binding.showCurrentPassword) {
            binding.edCurrentPassword.transformationMethod =
                if (binding.edCurrentPassword.transformationMethod is AsteriskPasswordTransformationMethod) {
                    binding.showCurrentPassword.text = "Hide"
                    null
                } else {
                    binding.showCurrentPassword.text = "View"
                    AsteriskPasswordTransformationMethod()
                }
            binding.edCurrentPassword.text?.let { it1 -> binding.edCurrentPassword.setSelection(it1.length) }
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
            binding.edNewPassword.text?.let { it1 -> binding.edNewPassword.setSelection(it1.length) }
        }
        safetyClick.setViewClickSafetyListener(binding.showConfirmPassword) {
            binding.edConfirmPassword.transformationMethod =
                if (binding.edConfirmPassword.transformationMethod is AsteriskPasswordTransformationMethod) {
                    binding.showConfirmPassword.text = "Hide"
                    null
                } else {
                    binding.showConfirmPassword.text = "View"
                    AsteriskPasswordTransformationMethod()
                }
            binding.edConfirmPassword.text?.length?.let { it1 ->
                binding.edConfirmPassword.setSelection(
                    it1
                )
            }
        }
        safetyClick.setViewClickSafetyListener(binding.btnSave) {
            val currentPassword = binding.edCurrentPassword.text.toString()
            val newPassword = binding.edNewPassword.text.toString()
            val confirmPassword = binding.edConfirmPassword.text.toString()

            if (newPassword.isEmpty() || confirmPassword.isEmpty() || currentPassword.isEmpty()) {
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
                Toast.makeText(context, "Password not match", Toast.LENGTH_SHORT).show()
                return@setViewClickSafetyListener
            }

            viewModel.changePassword(
                binding.edCurrentPassword.text.toString().trim(),
                binding.edNewPassword.text.toString().trim(),
                binding.edConfirmPassword.text.toString().trim()
            )
        }
    }

    override fun initObservers() {
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it == true) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                is ChangePasswordSuccessEvent -> {
                    (if (event.responsePassword.Status == 0) getString(R.string.Error) else getString(
                        R.string.Success
                    )).let {
                        context?.showAlertDialog(
                            title = it,
                            message = event.responsePassword.Message.toString(),
                            getString(R.string.btn_ok),
                            clickListener = {
                                if (event.responsePassword.Status == 0)
                                else
                                    findNavController().popBackStack()
                            }
                        )
                    }
                }
                is RequestFailedEvent -> {
                    handleError(event)
                }
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }

    private fun handleError(errorEvent: RequestFailedEvent) {
        when (errorEvent.error) {
            is InvalidCodeException -> {
                context?.showAlertDialog(
                    title = getString(R.string.failed),
                    message = getString(R.string.invalid_code),
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
            is InvalidPasswordException -> {
                context?.showAlertDialog(
                    title = getString(R.string.failed),
                    message = getString(R.string.please_enter_valid_password),
                    button = getString(R.string.btn_ok)
                )
            }
            is EmptyPasswordException -> {
                context?.showAlertDialog(
                    title = getString(R.string.failed),
                    message = getString(R.string.enter_required_field),
                    button = getString(R.string.btn_ok),
                )
            }
            else -> {
                context?.showAlertDialog(
                    title = getString(R.string.failed),
                    message = errorEvent.error.message ?: getString(R.string.unknown_error),
                    button = getString(R.string.btn_ok)
                )
            }
        }
    }

}