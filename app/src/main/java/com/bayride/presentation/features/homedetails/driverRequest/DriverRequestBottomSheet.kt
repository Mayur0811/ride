package com.bayride.presentation.features.homedetails.driverRequest

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayride.R
import com.bayride.common.utils.Constants.Companion.REMOVE_BLUR
import com.bayride.common.utils.toDateToLocal
import com.bayride.common.views.getLoginDetails
import com.bayride.common.views.getSignUpDetails
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.models.dto.DriverDetailsModel
import com.bayride.databinding.FragmentBookNowScreenBinding
import com.bayride.databinding.FragmentBottomSheetBinding
import com.bayride.presentation.features.homedetails.HomeDetailsScreenDirections
import com.bayride.presentation.features.homedetails.adapter.DriveRequestAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class DriverRequestBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetBinding
    private lateinit var bindingBookNow: FragmentBookNowScreenBinding
    private lateinit var driveRequestAdapter: DriveRequestAdapter
    private val dataList: ArrayList<DriverDetailsModel> = arrayListOf()
    private val offsetFromTop = 200
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    val viewModel: DriverRequestViewModel by viewModels()

    @Inject
    lateinit var secureStorageManager: SecureStorageManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)

    }

    private var mBottomSheetListener: BottomSheetListener? = null

        interface BottomSheetListener {
            fun onBottomSheetShow(show: Boolean, radius: Float)
        }

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetBinding.inflate(inflater)

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
        (getSignUpDetails(requireContext())?.info?.user_id
            ?: getLoginDetails(requireContext())?.info?.user_id)?.let {
            viewModel.getDriverList(
                it
            )
        }
        viewModel.store.observe(owner = viewLifecycleOwner,
            selector = { it.loading })
        {
            if (it == true) binding.progress.visible(true) else binding.progress.visible(false)
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.fairId }
        ) {
            if (it != null) {
                viewModel.getFairList(it)
            }
        }
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.fairList }
        ) { fair ->
            if (fair?.Info != null) {
                fair.Info.apply {
                    binding.txtJourneyDateAndTime.text = created_at?.toDateToLocal()
                    binding.price.text = "$$fare_cost_by_user"
                    binding.txtCurrentPlace.text = "${from_address}, ${from_city}, $from_country"
                    binding.txDestinationPlace.text = "${to_address}, ${to_city}, $to_country"
                }
            }
        }
        viewModel.store.observe(owner = viewLifecycleOwner,
            selector = { it.driverRequestListModel }
        ) {
            if (it?.info?.size == 0) {
                binding.waitingForDriverImageView.visible(true)
                binding.txtWaitingForDriver.visible(true)
            } else {
                driveRequestAdapter = DriveRequestAdapter()
                it?.info?.let { it1 -> driveRequestAdapter.updateList(it1) }
                binding.rcvDriveRequest.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.rcvDriveRequest.adapter = driveRequestAdapter
                driveRequestAdapter.onItemClick = { _, driver, status ->
                    if (status == getString(R.string.accept)) {
                        val fairlist = viewModel.currentState.fairList
                        // viewModel.
                        viewModel.loadData(driver)
                        viewModel.updateRequestStatus(driver.fare_driver_bid_id ?: 0, 1)
                        // fragmentManager?.let { it1 -> bookNowBottomSheet.show(it1, "tag") }

                    } else if (status == getString(R.string.reject)) {
                        viewModel.updateRequestStatus(
                            driver.fare_driver_bid_id ?: 0,
                            2
                        )
                    }
                }
                driveRequestAdapter.onItemClickCard = { driver ->
                    dismiss()
                    if (driver.driver_bid_by != null) {
                        findNavController().navigate(
                            HomeDetailsScreenDirections.actionHomeDetailsScreenToDriverProfileScreen(
                                userid = driver.driver_bid_by
                            )
                        )
                    }
                }
            }
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                DriverRequestSuccessFully -> {
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                }

                UpdateRequestSuccess -> {
                    if (viewModel.currentState.response?.Status == 1) {
                        val fairList = viewModel.currentState.fairList
                        val driveInfo = viewModel.currentState.driver
                        if (fairList != null && driveInfo != null) {
                            findNavController().navigate(
                                HomeDetailsScreenDirections.actionBottomSheetFragmentToBookNowBottomSheet(
                                    fairList = fairList, driverInfo = driveInfo
                                )
                            )
                        } else {

                        }
                    } else {
                        context?.showAlertDialog(
                            title = getString(R.string.Error),
                            message = viewModel.currentState.response?.Message.toString(),
                            cancelable = true,
                            button = getString(R.string.btn_ok)
                        )
                    }
                }
                is DriverRequestFailedEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
                is DriverRequestErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.toString() + " " + event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
            }
        }

//        binding.rcvDriveRequest.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        binding.rcvDriveRequest.adapter = driveRequestAdapter
//        driveRequestAdapter.onItemClick = {
//            // fragmentManager?.let { it1 -> bookNowBottomSheet.show(it1, "tag") }
//            findNavController().navigate(R.id.bookNowBottomSheet)
//        }
//        driveRequestAdapter.onItemClickCard = {
//            dismiss()
//            findNavController().navigate(R.id.driverProfileScreen)
//        }


    }


    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetListener?.onBottomSheetShow(true, 5F)

                }
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    mBottomSheetListener?.onBottomSheetShow(true, 5F)
                }
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetListener?.onBottomSheetShow(false, 1F)

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetListener?.onBottomSheetShow(true, 5F)

                }
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//        if (!CommonUtils.isDarkModeOn(activity))
//            dialog.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        // val view = View.inflate(activity?.applicationContext, R.layout.fragment_book_now_screen, null)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.setCancelable(false)
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
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            bottomSheetBehavior.isHideable = false

        }

        return bottomSheetDialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        findNavController().apply {
            currentBackStackEntry?.savedStateHandle?.set(
                REMOVE_BLUR,
                true
            )
        }
        mBottomSheetListener?.onBottomSheetShow(false, 1F)

        // findNavController().navigate(R.id.homeScreen2)
    }

    companion object {
        var TAG = "ModalBottomSheet"
        fun newInstance() = DriverRequestBottomSheet()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (dialog != null) {
            dialog?.setDismissMessage(null)
        }
    }
}