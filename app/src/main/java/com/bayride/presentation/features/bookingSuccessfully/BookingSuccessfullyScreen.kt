package com.bayride.presentation.features.bookingSuccessfully

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.bayride.presentation.homeActivity.HomeActivity
import com.bayride.R
import com.bayride.common.utils.Constants
import com.bayride.data.models.dto.DriverDetailsModel
import com.bayride.databinding.FragmentBookingSuccessfullyScreenBinding
import com.bayride.presentation.features.homedetails.adapter.DriveRequestAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingSuccessfullyScreen : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBookingSuccessfullyScreenBinding
    private lateinit var driveRequestAdapter: DriveRequestAdapter
    private val dataList: ArrayList<DriverDetailsModel> = arrayListOf()
    private val offsetFromTop = 200
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private val radioButtonList: ArrayList<RadioButton> = arrayListOf()

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
    ): View? {
        binding = FragmentBookingSuccessfullyScreenBinding.inflate(inflater, container, false)
        val id = "#919100"
        val spanMessage =
            SpannableString("Your Booking Id: $id has\nbeen confirmed. Your driver will come soon.")
        spanMessage.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.booking_id_color
                )
            ), 17, 17 + id.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.txtMessage.text = spanMessage
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
        binding.btnDone.setOnClickListener {
            mBottomSheetListener?.onBookingSuccess(false, 1F)
            Constants.ongoing = true
          //  findNavController().navigate(BookingSuccessfullyScreenDirections.actionBookingSuccessfullyScreenToHomeScreen2())
            startActivity(Intent(requireActivity(), HomeActivity::class.java))

            activity?.finish()
            requireActivity().overridePendingTransition(com.mukesh.R.anim.abc_fade_in, com.mukesh.R.anim.abc_fade_out)

            // activity?.findViewById<FragmentContainerView>(R.id.nav_host_fragment_content_drawer)?.findNavController()?.currentDestination
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
        val bottomSheetDialog:BottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
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
            var ratio = resources.getDimension(R.dimen.dp320) / height.toFloat()
            if (ratio < 0)
                ratio = 0f
            if (ratio > 1)
                ratio = 1f
            bottomSheetBehavior.halfExpandedRatio = ratio
            bottomSheetBehavior.expandedOffset = offsetFromTop
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.isHideable = false
        }

        return bottomSheetDialog
    }

    companion object {
        const val TAG = "ModalBottomSheet"
        fun newInstance() = BookingSuccessfullyScreen()
    }


}