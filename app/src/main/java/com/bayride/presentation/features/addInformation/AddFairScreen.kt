package com.bayride.presentation.features.addInformation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.permission.showPermanentlyDeniedDialog
import com.bayride.common.permission.showRationaleDialog
import com.bayride.common.utils.FileUtils
import com.bayride.common.views.setTint
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.databinding.AddInformationScreenBinding
import com.bayride.presentation.base.BaseFragment
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddFairScreen : BaseFragment<AddInformationScreenBinding>(),
    AdapterView.OnItemSelectedListener,
    PermissionRequest.Listener {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var biddingTime: String? = null
    val viewModel: AddFairViewModel by viewModels()
    var type: Int = 1
    var paymentMethod: Int = 1
    private var fileUri: Uri? = null

    override fun initData(data: Bundle?) {

    }


    override fun initViews() {
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

        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))

        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }


        binding.usdLayout.setOnClickListener {
            binding.checkboxUsd.isChecked = true
            binding.checkboxCryptocurrency.isChecked = false
            paymentMethod = 1
        }
        binding.layoutCryptoCurrency.setOnClickListener {
            binding.checkboxUsd.isChecked = false
            binding.checkboxCryptocurrency.isChecked = true
            paymentMethod = 2

        }
        binding.checkboxCryptocurrency.setOnCheckedChangeListener { _, p1 ->
            if (p1) {
                paymentMethod = 2
                binding.checkboxUsd.isChecked = false
            }
        }
        binding.checkboxUsd.setOnCheckedChangeListener { _, p1 ->
            if (p1) {
                paymentMethod = 1
                binding.checkboxCryptocurrency.isChecked = false
            }
        }

        val biddingTimeList = arrayOf("5 Min", "10 Min", "15 Min")
        binding.biddingTimeSpinner.onItemSelectedListener = this
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, biddingTimeList)
        binding.biddingTimeSpinner.adapter = arrayAdapter

        safetyClick.setViewClickSafetyListener(binding.sedenLayout) {
            setSectionBackground(binding.sedenLayout)
        }
        safetyClick.setViewClickSafetyListener(binding.carNormalLayout) {
            setSectionBackground(binding.carNormalLayout)
        }
        safetyClick.setViewClickSafetyListener(binding.vanLayout) {
            setSectionBackground(binding.vanLayout)
        }
        safetyClick.setViewClickSafetyListener(binding.xuvLayout) {
            setSectionBackground(binding.xuvLayout)
        }

        safetyClick.setViewClickSafetyListener(binding.termsAndConditions) {
            binding.termsAndConditionsCheckbox.isChecked =
                !binding.termsAndConditionsCheckbox.isChecked
        }
        safetyClick.setViewClickSafetyListener(binding.btnNext) {
            if (binding.termsAndConditionsCheckbox.isChecked && binding.termsAndConditionsCheckbox.isChecked) {
                if (binding.edFromLocationChanged.text.toString()
                        .isEmpty()
                ) {
                    binding.edFromLocationChanged.setText("99460 Clovis Inlet, Hamillstad, MO, SB")
                }

                if (binding.edChangeLocation.text.toString().isEmpty()) {
                    binding.edChangeLocation.setText("507 Lang Well, Lake Eliseo, OH, IE")
                }

                if (binding.edPrice.text.toString()
                        .isEmpty() || biddingTime == null || binding.edComment.text.toString()
                        .isEmpty()
                ) {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = getString(R.string.enter_required_field),
                        button = getString(R.string.btn_ok)
                    )
                    return@setViewClickSafetyListener
                }

                if (fileUri == null) {
                    Toast.makeText(context, "Please choose screenshot", Toast.LENGTH_SHORT).show()
                    return@setViewClickSafetyListener
                }
                //   findNavController().navigate(R.id.uploadSelfieScreen)

                viewModel.addFair(
                    from_address = binding.edFromLocationChanged.text.toString(),
                    "vadodara",
                    "india",
                    to_address = binding.edChangeLocation.text.toString(),
                    to_city = "surat",
                    "india",
                    21.2627787,
                    72.951421,
                    binding.edPrice.text.toString().toInt(),
                    paymentMethod,
                    biddingTime,
                    type,
                    binding.edComment.text.toString().trim(),
                    1,
                    File(FileUtils.getRealPath(requireContext(), fileUri).toString())
                )
            }

