package com.bayride.presentation.features.driver.filter

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.annotation.NonNull
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.utils.Constants
import com.bayride.databinding.FilterscreenBinding

import com.bayride.presentation.features.bookingSuccessfully.BookingSuccessfullyScreen
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterScreen : BottomSheetDialogFragment() {
    private lateinit var binding: FilterscreenBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private val offsetFromTop = 200
    var onFilterUserList: OnFilterUserList? = null
    var miles: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FilterscreenBinding.inflate(inflater, container, false)
        binding.seekBarDistance.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.txtDistance.text = "$progress Miles"
                miles = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private var mBottomSheetListener: BottomSheetListener? = null

    interface BottomSheetListener {
        fun onFilter(show: Boolean, radius: Float)
    }

    fun setOnFilterListener(onFilterUserListListener: OnFilterUserList) {
        onFilterUserList = onFilterUserListListener
    }

    fun setBottomSheetListener(bottomSheetListener: BottomSheetListener?) {
        mBottomSheetListener = bottomSheetListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    private fun initView() {
        binding.btnApplyFilter.setOnClickListener {
            onFilterUserList?.onFilterData(21.2372533, 72.8855092, miles)
            dismiss()
            // findNavController().navigate(R.id.sendOfferScreen)
        }

    }


    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetListener?.onFilter(false, 1F)
                }
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    mBottomSheetListener?.onFilter(true, 5F)
                }
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetListener?.onFilter(true, 5F)

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetListener?.onFilter(true, 5F)

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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mBottomSheetListener?.onFilter(false, 1F)
        findNavController().apply {
            previousBackStackEntry?.savedStateHandle?.set(
                Constants.REMOVE_BLUR,
                Pair(
                    true, Pair(miles, Pair(21.2372533, 72.8855092))
                )
            )
        }

    }

    companion object {
        const val TAG = "ModalBottomSheet"
        fun newInstance() = BookingSuccessfullyScreen()
    }

    interface OnFilterUserList {
        fun onFilterData(user_let: Double, user_long: Double, miles: Int)
    }
}