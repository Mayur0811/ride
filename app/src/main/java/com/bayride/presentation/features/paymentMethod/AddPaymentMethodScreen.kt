package com.bayride.presentation.features.paymentMethod

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.presentation.homeActivity.HomeActivity
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.sharedpreference.saveModelObjectToSharedPreference
import com.bayride.common.sharedpreference.saveObjectToSharedPreference
import com.bayride.common.utils.Constants
import com.bayride.common.views.showAlertDialog
import com.bayride.data.models.dto.PassengerSignup
import com.bayride.databinding.FragmentAddPaymentMethodBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddPaymentMethodScreen : BaseFragment<FragmentAddPaymentMethodBinding>() {

    private val viewModel: AddPaymentMethodViewModel by viewModels()

    override fun initData(data: Bundle?) {

    }

    override fun initViews() {
        binding.btnCreditCard.isChecked = true
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

        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))
    }

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            getEncryptedSharedPreferences(requireContext())?.edit()
                ?.putBoolean(Constants.ADD_PAYMENT_METHOD_SCREEN, false)?.apply()
            findNavController().popBackStack()
        }

        safetyClick.setViewClickSafetyListener(binding.btnNext) {
            if (Constants.isFromSignUp) {

                if (binding.termsAndConditionsCheckbox.isChecked) {
                    if (getEncryptedSharedPreferences(requireContext())?.getBoolean(
                            Constants.ADD_PAYMENT_METHOD_SCREEN,
                            false
                        ) == true
                    ) {
                        saveObjectToSharedPreference(
                            requireContext(),
                            Constants.BAYRIDE_PASSENGER_MODEL,
                            Constants.PASSENGER,
                            PassengerSignup(),
                            false,
                            signatureClear = false
                        )
                        findNavController().navigate(R.id.addEmergencyContactScreen)
//                        getPassenger(requireContext()).let {
//                            viewModel.signUpEdit(
//                                it?.phoneNumber.toString(),
//                                it?.username.toString(),
//                                it?.name.toString(),
//                                it?.createPassword?.first.toString(),
//                                it?.address,
//                                File(
//                                    FileUtils.getRealPath(requireContext(), Uri.parse(it?.photo))
//                                        .toString()
//                                ),
//                                20.141214,
//                                72.121212,
//                                File(it?.signature.toString()),
//                                null,
//                                null
//                            )
//                        }
                    } else {
                        getEncryptedSharedPreferences(requireContext())?.edit()
                            ?.putBoolean("isSignup", true)?.apply()
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.accept_term_condition),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireActivity(), "Payment Method Coming soon", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        safetyClick.setViewClickSafetyListener(binding.termsAndConditionsLayout) {
            binding.termsAndConditionsCheckbox.isChecked =
                !binding.termsAndConditionsCheckbox.isChecked
        }
        safetyClick.setViewClickSafetyListener(binding.termsAndConditions)
        {
            binding.termsAndConditionsCheckbox.isChecked =
                !binding.termsAndConditionsCheckbox.isChecked
        }
        binding.toggle.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.btnCreditCard.id -> {
                    binding.btnCryptoWallet.background = null
                    binding.btnCryptoWallet.elevation = 0f
                    binding.btnCreditCard.background =
                        context?.getDrawable(R.drawable.selected_payment_tab)
                    binding.btnCreditCard.elevation = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        5f,
                        resources.displayMetrics
                    )
                    binding.cryptoLayout.visibility = View.GONE
                    binding.creditCardLayout.visibility = View.VISIBLE
                }
                binding.btnCryptoWallet.id -> {
                    binding.btnCreditCard.background = null
                    binding.btnCreditCard.elevation = 0f
                    binding.btnCryptoWallet.background =
                        context?.getDrawable(R.drawable.selected_payment_tab)
                    binding.btnCryptoWallet.elevation = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        5f,
                        resources.displayMetrics
                    )
                    binding.creditCardLayout.visibility = View.GONE
                    binding.cryptoLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun initObservers() {
        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                is PaymentMethodSuccessEvent -> {
                    if (event.signUpResponse?.Status == 1) {
                        saveModelObjectToSharedPreference(
                            requireContext(),
                            Constants.SIGN_UP_DETAILS,
                            Constants.SIGN_UP,
                            event.signUpResponse
                        )
                        saveModelObjectToSharedPreference(
                            requireContext(),
                            Constants.LOGIN_DETAILS,
                            Constants.LOGIN,
                            event.signUpResponse
                        )

                        findNavController().navigate(AddPaymentMethodScreenDirections.actionAddPaymentMethodScreenToAddEmergencyScreen())

                    } else {
                        event.signUpResponse?.Message?.let {
                            requireContext().showAlertDialog(
                                "Error",
                                it,
                                "ok",
                                true,
                                clickListener = {

                                }
                            )
                        }
                    }
                }
                is PaymentMethodErrorEvent -> {
                    requireContext().showAlertDialog(
                        "Error",
                        event.code.toString() + " " + event.errorMessage,
                        "ok",
                        true,
                        clickListener = {

                        }
                    )
                }
                is PaymentMethodFailEvent -> {
                    requireContext().showAlertDialog(
                        "Error",
                        event.error.message.toString(),
                        "ok",
                        true,
                        clickListener = {
                        })
                }
            }

        }

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

    override fun onBackPressed() {
        super.onBackPressed()
        getEncryptedSharedPreferences(requireContext())?.edit()
            ?.putBoolean(Constants.ADD_PAYMENT_METHOD_SCREEN, false)?.apply()
        findNavController().popBackStack()
    }

}