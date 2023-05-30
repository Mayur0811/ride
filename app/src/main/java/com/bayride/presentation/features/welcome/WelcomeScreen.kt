package com.bayride.presentation.features.welcome

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
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.databinding.FragmentWelcomeScreenBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeScreen : BaseFragment<FragmentWelcomeScreenBinding>() {


    override fun initData(data: Bundle?) {

    }

    override fun initViews() {
        val spanText = SpannableString("I accept to Terms & Conditions")
        val clickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
                startActivity(i)
            }
        }
        spanText.setSpan(clickSpan.apply {
            updateDrawState(TextPaint(Color.parseColor("#00000000")))
        }, 12, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanText.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.txtColor
                )
            ), 12, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.termsAndConditions.movementMethod = LinkMovementMethod.getInstance()
        binding.termsAndConditions.text = spanText
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.lightGreen))

    }

    override fun initActions() {
      //  getEncryptedSharedPreferences(requireContext())?.edit()?.putInt("type", 1)?.apply()
        /*safetyClick.setViewClickSafetyListener(binding.smartfareTextView)
        {
            binding.smartfareCheckbox.isChecked = !binding.smartfareCheckbox.isChecked
        }*/

//        if (getEncryptedSharedPreferences(requireContext())?.getInt("type", 0) == 1) {
//            binding.userApp.isChecked = true
//        } else {
//            binding.driverApp.isChecked = true
//        }
        binding.layoutUserApp.setOnClickListener {
            binding.userApp.isChecked = true
            binding.driverApp.isChecked = false
            getEncryptedSharedPreferences(requireContext())?.edit()?.putInt("type", 1)?.apply()
        }
        binding.layoutDriverApp.setOnClickListener {
            binding.userApp.isChecked = false
            binding.driverApp.isChecked = true
            getEncryptedSharedPreferences(requireContext())?.edit()?.putInt("type", 2)?.apply()

        }
        binding.driverApp.setOnCheckedChangeListener { _, p1 ->
            if (p1) {
                getEncryptedSharedPreferences(requireContext())?.edit()?.putInt("type", 2)?.apply()
                binding.userApp.isChecked = false
            }
        }
        binding.userApp.setOnCheckedChangeListener { _, p1 ->
            if (p1) {

                binding.driverApp.isChecked = false
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
        safetyClick.setViewClickSafetyListener(binding.btnNext)
        {
            if (binding.smartfareCheckbox.isChecked && binding.termsAndConditionsCheckbox.isChecked) {
                findNavController().navigate(R.id.loginScreen)
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Please Accept Terms & Conditions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun initObservers() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        activity?.finish()
    }
}