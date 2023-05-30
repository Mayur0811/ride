package com.bayride.presentation.features.driver.otpdialog

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bayride.R
import com.bayride.common.utils.Constants
import com.bayride.common.views.blur
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.databinding.PickupOtpVerificationBinding
import com.bayride.presentation.features.driver.locationDialog.LocationDialog
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderScriptBlur


@AndroidEntryPoint
class PickupOTPVerificationDialog : DialogFragment() {

    var callback: Constants.PickupOTPListener? = null

    val viewModel: PickupOtpViewModel by viewModels()

    fun setListener(listener: Constants.PickupOTPListener) {
        callback = listener
    }

    private lateinit var binding: PickupOtpVerificationBinding
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

        binding = PickupOtpVerificationBinding.inflate(inflater)

        val decorView: View? = activity?.window?.decorView
        val rootView = decorView?.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        binding.blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(requireContext()))
            .setBlurRadius(10F)

        binding.otpView.setOtpCompletionListener {
            otp = it
        }
        binding.btnDone.setOnClickListener {
            if (otp.isNullOrEmpty()) {
                context?.showAlertDialog(
                    title = getString(R.string.Error),
                    message = "Please enter valid OTP",
                    cancelable = true,
                    button = getString(R.string.btn_ok)
                )
                return@setOnClickListener
            }

            viewModel.verifyPickupOtp(otp, 1)

//            val locationDialog = LocationDialog()
//            locationDialog.show(requireFragmentManager(), "")
//            if (otp.isNullOrEmpty()) {
//                Toast.makeText(context, "Please Enter Otp", Toast.LENGTH_SHORT).show()
//            } else {
//                dismiss()
//
//            }
        }
        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.loading }) {
            if (it == true) binding.progress.visible(true) else binding.progress.visible(false)
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                PickupOtpSuccessEvent -> {
                    if (viewModel.currentState.response?.Status == 1) {
                        dismiss()
                        callback?.onDone(otp.toString())
                    } else {
                        context?.showAlertDialog(
                            title = getString(R.string.Error),
                            message = viewModel.currentState.response?.Message.toString(),
                            button = getString(R.string.btn_ok)
                        )
                    }
                }
                is PickupOtpErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
                is PickupOtpFailedEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
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