package com.bayride.presentation.features.forgotPassword

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.views.showAlertDialog
import com.bayride.data.models.exceptions.AuthenticationException
import com.bayride.data.models.exceptions.EmptyEmailException
import com.bayride.data.models.exceptions.EmptyPasswordException
import com.bayride.databinding.FragmentForgotPasswordBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordScreen : BaseFragment<FragmentForgotPasswordBinding>() {

    val viewModel: ForgotPasswordViewModel by viewModels()


    override fun initData(data: Bundle?) {

    }

    override fun initViews() {
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.lightGreen))
    }

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.btnSend) {
            viewModel.forgotPassword(binding.email.text.toString().trim())
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
                is ForgotPasswordSuccessEvent -> {
                    if (event.responsePassword.Status == 1) {
                        requireContext().showAlertDialog(
                            title = getString(R.string.Success),
                            message = event.responsePassword.Message.toString(),
                            button = getString(R.string.btn_ok),
                            clickListener = {
                                findNavController().navigate(
                                    ForgotPasswordScreenDirections.actionForgotPasswordScreenToResetPasswordScreen(
                                        binding.email.text.toString()
                                    )
                                )
                            }
                        )
                    } else {
                        requireContext().showAlertDialog(
                            title = getString(R.string.failed),
                            message = event.responsePassword.Message.toString(),
                            button = getString(R.string.btn_ok),
                            clickListener = {

                            }
                        )
                    }

                }
                is ForgotPasswordFailEvent -> {
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
                is ForgotPasswordErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.failed),
                        message = getString(event.error),
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