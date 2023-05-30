package com.bayride.presentation.features.pikupPopup

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.utils.Constants
import com.bayride.common.views.blur
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.data.models.dto.FairList
import com.bayride.databinding.DialogPickupBinding
import com.bayride.presentation.features.cancelRideDialog.CancelRideDialogScreen
import com.bayride.presentation.features.giveDriverRating.GiveDriverRatingScreen
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderScriptBlur


@AndroidEntryPoint
class PickUpPopUpDialog constructor(val text: String, val fairList: FairList) : DialogFragment(),
    GiveDriverRatingScreen.BottomSheetListener,
    CancelRideDialogScreen.BottomSheetListener {
    val viewModel: PickUpPopupViewModel by viewModels()
    var callback: Constants.PickupPopUpListener? = null
    var flag = false
    fun setPickUpDialogListener(listener: Constants.PickupPopUpListener) {
        callback = listener
    }

    private lateinit var binding: DialogPickupBinding

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

        binding = DialogPickupBinding.inflate(inflater)

        val decorView: View? = activity?.window?.decorView
        val rootView = decorView?.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        binding.blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(requireContext()))
            .setBlurRadius(10F)
        when (fairList.Info?.fare_status) {
            4 -> {
                binding.titleView.text = "Is your Driver Arrived to pick up\n you for your Ride?"
                flag = true
            }
            7 -> {
                binding.titleView.text = "Is your ride Completed?"
                flag = false
            }
        }

//        if (getEncryptedSharedPreferences(requireContext())?.getBoolean("show", false) == true) {
//            binding.titleView.text = "Is your ride Completed?"
//        }
        binding.btnYes.setOnClickListener {
            if (flag) {
                viewModel.pickupDoneByUser(1)
            } else {
                viewModel.fairCompleteByUser(1)
            }
//            if (getEncryptedSharedPreferences(requireContext())?.getBoolean(
//                    "show",
//                    false
//                ) == true
//            ) {
//                callback?.onYes()
//                Handler().postDelayed({}, 100)
//                binding.cardView2.visible(false)
//                findNavController().navigate(R.id.giveDriverRatingScreen2)
//            } else {
//                dismiss()
//                callback?.onYes()
//            }
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                PickUpPopupSuccessEvent -> {
                    dismiss()
                }
                FairCompleteUserSuccessEvent -> {
                    dismiss()
                }
                is PickUpPopupErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
                is PickUpPopupFailEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
            }
        }
        binding.btnNo.setOnClickListener {
            callback?.OnNo()
            if (getEncryptedSharedPreferences(requireContext())?.getBoolean(
                    "show",
                    false
                ) == true
            ) {
                binding.cardView2.visible(false)
                findNavController().navigate(R.id.cancelRideDialogScreen)
//                val giveDriverRatingScreen = CancelRideDialogScreen()
//                activity?.supportFragmentManager.let {
//                    if (it != null) {
//                        giveDriverRatingScreen.show(it, "")
//                    }
//                }
//                giveDriverRatingScreen.setBottomSheetListener(this)
            } else {
                dismiss()

            }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<Boolean>(Constants.REMOVE_BLUR)
                .observe(viewLifecycleOwner) {
                    dismiss()
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

    override fun onDestroyView() {
        super.onDestroyView()
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            remove<Boolean>(Constants.REMOVE_BLUR)
        }
    }

    override fun onBookingSuccess(show: Boolean, radius: Float) {
        dismiss()
    }


}