package com.bayride.presentation.features.ongoing

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bayride.R
import com.bayride.common.permission.showPermanentlyDeniedDialog
import com.bayride.common.permission.showRationaleDialog
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.utils.Constants
import com.bayride.common.utils.getVehicleType
import com.bayride.common.utils.toDateToLocal
import com.bayride.common.views.*
import com.bayride.databinding.FragmentOngoingBinding
import com.bayride.presentation.features.pikupPopup.PickUpPopUpDialog
import com.bumptech.glide.Glide
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OngoingScreen : BottomSheetDialogFragment(), PermissionRequest.Listener {
    private lateinit var binding: FragmentOngoingBinding
    private val offsetFromTop = 200
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    val viewModel: OnGoingViewModel by viewModels()
    var text: String? = null

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
        binding = FragmentOngoingBinding.inflate(inflater, container, false)
        val spanText = SpannableString("You agree our Terms & Conditions")
        val clickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
                startActivity(i)
            }
        }
        spanText.setSpan(clickSpan.apply {
            updateDrawState(TextPaint(Color.parseColor("#00000000")))
        }, 14, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanText.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.txtColor)),
            14,
            32,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.termsAndConditions.movementMethod = LinkMovementMethod.getInstance()
        binding.termsAndConditions.text = spanText

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
        viewModel.loadData()


        binding.termsAndConditionsLayout.setOnClickListener {
            binding.termsAndConditionsCheckbox.isChecked =
                !binding.termsAndConditionsCheckbox.isChecked
        }
        binding.termsAndConditions.setOnClickListener {
            binding.termsAndConditionsCheckbox.isChecked =
                !binding.termsAndConditionsCheckbox.isChecked
        }
        request.addListener(this)
        binding.ridePalTmBtn.setOnClickListener {
            request.send()
        }
        binding.btnCall.setOnClickListener {
            makeCall(binding.driverPhoneNo.text.toString(), requireContext())
//            getEncryptedSharedPreferences(requireContext())?.edit()?.putBoolean("show", false)
//                ?.apply()
//            activity?.supportFragmentManager.let {
//                if (it != null) {
//                    dialogPopUp.show(it, "")
//                }
//            }
        }

        binding.constraint.setOnClickListener {
                text = "Is your Driver Arrived to pick up\n you for your Ride?"
            val dialogPopUp = viewModel.currentState.fairList?.let { PickUpPopUpDialog(text!!, it) }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            getEncryptedSharedPreferences(requireContext())?.edit()?.putBoolean("show", true)
                ?.apply()
            activity?.supportFragmentManager.let {
                if (it != null) {
                    dialogPopUp?.show(it, "")
                }
            }
            dialogPopUp?.setPickUpDialogListener(object : Constants.PickupPopUpListener {
                override fun onYes() {
                    //   bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
//                    mBottomSheetListener?.onBookingSuccess(true, 5F)
//                    Handler().postDelayed({
//                        findNavController().navigate(R.id.giveDriverRatingScreen2)
//                    }, 1000)
                }

                override fun OnNo() {

                }

            })
        }
        binding.btnCancelRide.setOnClickListener {
                text = "Is your ride Completed?"
            val dialogPopUp = viewModel.currentState.fairList?.let { PickUpPopUpDialog(text!!, it) }
            activity?.supportFragmentManager.let {
                if (it != null) {
                    dialogPopUp?.show(it, "")
                }
            }
            dialogPopUp?.setPickUpDialogListener(object : Constants.PickupPopUpListener {
                override fun onYes() {
                    //   bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
//                    mBottomSheetListener?.onBookingSuccess(true, 5F)
//                    Handler().postDelayed({
//                        findNavController().navigate(R.id.giveDriverRatingScreen2)
//                    }, 1000)
                }

                override fun OnNo() {

                }

            })
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it == true) binding.progressTop.visible(true) else binding.progressTop.visible(false)
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.fairId }
        ) {
            if (it != 0) {
                viewModel.getFairList(it)
            }
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.contactList }
        ) {
            it?.forEach { contact ->
                sendSMS(contact.contact_phone_number.toString(), "Ride Pal Ride is Started")
            }
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.driverbidprice }
        ) {
            if (it != 0) {
                binding.txtAmount.text = "Amount : $${it}"
            }
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.profile }
        ) { profile ->
            if (profile != null) {
                binding.apply {
                    profile.info?.driver_info?.get(0).let { driverInfo->
                        driverName.text = driverInfo?.user_name
                        driverPhoneNo.text = driverInfo?.user_phone_number
                        ratingBar.rating = driverInfo?.real_star?.toFloat() ?: 0f
                        txtCarName.text = driverInfo?.vehicle_brand + " " + getVehicleType(
                                driverInfo?.vehicle_type_id)
                        txtCarNumber.text = driverInfo?.vehicle_number
                        txtYear.text = driverInfo?.vehicle_year
                        txtLicenseNo.text = driverInfo?.vehicle_license_number
                        txtModelName.text = driverInfo?.vehicle_model
                        txtCarColor.text = driverInfo?.vehicle_colour
                        binding.totalReview.text = "(${driverInfo?.total_review.toString()})"
                        Glide.with(requireContext())
                            .load(Constants.imageDomain + driverInfo?.user_profile_pic)
                            .into(binding.driverProfileIcon)
                    }

                }
            }
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.fairList }
        ) { fair ->
            if (fair != null) {
                binding.apply {
                    txtBookingId.text = "Booking Id: ${fair.Info?.fare_booking_unique_id}"
                    dateAndTime.text = fair.Info?.created_at?.toDateToLocal()
                    txtFromLocation.text =
                        "${fair.Info?.from_address}, ${fair.Info?.from_city}, ${fair.Info?.from_country}"
                    txtToLocation.text =
                        "${fair.Info?.to_address}, ${fair.Info?.to_city}, ${fair.Info?.to_country}"
                }
            }
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                OngoingSuccessEvent -> {
                    viewModel.gertDriverProfile()
                }
                DriverProfileSuccessEvent -> {

                }
                GetContactSuccessEvent -> {

                }
                PickupDoneByUserSuccessEvent -> {
                    if (viewModel.currentState.response?.Status == 1) {

                    } else {
                        context?.showAlertDialog(
                            title = getString(R.string.Error),
                            message = viewModel.currentState.response?.Message.toString(),
                            button = getString(R.string.btn_ok)
                        )
                    }
                }
                is OngoingErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.errorMessage,
                        button = getString(R.string.btn_ok)
                    )
                }
                is OngoingFailEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
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
                    mBottomSheetListener?.onBookingSuccess(false, 5F)

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetListener?.onBookingSuccess(true, 5F)

                }
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout


            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.addBottomSheetCallback(mBottomSheetBehaviorCallback)
            val layoutParams = bottomSheet.layoutParams
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels

            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            bottomSheet.layoutParams = layoutParams

            bottomSheetBehavior.peekHeight = 520
            bottomSheetBehavior.isFitToContents = false
            var ratio = resources.getDimension(R.dimen.dp250) / height.toFloat()

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
        fun newInstance() = OngoingScreen()
    }

    private fun sendSMS(phoneNo: String, msg: String) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(
                requireContext(), "Message Sent",
                Toast.LENGTH_LONG
            ).show()
        } catch (ex: Exception) {
            Toast.makeText(
                requireContext(), ex.message.toString(),
                Toast.LENGTH_LONG
            ).show()
            ex.printStackTrace()
        }
    }

    private val request by lazy {
        permissionsBuilder(
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE
        ).build()
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> context?.showPermanentlyDeniedDialog(result)
            result.anyShouldShowRationale() -> context?.showRationaleDialog(result, request)
            result.allGranted() -> {
                viewModel.listEmergencyContact()
            }
        }
    }
}