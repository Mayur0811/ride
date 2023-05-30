package com.bayride.presentation.features.giveDriverRating

import android.app.Dialog
import android.content.DialogInterface
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
import com.bayride.common.views.getLoginDetails
import com.bayride.common.views.getSignUpDetails
import com.bayride.common.views.showAlertDialog
import com.bayride.databinding.FragmentGiveDriverRatingScreenBinding
import com.bayride.presentation.features.bookingSuccessfully.BookingSuccessfullyScreen
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GiveDriverRatingScreen : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentGiveDriverRatingScreenBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private val offsetFromTop = 200
    val viewModel: DriverRatingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGiveDriverRatingScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private var mBottomSheetListener: BottomSheetListener? = null

    interface BottomSheetListener {
        fun onBookingSuccess(show: Boolean, radius: Float)
    }

    fun setBottomSheetListener(bottomSheetListener: BottomSheetListener?) {
        mBottomSheetListener = bottomSheetListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    private fun initView() {
        viewModel.getDriverProfile()
        binding.btnSubmit.setOnClickListener {
            binding.ratingBar.rating
            val user_id = if (getLoginDetails(requireContext())?.info?.user_id == null) {
                getSignUpDetails(requireContext())?.info?.user_id
            } else {
                getLoginDetails(requireContext())?.info?.user_id
            }

            viewModel.addReviewToDriver(user_id, binding.ratingBar.rating)
        }

        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.profile }) {
            if (it != null) {
                it.info?.driver_info?.get(0).let {driverInfo->
                    binding.ratingBar.rating = driverInfo?.real_star?.toFloat() ?: 0F
                    Glide.with(requireContext())
                        .load(Constants.imageDomain + driverInfo?.user_profile_pic)
                        .placeholder(R.drawable.ic_user_avatar).into(binding.driverProfileIcon)
                    binding.driverName.text = driverInfo?.user_name
                    binding.driverPhoneNo.text = driverInfo?.user_phone_number
                }
            }
        }
        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                DriverRatingSuccessFully -> {
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                is DriverRatingErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
                is DriverProfileSuccessEvent -> {

                }

                is DriverRatingFailedEvent -> {
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        findNavController().apply {
            previousBackStackEntry?.savedStateHandle?.set(
                Constants.REMOVE_BLUR,
                true
            )
        }
        mBottomSheetListener?.onBookingSuccess(true, 5F)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // mBottomSheetListener?.onBookingSuccess(true, 5F)
                }
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    // mBottomSheetListener?.onBookingSuccess(true, 5F)
                }
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    //  mBottomSheetListener?.onBookingSuccess(false, 1F)

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // mBottomSheetListener?.onBookingSuccess(true, 5F)

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
            bottomSheetBehavior.peekHeight = 600
            bottomSheetBehavior.isFitToContents = false
            bottomSheetBehavior.isDraggable = false
            var ratio = resources.getDimension(R.dimen.dp400) / height.toFloat()
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