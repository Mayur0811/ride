package com.bayride.presentation.features.driver.locationDialog

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.bayride.R
import com.bayride.common.utils.Constants
import com.bayride.common.views.blur
import com.bayride.databinding.LocationDialogBinding
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import eightbitlab.com.blurview.RenderScriptBlur


class LocationDialog : DialogFragment() {

    var callback: Constants.LocationListener? = null

    fun setListener(listener: Constants.LocationListener) {
        callback = listener
    }

    var boolean: Boolean = false

    private lateinit var binding: LocationDialogBinding
    private var otp: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Instacapture.capture(requireActivity(), object : SimpleScreenCapturingListener() {
            override fun onCaptureComplete(bitmap: Bitmap) {
                super.onCaptureComplete(bitmap)
                setAlertDialogBackground(bitmap)
            }
        })


        binding = LocationDialogBinding.inflate(inflater)
        val decorView: View? = activity?.window?.decorView
        val rootView = decorView?.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        binding.blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(requireContext()))
            .setBlurRadius(10F)

        binding.btnOk.setOnClickListener {
            if (boolean) {
                dismiss()
            } else {
                boolean = true
                binding.imageView.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_location_matched
                    )
                )
                binding.messageView.text = "You arrive at your pick Up location\n" +
                        "of the passenger."
                binding.btnOk.text = "Confirm"
            }
        }

        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    private fun setAlertDialogBackground(result: Bitmap?) {
        val draw = BitmapDrawable(resources, result?.let { blur(context, it) })
        val window = dialog?.window
        window?.setBackgroundDrawable(draw)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window?.setGravity(Gravity.CENTER)

    }


}