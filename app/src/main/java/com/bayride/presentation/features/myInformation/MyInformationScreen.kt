package com.bayride.presentation.features.myInformation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.presentation.homeActivity.HomeActivity
import com.bayride.R
import com.bayride.common.permission.showPermanentlyDeniedDialog
import com.bayride.common.permission.showRationaleDialog
import com.bayride.common.sharedpreference.saveModelObjectToSharedPreference
import com.bayride.common.utils.Constants
import com.bayride.common.utils.FileUtils
import com.bayride.common.views.getLoginDetails
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.data.models.exceptions.AuthenticationException
import com.bayride.data.models.exceptions.EmptyEmailException
import com.bayride.data.models.exceptions.InvalidEmailException
import com.bayride.databinding.FragmentMyInformationScreenBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.driver.contactInformation.*
import com.bumptech.glide.Glide
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderScriptBlur
import java.io.File

@AndroidEntryPoint
class MyInformationScreen : BaseFragment<FragmentMyInformationScreenBinding>(),
    PermissionRequest.Listener {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    val viewModel: ContactInformationViewModel by viewModels()
    private var fileUri: Uri? = null

    override fun initData(data: Bundle?) {
        safetyClick.setViewClickSafetyListener(binding.appCompatImageView) {
            findNavController().popBackStack()
        }
    }

    override fun initViews() {
        val decorView: View? = activity?.window?.decorView
        val rootView = decorView?.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        binding.blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(requireContext()))
            .setBlurRadius(5F)
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))

        val imageProfile =
            Constants.imageDomain + getLoginDetails(requireActivity())?.info?.user_profile_pic
        Glide.with(requireActivity()).load(imageProfile).placeholder(R.drawable.ic_user_avatar)
            .into(binding.uploadPhoto)
        binding.edUserName.setText(getLoginDetails(requireActivity())?.info?.user_name)
        binding.edName.setText(getLoginDetails(requireActivity())?.info?.user_first_name)
        binding.edEmail.setText(getLoginDetails(requireActivity())?.info?.user_email_id)
        binding.edPhoneNo.setText(getLoginDetails(requireActivity())?.info?.user_phone_number)
        binding.edAddress.setText(getLoginDetails(requireActivity())?.info?.user_address)

        request.addListener(this)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val resultCode = result.resultCode
                val data = result.data
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        fileUri = data?.data
                        binding.uploadPhoto.setImageURI(fileUri)
                        binding.blurView.visible(true)
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
        safetyClick.setViewClickSafetyListener(binding.profileImageLayout) {
            request.send()
        }
        safetyClick.setViewClickSafetyListener(binding.btnSave) {
            if (binding.edPhoneNo.text.toString().isEmpty() ||
                binding.edName.text.toString().isEmpty() ||
                binding.edUserName.text.toString().isEmpty() ||
                binding.edEmail.text.toString().isEmpty() ||
                binding.edAddress.text.toString().isEmpty()
            ) {
                context?.showAlertDialog(
                    title = getString(R.string.Error),
                    message = getString(R.string.enter_required_field),
                    button = getString(R.string.btn_ok)
                )
                return@setViewClickSafetyListener
            }
            if (binding.edEmail.text.toString() != getLoginDetails(requireActivity())?.info?.user_email_id) {
                viewModel.signUpEmail(1, binding.edEmail.text.toString(), 1)
            } else {
                editSignUp()
            }

        }
    }

    private val request by lazy {
        permissionsBuilder(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).build()
    }

    private fun editSignUp() {
        if (binding.edUserName.text.toString() == getLoginDetails(requireActivity())?.info?.user_name &&
            binding.edPhoneNo.text.toString() == getLoginDetails(requireActivity())?.info?.user_phone_number
        ) {
            if (fileUri != null) {
                viewModel.signUpEdit(
                    user_first_name = binding.edName.text.toString(),
                    user_address = binding.edAddress.text.toString(),
                    country_code = binding.countryPicker.selectedCountryCode,
                    user_profile_pic = FileUtils.getRealPath(requireActivity(), fileUri)
                        ?.let { File(it) }
                )
            } else {
                viewModel.signUpEdit(
                    user_first_name = binding.edName.text.toString(),
                    user_address = binding.edAddress.text.toString(),
                )
            }
        } else if (binding.edUserName.text.toString() != getLoginDetails(requireActivity())?.info?.user_name &&
            binding.edPhoneNo.text.toString() == getLoginDetails(requireActivity())?.info?.user_phone_number
        ) {
            if (fileUri != null) {
                viewModel.signUpEdit(
                    user_first_name = binding.edName.text.toString(),
                    user_address = binding.edAddress.text.toString(),
                    user_name = binding.edUserName.text.toString(),
                    user_profile_pic = FileUtils.getRealPath(requireActivity(), fileUri)
                        ?.let { File(it) }
                )
            } else {
                viewModel.signUpEdit(
                    user_first_name = binding.edName.text.toString(),
                    user_address = binding.edAddress.text.toString(),
                    user_name = binding.edUserName.text.toString(),
                )
            }
        } else if (binding.edUserName.text.toString() == getLoginDetails(requireActivity())?.info?.user_name &&
            binding.edPhoneNo.text.toString() != getLoginDetails(requireActivity())?.info?.user_phone_number
        ) {
            if (fileUri != null) {
                viewModel.signUpEdit(
                    user_phone_number = binding.edPhoneNo.text.toString(),
                    user_address = binding.edAddress.text.toString(),
                    user_first_name = binding.edName.text.toString(),
                    country_code = binding.countryPicker.selectedCountryCode,
                    user_profile_pic = FileUtils.getRealPath(requireActivity(), fileUri)
                        ?.let { File(it) }
                )
            } else {
                viewModel.signUpEdit(
                    user_phone_number = binding.edPhoneNo.text.toString(),
                    user_address = binding.edAddress.text.toString(),
                    country_code = binding.countryPicker.selectedCountryCode,
                    user_first_name = binding.edName.text.toString()
                )
            }
        } else {
            if (fileUri != null) {
                viewModel.signUpEdit(
                    user_first_name = binding.edName.text.toString(),
                    user_address = binding.edAddress.text.toString(),
                    user_name = binding.edUserName.text.toString(),
                    user_phone_number = binding.edPhoneNo.text.toString(),
                    country_code = binding.countryPicker.selectedCountryCode,
                    user_profile_pic = FileUtils.getRealPath(requireActivity(), fileUri)
                        ?.let { File(it) }
                )
            } else {
                viewModel.signUpEdit(
                    user_first_name = binding.edName.text.toString(),
                    user_address = binding.edAddress.text.toString(),
                    country_code = binding.countryPicker.selectedCountryCode,
                    user_name = binding.edUserName.text.toString(),
                    user_phone_number = binding.edPhoneNo.text.toString()
                )
            }
        }
    }

    override fun initObservers() {
        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.loading }) {
            if (it == true) showLoading() else hideLoading()
        }
        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                is ContactInformationSuccessFullEvent -> {
                    if (event.sigUpResponse?.Status == 1) {
                        saveModelObjectToSharedPreference(
                            requireContext(),
                            Constants.SIGN_UP_DETAILS,
                            Constants.SIGN_UP,
                            event.sigUpResponse
                        )
                        saveModelObjectToSharedPreference(
                            requireContext(),
                            Constants.LOGIN_DETAILS,
                            Constants.LOGIN,
                            event.sigUpResponse
                        )

                        findNavController().popBackStack()

                    } else {
                        event.sigUpResponse?.Message?.let { it1 ->
                            requireContext().showAlertDialog(
                                "Error",
                                it1,
                                "ok",
                                true,
                                clickListener = {

                                }
                            )
                        }
                    }
                }
                is ContactInformationSuccessEvent -> {
                    if (event.signUpEmailResponse?.Status == 1) {
                        if (event.signUpEmailResponse.Status == 1) {
                            if (binding.edUserName.text.toString() == getLoginDetails(
                                    requireActivity()
                                )?.info?.user_name &&
                                binding.edPhoneNo.text.toString() == getLoginDetails(requireActivity())?.info?.user_phone_number &&
                                binding.edName.text.toString() == getLoginDetails(requireActivity())?.info?.user_first_name &&
                                binding.edAddress.text.toString() == getLoginDetails(requireActivity())?.info?.user_address
                            ) {
                                findNavController().popBackStack()
                            } else {
                                editSignUp()
                            }
                        }
                    } else {
                        context?.showAlertDialog(
                            title = getString(R.string.Error),
                            message = event.signUpEmailResponse?.Message.toString(),
                            button = getString(R.string.btn_ok)
                        )
                    }
                }
                is ContactInformationErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.Message,
                        button = getString(R.string.btn_ok)
                    )
                }
                is ContactInformationFailedEvent -> {
                    when (event.error as? AuthenticationException) {
                        is EmptyEmailException -> {
                            requireContext().showAlertDialog(
                                title = getString(R.string.Error),
                                message = getString(R.string.enter_required_field),
                                button = getString(R.string.btn_ok)
                            )
                        }
                        is InvalidEmailException -> {
                            requireContext().showAlertDialog(
                                title = getString(R.string.Error),
                                message = "Enter valid Email",
                                button = getString(R.string.btn_ok)
                            )
                        }
                        else -> {
                            requireContext().showAlertDialog(
                                title = getString(R.string.Error),
                                message = event.error.message.toString(),
                                button = getString(R.string.btn_ok)
                            )
                        }
                    }
                }
            }

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> requireContext().showPermanentlyDeniedDialog(result)
            result.anyShouldShowRationale() -> requireContext().showRationaleDialog(result, request)
            result.allGranted() -> {
                ImagePicker.with(this)
                    //Final image size will be less than 1 MB(Optional)
                    .createIntent { intent ->
                        resultLauncher.launch(intent)
                    }
//                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                resultLauncher.launch(cameraIntent)
            }
        }
    }

    private fun handleCameraImage(intent: Intent?) {
        val bitmap = intent?.extras?.get("data") as Bitmap
        binding.uploadPhoto.setImageBitmap(bitmap)

    }
}