package com.bayride.presentation.features.driver.sendOffer

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.utils.Constants
import com.bayride.common.utils.toDateToLocal
import com.bayride.common.views.getLoginDetails
import com.bayride.common.views.makeCall
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.databinding.SendOfferScreenBinding
import com.bayride.presentation.features.bookingSuccessfully.BookingSuccessfullyScreen
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.send_offer_screen.*
import javax.inject.Inject

@AndroidEntryPoint
class SendOfferScreen : BottomSheetDialogFragment() {
    private lateinit var binding: SendOfferScreenBinding
    private val offsetFromTop = 200
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    val viewModel: SendOfferViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private var mBottomSheetListener: BottomSheetListener? = null

    interface BottomSheetListener {
        fun onBookingSuccess(show: Boolean, radius: Float)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SendOfferScreenBinding.inflate(inflater, container, false)

        return binding.root
    }

    fun setBottomSheetListener(bottomSheetListener: BottomSheetListener?) {
        mBottomSheetListener = bottomSheetListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    private fun initView() {
        viewModel.getFairList()

        binding.btnOfferSend.setOnClickListener {
            binding.edPrice.visible(true)
            binding.txtOfferPrice.visible(true)
            binding.btnSendOffer.visible(true)
            binding.btnLayout.visible(false)
        }

        binding.btnSendOffer.setOnClickListener {
            if (binding.edPrice.text.toString().isEmpty()) {
                context?.showAlertDialog(
                    title = getString(R.string.Error),
                    message = "Please price can not be blank",
                    button = getString(R.string.btn_ok)
                )
                return@setOnClickListener
            }

            viewModel.sendOfferUser(binding.edPrice.text.toString().toInt())
        }

        binding.btnCall.setOnClickListener {
            viewModel.currentState.fairList?.Info?.user_phone_number?.let { it1 -> makeCall(it1, requireContext()) }
        }
        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.loading }) {
            if (it == true) binding.progress.visible(true) else binding.progress.visible(false)
        }

        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.fairList }) { fair ->
            if (fair?.Status == 1) {
                binding.let {
                    dateAndTime.text = fair.Info?.created_at?.toDateToLocal()
                    txt_from_location.text = fair.Info?.from_address + ", " + fair.Info?.from_city+ ", " + fair.Info?.from_country
                    txt_to_location.text=fair.Info?.to_address + ", " + fair.Info?.to_city+ ", " + fair.Info?.to_country
                    ridePalTmBtn.text="$${fair.Info?.fare_cost_by_user}"
                    driver_name.text=fair.Info?.user_name
                    driver_phone_no.text=fair.Info?.user_phone_number
                    Glide.with(requireContext())
                        .load(Constants.imageDomain + fair.Info?.user_profile_pic)
                        .placeholder(R.drawable.ic_user_avatar).into(binding.driverProfileIcon)
                }
            }
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                FairListSuccessEvent -> {
                }
                SendOfferSuccessFully -> {
                    findNavController().navigate(R.id.homeScreen2)
                }
                is SendOfferErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.toString() + " " + event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )

                }
                is SendOfferFailedEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
            }
        }
    }


    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetListener?.onBookingSuccess(true, 5F)
                }
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    mBottomSheetListener?.onBookingSuccess(true, 5F)
                }
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetListener?.onBookingSuccess(false, 1F)

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetListener?.onBookingSuccess(true, 5F)

                }
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//        if (!CommonUtils.isDarkModeOn(activity))
//            dialog.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        // val view = View.inflate(activity?.applicationContext, R.layout.fragment_book_now_screen, null)
        bottomSheetDialog.setOnShowListener {
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout

            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.addBottomSheetCallback(mBottomSheetBehaviorCallback)
            //    bottomSheetDialog.setContentView(view)
            val layoutParams = bottomSheet.layoutParams
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels

            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            bottomSheet.layoutParams = layoutParams
            bottomSheetBehavior.peekHeight = 520
            bottomSheetBehavior.isFitToContents = false
            bottomSheetBehavior.isDraggable = false
            var ratio = resources.getDimension(R.dimen.dp380) / height.toFloat()
            if (ratio < 0)
                ratio = 0f
            if (ratio > 1)
                ratio = 1f
            bottomSheetBehavior.halfExpandedRatio = ratio
            bottomSheetBehavior.expandedOffset = offsetFromTop
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            bottomSheetBehavior.isHideable = false
        }

        return bottomSheetDialog
    }

    companion object {
        const val TAG = "ModalBottomSheet"
        fun newInstance() = BookingSuccessfullyScreen()
    }

}