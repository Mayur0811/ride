package com.bayride.presentation.features.countrycodedialog

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.utils.Constants
import com.bayride.common.views.blur
import com.bayride.common.views.getCountryCode
import com.bayride.common.views.getFlag
import com.bayride.common.views.visible
import com.bayride.databinding.ContryCodeLayoutBinding
import com.bayride.databinding.DialogPickupBinding
import com.bayride.presentation.features.countrycodedialog.adapter.CountryCodeAdapter
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import eightbitlab.com.blurview.RenderScriptBlur

class CountryCodeDialog : DialogFragment() {

    var callback: Constants.OnCountryCodeListener? = null
    val countryCodeAdapter = CountryCodeAdapter()
    fun setPickUpDialogListener(listener: Constants.OnCountryCodeListener) {
        callback = listener
    }

    private lateinit var binding: ContryCodeLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ContryCodeLayoutBinding.inflate(inflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        getCountryCode()?.let { countryCodeAdapter.countryList(it,requireContext()) }
        binding.rvCountryCode.adapter = countryCodeAdapter
        countryCodeAdapter.onCountryCode = {
            callback?.countryCode(it.second,it.first)
            dismiss()
        }
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onDestroyView() {
        super.onDestroyView()
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            remove<Boolean>(Constants.REMOVE_BLUR)
        }
    }
}