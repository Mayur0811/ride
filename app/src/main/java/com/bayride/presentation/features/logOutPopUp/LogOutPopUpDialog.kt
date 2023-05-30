package com.bayride.presentation.features.logOutPopUp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bayride.MainActivity
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.sharedpreference.saveModelObjectToSharedPreference
import com.bayride.common.utils.Constants
import com.bayride.common.views.blur
import com.bayride.common.views.showAlertDialog
import com.bayride.databinding.DialogLogoutBinding
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LogOutPopUpDialog : DialogFragment() {
    private lateinit var binding: DialogLogoutBinding
    val viewModel: LogoutViewModel by viewModels()
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

        binding = DialogLogoutBinding.inflate(inflater)

        binding.btnYes.setOnClickListener {
            viewModel.logout()

        }
        binding.btnNo.setOnClickListener {
            dismiss()
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                LogoutSuccessEvent -> {
                    saveModelObjectToSharedPreference(
                        requireContext(),
                        Constants.SIGN_UP_DETAILS,
                        Constants.SIGN_UP,
                        null
                    )
                    saveModelObjectToSharedPreference(
                        requireContext(),
                        Constants.LOGIN_DETAILS,
                        Constants.LOGIN,
                        null
                    )
                    saveModelObjectToSharedPreference(
                        requireContext(), Constants.BAYRIDE_DRIVER_MODEL,
                        Constants.PROFILE,
                        null
                    )
                    getEncryptedSharedPreferences(requireContext())?.edit()
                        ?.putBoolean("isSignup", false)?.apply()
                    getEncryptedSharedPreferences(requireContext())?.edit()
                        ?.putBoolean(Constants.isLogin, false)?.apply()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    activity?.finish()
                    dismiss()
                }
                is LogoutErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.toString() + " " + event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
                is LogoutFailedEvent -> {
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