package com.bayride.presentation.features.driver.onGoingScreendialog

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
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.data.datasources.remote.entities.BookingHistoryInfo
import com.bayride.data.models.dto.DriverOngoingFairDetails
import com.bayride.databinding.OngoingBottomsheetBinding
import com.bayride.presentation.features.driver.otpdialog.PickupOTPVerificationDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnGoingBottomSheet constructor(
    val bookingHistoryInfo: BookingHistoryInfo? = null,
    val driverOngoingFairDetails: DriverOngoingFairDetails? = null
) :
    BottomSheetDialogFragment() {
    private lateinit var binding: OngoingBottomsheetBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private val offsetFromTop = 200
    var height = 0
    val viewModel: OnGoingBottomSheetViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OngoingBottomsheetBinding.inflate(inflater, container, false)
        if (bookingHistoryInfo != null) {
            viewModel.loadData(bookingHistoryInfo = bookingHistoryInfo)
        }
        if (driverOngoingFairDetails != null) {
            viewModel.loadData(driverOngoingFairDetails = driverOngoingFairDetails)
        }

        Glide.with(requireContext())
            .load(Constants.imageDomain + getLoginDetails(requireContext())?.info?.user_profile_pic)
            .placeholder(R.drawable.ic_user_avatar).into(binding.driverProfileIcon)
        binding.driverPhoneNo.text = getLoginDetails(requireContext())?.info?.user_phone_number
        binding.driverName.text = getLoginDetails(requireContext())?.info?.user_name
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private var mBottomSheetListener: BottomSheetListener? = null

    interface BottomSheetListener {
        fun onBookingSuccess(show: Boolean, radius: Float, height: Int? = null)
    }

    fun setBottomSheetListener(bottomSheetListener: BottomSheetListener?) {
        mBottomSheetListener = bottomSheetListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    private fun initView() {

        val dialog = PickupOTPVerificationDialog()
        binding.btnPickup.setOnClickListener {
            viewModel.pikUpDoneByDriver(1)
        }
        binding.btnComplete.isClickable = false
        binding.btnComplete.setOnClickListener {
            viewModel.pikUpCompleteByDriver()
        }

        dialog.setListener(object : Constants.PickupOTPListener {
            override fun onDone(otp: String) {
                binding.btnPickup.background.alpha = 190
                binding.layoutBtn.background =
                    resources.getDrawable(R.drawable.ic_btn_green_complete, null)
                binding.btnPickup.isClickable = false
                binding.btnComplete.isClickable = true

            }
        })

        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.loading }) {
            if (it == true) binding.progress.visible(true) else binding.progress.visible(false)
        }
        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.bookingHistoryInfo }) {
            if (it != null) {
                binding.apply {
                    txtBookingId.text = "Booking Id: ${it.fare_booking_unique_id}"
                    dateAndTime.text = it.booked_at?.toDateToLocal()
                    txtFromLocation.text =
                        "${it.from_address}, ${it.from_city}, ${it.from_country}"
                    txtToLocation.text =
                        "${it.to_address}, ${it.to_city}, ${it.to_country}"
                    price.text = "$${it.fare_booking_amount}"
                }
            }
        }
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.driverOngoingFairDetails }) { driverOngoingDetails ->
            driverOngoingDetails?.info?.let {
                binding.apply {
                    txtBookingId.text = "Booking Id: ${it.fare_booking_unique_id}"
                    dateAndTime.text = it.booked_at?.toDateToLocal()
                    txtFromLocation.text =
                        "${it.from_address}, ${it.from_city}, ${it.from_country}"
                    txtToLocation.text =
                        "${it.to_address}, ${it.to_city}, ${it.to_country}"
                    price.text = "$${it.fare_booking_amount}"
                }
            }
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                OnGoingBottomSheetSuccessEvent -> {
                    Toast.makeText(requireContext(), "Sent", Toast.LENGTH_SHORT).show()
                    activity?.supportFragmentManager.let {
                        if (it != null) {
                            dialog.show(it, "")
                        }
                    }
                }
                PickupCompleteByDriverSuccessEvent -> {
                    dismiss()
                    findNavController().navigate(R.id.driverBookingHistory)
                }
                is OnGoingBottomSheetErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
                is OnGoingBottomSheetFailedEvent -> {
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
                    try {
//                        val r = Rect()
//                        bottomSheet.getGlobalVisibleRect(r)
//                        height = r.height()
//                        binding.constraintLayout.isDrawingCacheEnabled = true
//                        val bitmapWithBottomSheet = binding.constraintLayout.getDrawingCache()
//                        val bout = FileOutputStream(
//                            File(
//                                requireContext().filesDir.toString() + File.separator,
//                                "map1.png"
//                            )
//                        )
//                        bitmapWithBottomSheet!!.compress(Bitmap.CompressFormat.PNG, 90, bout)
                        mBottomSheetListener?.onBookingSuccess(false, 1F, height)

                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }

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
            var ratio = resources.getDimension(R.dimen.dp330) / height.toFloat()
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
        fun newInstance() = OnGoingBottomSheet()
    }

}