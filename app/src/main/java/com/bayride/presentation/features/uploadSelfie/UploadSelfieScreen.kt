package com.bayride.presentation.features.uploadSelfie

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.permission.showPermanentlyDeniedDialog
import com.bayride.common.permission.showRationaleDialog
import com.bayride.common.utils.Constants
import com.bayride.common.utils.FileUtils
import com.bayride.common.utils.getVehicleType
import com.bayride.common.utils.toDateToLocal
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.databinding.UploadselfiescreenBinding
import com.bayride.presentation.base.BaseFragment
import com.bumptech.glide.Glide
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
class UploadSelfieScreen : BaseFragment<UploadselfiescreenBinding>(), PermissionRequest.Listener {
    private val viewModel: UploadSelfieViewModel by viewModels()
    private var fileUri: Uri? = null
    var view: Int = 1
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun initData(data: Bundle?) {
    }

    override fun initViews() {

        activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
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
        request.addListener(this)
        viewModel.getFairList()
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                if (result.resultCode == Activity.RESULT_OK) {
//                    handleCameraImage(result.data)
//                }
                val resultCode = result.resultCode
                val data = result.data
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        //Image Uri will not be null for RESULT_OK
                        fileUri = data?.data
                        binding.setSelfie.setImageURI(fileUri)
                        binding.relative.layoutParams.height =
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        binding.cardViewSelfie.visible(true)
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
        safetyClick.setViewClickSafetyListener(binding.termsAndConditions) {
            binding.termsAndConditionsCheckbox.isChecked =
                !binding.termsAndConditionsCheckbox.isChecked
        }
        safetyClick.setViewClickSafetyListener(binding.btnNext) {
            if (binding.btnNext.text == "Confirm") {

                if (!binding.signatureView.isSignatureEmpty())
                    viewModel.addSelfieSignature(
                        3,
                        sendRequest()?.let { it1 -> File(it1) }
                    )
                else
                    Toast.makeText(context, "Please draw signature", Toast.LENGTH_SHORT).show()
            } else if (binding.termsAndConditionsCheckbox.isChecked && binding.termsAndConditionsCheckbox.isChecked) {
                if (fileUri != null) {
                    viewModel.addSelfieSignature(
                        2,
                        fare_image_url = File(
                            FileUtils.getRealPath(requireContext(), fileUri).toString()
                        )
                    )
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please capture selfie",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please Accept Terms & Conditions",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }

    }

    private fun sendRequest(): String? {
        viewModel.requestSend()
        binding.signatureView.getTransparentSignatureBitmap()
            ?.let { it1 ->
                return FileUtils.saveToInternalStorage(it1, requireContext())
            }
        return null
    }

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.relative) {
            request.send()
        }
    }

    override fun initObservers() {
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it) showLoading() else hideLoading()
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.fairList }
        ) { fair ->
            fair?.Info.let {
                binding.txtDate.text = it?.created_at?.toDateToLocal().toString()
                binding.txtBiddingTime.text = it?.fare_bidding_time.toString()
                binding.vehicleType.text = getVehicleType(it?.vehicle_type_id)
                binding.txtFromLocation.text = it?.from_address
                binding.txtToLocation.text = it?.to_address
                binding.commentDescription.text = it?.fare_comments
                binding.txtPrice.text = "$${it?.fare_cost_by_user}"
                Glide.with(requireContext())
                    .load(Constants.imageDomain + it?.fare_image?.get(0)?.fare_image_url)
                    .into(binding.carSedan)
            }
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                UploadSelfieSuccessEvent -> {
                    when (view) {
                        1 -> {
                            view = 2
                        }
                        2 -> {
                            view = 3
                            binding.txtSignature.visible(true)
                            binding.signatureLayout.visible(true)
                            binding.txtSignatureInstruction.visible(true)
                            binding.btnNext.text = "Confirm"
                        }
                        3 -> {
                            view = 4
                            binding.layoutAlpha.visible(true)
                            binding.requestDialog.visible(true)
                            Handler().postDelayed({
                                findNavController().navigate(R.id.homeDetailsScreen)
                            }, 2000)
                        }
                        else -> {
                            binding.requestDialog.visible(true)
                        }
                    }
                }
                is UploadSelfieErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.toString() + " " + event.Message,
                        button = getString(R.string.btn_ok)
                    )
                }
                is UploadSelfieFailedEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        button = getString(R.string.btn_ok)
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }

    private fun handleCameraImage(intent: Intent?) {
        val bitmap = intent?.extras?.get("data") as Bitmap
        binding.setSelfie.setImageBitmap(bitmap)
        binding.relative.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
        binding.cardViewSelfie.visible(true)

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
            result.anyShouldShowRationale() -> requireContext().showRationaleDialog(
                result,
                request
            )
            result.allGranted() -> {
                ImagePicker.with(this).createIntent { resultLauncher.launch(it) }
//                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                resultLauncher.launch(cameraIntent)
            }
        }
    }

}