//            viewModel.addFair()
            else Toast.makeText(
                requireContext(),
                "Please Accept Terms & Conditions",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.edChangeLocation.onFocusChangeListener =
            View.OnFocusChangeListener { _, _ ->
                binding.edChangeLocation.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.rounded_green_rectangle)
            }

        binding.edFromLocationChanged.onFocusChangeListener =
            View.OnFocusChangeListener { _, _ ->
                binding.edFromLocationChanged.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.rounded_green_rectangle)
            }

        request.addListener(this)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val resultCode = result.resultCode
                val data = result.data
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        fileUri = data?.data
                        binding.uploadPhoto.setImageURI(fileUri)
                        binding.setImageLayout.visible(true)
                        binding.uploadGoogleScreenshot.background = null
                    }
                    ImagePicker.RESULT_ERROR -> {
                        Toast.makeText(
                            requireContext(),
                            ImagePicker.getError(data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.uploadGoogleScreenshot) {
            request.send()
        }
    }

    override fun initObservers() {

        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.loading }) {
            if (it == true) showLoading() else hideLoading()
        }
        viewModel.liveEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                AddFairSuccessEvent -> {
                    findNavController().navigate(R.id.uploadSelfieScreen)
                }
                is AddFairErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.toString() + " " + event.Message,
                        button = getString(R.string.btn_ok)
                    )

                }
                is AddFairFailedEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        button = getString(R.string.btn_ok)
                    )
                }
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        biddingTime = p0?.selectedItem.toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }


    private fun setSectionBackground(view: View) {
        if (view.id != binding.sedenLayout.id) {
            binding.carSedan.setTint(R.color.gray)
            binding.txtSedan.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray))
            binding.sedenLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.round_gray_rectengle)
        } else if (view.id == binding.sedenLayout.id) {
            type = 1
            binding.carSedan.setTint(R.color.vehicle_color)
            binding.txtSedan.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.vehicle_color
                )
            )
            binding.sedenLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.rounded_green_rectangle)
        }

        if (view.id != binding.carNormalLayout.id) {
            binding.carNormal.setTint(R.color.gray)
            binding.txtNormal.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray))
            binding.carNormalLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.round_gray_rectengle)
        } else if (view.id == binding.carNormalLayout.id) {
            type = 2

            binding.carNormal.setTint(R.color.vehicle_color)
            binding.txtNormal.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.vehicle_color
                )
            )
            binding.carNormalLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.rounded_green_rectangle)

        }
        if (view.id != binding.vanLayout.id) {
            binding.van.setTint(R.color.gray)
            binding.txtVan.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray))
            binding.vanLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.round_gray_rectengle)
        } else if (view.id == binding.vanLayout.id) {
            type = 3

            binding.van.setTint(R.color.vehicle_color)
            binding.txtVan.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.vehicle_color
                )
            )
            binding.vanLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.rounded_green_rectangle)

        }

        if (view.id != binding.xuvLayout.id) {
            binding.xuv.setTint(R.color.gray)
            binding.txtXuv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray))
            binding.xuvLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.round_gray_rectengle)
        } else if (view.id == binding.xuvLayout.id) {
            type = 4
            binding.xuv.setTint(R.color.vehicle_color)
            binding.txtXuv.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.vehicle_color
                )
            )
            binding.xuvLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.rounded_green_rectangle)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().navigateUp()
    }

    private val request by lazy {
        permissionsBuilder(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).build()
    }


    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> requireContext().showPermanentlyDeniedDialog(result)
            result.anyShouldShowRationale() -> requireContext().showRationaleDialog(result, request)
            result.allGranted() -> {
                ImagePicker.with(this)
                    .createIntent { intent ->
                        resultLauncher.launch(intent)
                    }
            }
        }
    }

}