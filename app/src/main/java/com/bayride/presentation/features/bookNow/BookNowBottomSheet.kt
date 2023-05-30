package com.bayride.presentation.features.bookNow

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.annotation.NonNull
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.utils.Constants
import com.bayride.common.utils.getVehicleType
import com.bayride.common.utils.toDateToLocal
import com.bayride.databinding.FragmentBookNowScreenBinding
import com.bayride.presentation.features.selection.SelectionScreenArgs
import com.bumptech.glide.Glide

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookNowBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBookNowScreenBinding
    private val offsetFromTop = 200
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private val radioButtonList: ArrayList<RadioButton> = arrayListOf()
    val viewModel: BookNowViewModel by viewModels()
    val bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private var mBottomSheetListener: BottomSheetListenerHideDialog? = null

    interface BottomSheetListenerHideDialog {
        fun onBottomSheetBookNowShow(show: Boolean, radius: Float)
    }

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookNowScreenBinding.inflate(inflater)

        radioButtonList.add(binding.radioCreditCard)
        radioButtonList.add(binding.radioBitcoin)
        radioButtonList.add(binding.radioETH)
        radioButtonList.add(binding.radioDodge)
        radioButtonList.add(binding.radioRideNode)

        return binding.root

    }

    fun setBottomSheetListener(bottomSheetListener: BottomSheetListenerHideDialog?) {
        mBottomSheetListener = bottomSheetListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initView()

    }


    private fun initView() {

        binding.btnBack.setOnClickListener {
            dialog?.dismiss()
        }
        binding.btnBookNow.setOnClickListener {
            findNavController().navigate(R.id.creditCardBottomSheet, bundle)
        }
        binding.optionCreditCard.setOnClickListener {
            uncheckOthers(binding.radioCreditCard)
        }
        binding.optionBitcoin.setOnClickListener {
        }
        binding.optionETH.setOnClickListener {
        }
        binding.optionDodge.setOnClickListener {
        }
        binding.optionRideNode.setOnClickListener {
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.fairList }
        ) {
            if (it?.Info != null) {
                it.Info.let { fair ->
                    binding.dateAndTime.text = fair.created_at?.toDateToLocal()
                    binding.txtFromLocation.text =
                        "${fair.from_address}, ${fair.from_city}, ${fair.from_country}"
                    binding.txtToLocation.text =
                        "${fair.to_address}, ${fair.to_city}, ${fair.to_country}"
                }

            }
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.driverRequestInfo }
        ) {
            if (it != null) {
                Glide.with(requireContext()).load(Constants.imageDomain + it.user_profile_pic)
                    .placeholder(R.drawable.ic_user_avatar).into(binding.driverProfileIcon)
                binding.driverName.text = it.user_name
                binding.driverPhoneNo.text = it.user_phone_number
                binding.ratingBar.rating = it.real_star?.toFloat() ?: 0.0F
                binding.totalReview.text = "(${it.total_review})"
                binding.txtCarName.text =
                    it.vehicle_brand + " " + getVehicleType(it.vehicle_type_id)
                binding.txtCarNumber.text = it.vehicle_number
                binding.txtPrice.text = "$${it.driver_bid_price}"
                bundle.putString("price", it.driver_bid_price)
                bundle.putString("fair_driver_bid_id", it.fare_driver_bid_id.toString())

            }
        }

        val args = arguments?.let { BookNowBottomSheetArgs.fromBundle(it) } ?: run {
            findNavController().popBackStack()
            return
        }
        viewModel.loadData(args)
    }


    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetListener?.onBottomSheetBookNowShow(true, 5F)
                }
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    mBottomSheetListener?.onBottomSheetBookNowShow(true, 5F)

                }
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetListener?.onBottomSheetBookNowShow(false, 1F)

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetListener?.onBottomSheetBookNowShow(true, 5F)

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
        fun newInstance() = BookNowBottomSheet()
    }

    private fun uncheckOthers(radioButton: RadioButton) {
        radioButton.isChecked = true
        radioButtonList.forEach {
            if (it.isChecked && radioButton.id != it.id) {
                it.isChecked = false
                return@forEach
            }
        }
    }
}