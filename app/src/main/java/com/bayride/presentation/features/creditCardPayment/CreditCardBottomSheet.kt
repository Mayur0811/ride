package com.bayride.presentation.features.creditCardPayment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.data.models.dto.DriverDetailsModel
import com.bayride.databinding.FragmentBookNowScreenBinding
import com.bayride.databinding.FragmentCreditCardPaymentScreenBinding
import com.bayride.presentation.features.bookNow.BookNowBottomSheet
import com.bayride.presentation.features.bookNow.BookNowBottomSheetArgs
import com.bayride.presentation.features.bookingSuccessfully.BookingSuccessfullyScreen
import com.bayride.presentation.features.homedetails.adapter.DriveRequestAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreditCardBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCreditCardPaymentScreenBinding
    private val offsetFromTop = 200
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private val radioButtonList: ArrayList<RadioButton> = arrayListOf()
    val viewModel: CreditCardViewModel by viewModels()

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
        binding = FragmentCreditCardPaymentScreenBinding.inflate(inflater)

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
        val args = arguments?.let { CreditCardBottomSheetArgs.fromBundle(it) } ?: run {
            findNavController().popBackStack()
            return
        }

        binding.btnBack.setOnClickListener {
            dialog?.dismiss()
        }
        binding.confirmPayment.setOnClickListener {
            if (args.price != null) {
                viewModel.fairBooked(args.price.toString().toInt(), 2, 1,
                    args.fairDriverBidId?.toInt() ?: 0
                )
            }
        }
        binding.edCardNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                val space = ' '
                if (s.length > 0 && s.length % 5 == 0) {
                    val c = s[s.length - 1]
                    if (space == c) {
                        s.delete(s.length - 1, s.length)
                    }
                }
                if (s.length > 0 && s.length % 5 == 0) {
                    val c = s[s.length - 1]
                    if (Character.isDigit(c) && TextUtils.split(
                            s.toString(),
                            space.toString()
                        ).size <= 3
                    ) {
                        s.insert(s.length - 1, space.toString())
                    }
                }
                binding.cardNumber.text = s.toString()
            }
        })
        binding.edCardHolderName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! < 20) {
                    binding.cardHolderName.text = s.toString()
                }
            }

        })
        binding.edValidThru.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                val slash = '/'
                if (s.length > 0 && s.length % 3 == 0) {
                    val c = s[s.length - 1]
                    if (slash == c) {
                        s.delete(s.length - 1, s.length)
                    }
                }
                if (s.length > 0 && s.length % 3 == 0) {
                    val c = s[s.length - 1]
                    if (Character.isDigit(c) && TextUtils.split(
                            s.toString(),
                            slash.toString()
                        ).size <= 3
                    ) {
                        s.insert(s.length - 1, slash.toString())
                    }
                }
                binding.validThru.text = "Thru " + s.toString()
            }
        })

        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.loading }) {
            if (it == true) binding.progress.visible(true) else binding.progress.visible(false)
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                CreditCardSuccess -> {
                    findNavController().navigate(R.id.bookingSuccessfullyScreen)
                }
                is CreditCardErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.toString() + " " + event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
                is CreditCardFailedEvent -> {
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