package com.bayride.presentation.features.driver.contactInformation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.bayride.R
import com.bayride.common.permission.showPermanentlyDeniedDialog
import com.bayride.common.permission.showRationaleDialog
import com.bayride.common.sharedpreference.saveDriverObjectToSharedPreference
import com.bayride.common.utils.AsteriskPasswordTransformationMethod
import com.bayride.common.utils.Constants
import com.bayride.common.utils.FileUtils
import com.bayride.common.views.*
import com.bayride.data.models.dto.ContactInformation
import com.bayride.data.models.dto.Driver
import com.bayride.data.models.exceptions.AuthenticationException
import com.bayride.data.models.exceptions.EmptyEmailException
import com.bayride.data.models.exceptions.InvalidEmailException
import com.bayride.databinding.ContactInformationScreenBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.countrycodedialog.CountryCodeDialog
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
class ContactInformationScreen : BaseFragment<ContactInformationScreenBinding>(),
    PermissionRequest.Listener {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var fileUri: Uri? = null

    val viewModel: ContactInformationViewModel by viewModels()

    companion object {
        const val CONTACT_INFORMATION = "CONTACT_INFORMATION"
    }


    override fun initData(data: Bundle?) {
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
        // binding.flagEmoji.setImageResource(getFlag("in"))
        binding.countryPicker.setFlagSize(60)
        binding.uploadPhoto.setImageURI(Uri.parse(getContact()?.profile_pic.toString()))
        binding.edUserName.setText(getContact()?.username)
        binding.edName.setText(getContact()?.name)
        binding.edEmail.setText(getContact()?.email)
        binding.edPhoneNo.setText(getContact()?.phone_number)
        binding.edAddress.setText(getContact()?.address)
        binding.edPassword.setText(getContact()?.password)
        getContact()?.country_Code?.let { binding.countryPicker.setCountryForPhoneCode(it.toInt()) }
        request.addListener(this)
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
        safetyClick.setViewClickSafetyListener(binding.showPassword)
        {
            binding.showPassword.hideKeyBoard()
            binding.edPassword.transformationMethod =
                if (binding.edPassword.transformationMethod is AsteriskPasswordTransformationMethod) {
                    binding.showPassword.text = "Hide"
                    null
                } else {
                    binding.showPassword.text = "View"
                    AsteriskPasswordTransformationMethod()
                }
            binding.edPassword.setSelection(binding.edPassword.text.length)
        }
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }
        safetyClick.setViewClickSafetyListener(binding.codePicker) {
            val dialogPopUp =
                CountryCodeDialog()
            activity?.supportFragmentManager.let {
                if (it != null) {
                    dialogPopUp.show(it, "")
                }
            }

            dialogPopUp.setPickUpDialogListener(object : Constants.OnCountryCodeListener {
                override fun countryCode(code: String, flag: String) {
                    // binding.flagEmoji.setImageResource(getFlag(flag))
                }

            })


        }
        safetyClick.setViewClickSafetyListener(binding.btnSave) {
            if (fileUri.toString().isEmpty() || binding.edUserName.text.toString().isEmpty() ||
                binding.edName.text.toString().isEmpty() ||
                binding.edEmail.text.toString().isEmpty() ||
                binding.edPhoneNo.text.toString().isEmpty() ||
                binding.edAddress.text.toString().isEmpty() ||
                binding.edPassword.text.toString().isEmpty()
            ) {
                context?.showAlertDialog(
                    title = getString(R.string.Error),
                    message = getString(R.string.enter_required_field),
                    button = getString(R.string.btn_ok)
                )
                return@setViewClickSafetyListener
            }

            if (!isValidPhoneNumber(binding.edPhoneNo.text.toString())) {
                context?.showAlertDialog(
                    title = getString(R.string.Error),
                    message = "Please enter valid mobile number",
                    button = getString(R.string.btn_ok)
                )
                return@setViewClickSafetyListener
            }


            isAlphaNumeric(binding.edPassword.text.toString().trim()).let {
                if (!it.first) {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = it.second,
                        button = getString(R.string.btn_ok)
                    )
                    return@setViewClickSafetyListener
                }
            }

            if (getContact()?.email == binding.edEmail.text.toString()) {
                initSignUPEdit()
            } else {
                viewModel.signUpEmail(
                    2,
                    binding.edEmail.text.toString().trim(),
                    1,
                )
            }
        }
    }

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.profileImageLayout) {
            request.send()
        }
    }

    override fun initObservers() {

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it == true) showLoading() else hideLoading()
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                is ContactInformationSuccessFullEvent -> {
                    if (event.sigUpResponse?.Status == 1) {
                        findNavController().apply {
                            previousBackStackEntry?.savedStateHandle?.set(
                                CONTACT_INFORMATION,
                                Pair(
                                    CONTACT_INFORMATION, ContactInformation(
                                        fileUri.toString(),
                                        binding.edUserName.text.toString(),
                                        binding.edName.text.toString(),
                                        binding.edEmail.text.toString(),
                                        binding.edPhoneNo.text.toString(),
                                        binding.countryPicker.selectedCountryCode,
                                        binding.edAddress.text.toString(),
                                        binding.edPassword.text.toString()
                                    )
                                )
                            )
                            popBackStack()

                        }

                    } else {
                        context?.showAlertDialog(
                            title = getString(R.string.Error),
                            message = event.sigUpResponse?.Message.toString(),
                            button = getString(R.string.btn_ok)
                        )
                    }
                }
                is ContactInformationSuccessEvent -> {
                    if (event.signUpEmailResponse?.Status == 1) {
                        saveDriverInformation(
                            Driver(
                                ContactInformation(
                                    fileUri.toString(),
                                    binding.edUserName.text.toString(),
                                    binding.edName.text.toString(),
                                    binding.edEmail.text.toString(),
                                    binding.edPhoneNo.text.toString(),
                                    binding.countryPicker.selectedCountryCode,
                                    binding.edAddress.text.toString(),
                                    binding.edPassword.text.toString(),

                                    )
                            ), requireContext()
                        )
                        initSignUPEdit()
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

    private fun initSignUPEdit() {
        val profileImage = if (fileUri == null) {
            FileUtils.getRealPath(requireContext(), Uri.parse(getContact()?.profile_pic))
        } else {
            FileUtils.getRealPath(requireContext(), fileUri)
        }

        if (profileImage == null) {
            viewModel.signUpEdit(
                user_phone_number = binding.edPhoneNo.text.toString(),
                user_name = binding.edUserName.text.toString(),
                user_first_name = binding.edName.text.toString(),
                user_password = binding.edPassword.text.toString(),
                user_address = binding.edAddress.text.toString(),
                country_code = binding.countryPicker.selectedCountryCode

            )
        } else {
            viewModel.signUpEdit(
                user_phone_number = binding.edPhoneNo.text.toString(),
                user_name = binding.edUserName.text.toString(),
                user_first_name = binding.edName.text.toString(),
                user_password = binding.edPassword.text.toString(),
                user_address = binding.edAddress.text.toString(),
                user_profile_pic = File(profileImage),
                country_code = binding.countryPicker.selectedCountryCode
            )
        }

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
                    //Final image size will be less than 1 MB(Optional)
                    .createIntent { intent ->
                        resultLauncher.launch(intent)
                    }
//                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                resultLauncher.launch(cameraIntent)
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }

    fun getContact(): ContactInformation? {
        return getDriver(requireContext())?.contactInformation
    }

    fun saveContactInfo() {
//        saveDriverInformation(Driver(contactInformation = null))
        initSignUPEdit()
    }
}

private fun saveDriverInformation(driver: Driver, context: Context) {
    saveDriverObjectToSharedPreference(
        context = context, Constants.BAYRIDE_DRIVER_MODEL,
        Constants.DRIVER,
        driver,
        true
    )
}
