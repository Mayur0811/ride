package com.bayride.presentation.features.login

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.presentation.homeActivity.HomeActivity
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.sharedpreference.saveModelObjectToSharedPreference
import com.bayride.common.utils.AsteriskPasswordTransformationMethod
import com.bayride.common.utils.Constants
import com.bayride.common.views.hideKeyBoard
import com.bayride.common.views.showAlertDialog
import com.bayride.data.models.exceptions.*
import com.bayride.databinding.FragmentLoginScreenBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginScreen : BaseFragment<FragmentLoginScreenBinding>() {

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun initData(data: Bundle?) {
    }

    override fun initViews() {
        val spanText = SpannableString("You agree our Terms & Conditions")
        val clickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
                startActivity(i)
            }
        }
        spanText.setSpan(clickSpan.apply {
            updateDrawState(TextPaint(Color.parseColor("#00000000")))
        }, 14, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanText.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.txtColor)),
            14,
            32,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.termsAndConditions.movementMethod = LinkMovementMethod.getInstance()
        binding.termsAndConditions.text = spanText
        val spanRegisterText = SpannableString("Don't have an Account? Register")
        spanRegisterText.setSpan(UnderlineSpan(), 23, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanRegisterText.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.txtColor
                )
            ), 23, 31, 0
        )
        binding.btnRegister.text = spanRegisterText
        binding.edPassword.transformationMethod = AsteriskPasswordTransformationMethod()
    }

    override fun initActions() {
        binding.termsAndConditions.movementMethod = LinkMovementMethod.getInstance()
        safetyClick.setViewClickSafetyListener(binding.btnSignIn) {
            if (binding.edPassword.text.toString().trim().isEmpty()) {
                context?.showAlertDialog(
                    title = getString(R.string.Error),
                    message = getString(R.string.enter_required_field),
                    button = getString(R.string.btn_ok)
                )
                return@setViewClickSafetyListener
            }

            if (binding.termsAndConditionsCheckbox.isChecked) {
                //startActivity(Intent(requireActivity(), HomeActivity::class.java))
                loginViewModel.login(
                    binding.email.text.toString().trim(),
                    binding.edPassword.text.toString().trim(),
                    21.121212,
                    72.121212,
                    "asdfhfgjhfgjhgjd",
                    1,
                    "test1"
                )
            } else {
                Toast.makeText(
                    activity?.applicationContext,
                    "Please Accept Terms & Conditions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        safetyClick.setViewClickSafetyListener(binding.btnRegister)
        {
            findNavController().navigate(R.id.signUpScreen)

        }
        safetyClick.setViewClickSafetyListener(binding.forgotPassword)
        {
            findNavController().navigate(R.id.forgotPasswordScreen)
        }
        safetyClick.setViewClickSafetyListener(binding.termsAndConditionsLayout) {
            binding.termsAndConditionsCheckbox.isChecked =
                !binding.termsAndConditionsCheckbox.isChecked
        }
        safetyClick.setViewClickSafetyListener(binding.termsAndConditions) {
            binding.termsAndConditionsCheckbox.isChecked =
                !binding.termsAndConditionsCheckbox.isChecked
        }

        safetyClick.setViewClickSafetyListener(binding.showPassword)
        {
            binding.showPassword.hideKeyBoard()
            binding.edPassword.transformationMethod =
                if (binding.edPassword.transformationMethod is AsteriskPasswordTransformationMethod) {
                    binding.showPassword.text = "Hide"
                    null
                } else {
                    binding.showPassword.text = "View"
                    AsteriskPasswordTransformationMethod()
                }
            binding.edPassword.setSelection(binding.edPassword.text.length)
        }
    }

    override fun initObservers() {
        loginViewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it == true) showLoading() else hideLoading()

        }
        loginViewModel.liveEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is LoginSuccessEvent -> {
                    Toast.makeText(
                        context,
                        event.signInResponse?.Message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    event.signInResponse?.let {
                        saveModelObjectToSharedPreference(
                            requireContext(),
                            Constants.LOGIN_DETAILS,
                            Constants.LOGIN,
                            it
                        )
                    }

                    when (event.signInResponse?.info?.user_role) {
                        1 -> {
                            getEncryptedSharedPreferences(requireContext())?.edit()
                                ?.putInt("type", 1)
                                ?.apply()
                            getEncryptedSharedPreferences(requireContext())?.edit()
                                ?.putBoolean(Constants.isLogin, true)?.apply()
                            if (event.signInResponse.info.emergency_count == 0) {
                                findNavController().navigate(R.id.addEmergencyContactScreen)
                            } else {
                                startActivity(Intent(requireContext(), HomeActivity::class.java))
                            }
                        }
                        2 -> {
                            getEncryptedSharedPreferences(requireContext())?.edit()
                                ?.putInt("type", 2)
                                ?.apply()
                            getEncryptedSharedPreferences(requireContext())?.edit()
                                ?.putBoolean(Constants.isLogin, true)?.apply()
                            startActivity(Intent(requireContext(), HomeActivity::class.java))
                        }
                    }


                }
                is LoginFailEvent -> {
                    getEncryptedSharedPreferences(requireContext())?.edit()
                        ?.putBoolean(Constants.isLogin, false)
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
                        is InvalidEmailException -> {
                            context?.showAlertDialog(
                                title = getString(R.string.Error),
                                message = getString(R.string.please_enter_valid_email),
                                button = getString(R.string.btn_ok)
                            )
                        }
                        is InvalidPasswordException -> {
                            context?.showAlertDialog(
                                title = getString(R.string.Error),
                                message = getString(R.string.please_enter_valid_password),
                                button = getString(R.string.btn_ok)
                            )

                        }
                        else -> {
                            if (event.error.message == null) {
                                context?.showAlertDialog(
                                    title = getString(R.string.failed),
                                    message = getString(R.string.login_error),
                                    button = getString(R.string.btn_ok)
                                )
                            }
                        }
                    }
                }
                is LoginErrorEvent -> {
                    getEncryptedSharedPreferences(requireContext())?.edit()
                        ?.putBoolean(Constants.isLogin, false)
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.toString(),
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