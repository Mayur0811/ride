package com.bayride.presentation.features.uploadphoto

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.permission.showPermanentlyDeniedDialog
import com.bayride.common.permission.showRationaleDialog
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.sharedpreference.getSavedObjectFromPreference
import com.bayride.common.utils.Constants
import com.bayride.common.utils.FileUtils
import com.bayride.common.views.getDriver
import com.bayride.common.views.getDriverProFile
import com.bayride.common.views.getPassenger
import com.bayride.common.views.showAlertDialog
import com.bayride.data.models.dto.PassengerSignup
import com.bayride.databinding.FragmentPhotoUploadScreenBinding
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
class PhotoUploadScreen : BaseFragment<FragmentPhotoUploadScreenBinding>(),
    PermissionRequest.Listener {
    companion object {
        const val SELECT_PHOTO_KEY = "SELECT_PHOTO_KEY"
    }

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var fileUri: Uri? = null

    private val viewModel: PhotoUploadViewModel by viewModels()

    override fun initData(data: Bundle?) {
        val args = data?.let { PhotoUploadScreenArgs.fromBundle(data) } ?: run {
            findNavController().popBackStack()
            return
        }
        viewModel.loadData(args)
    }

    override fun initViews() {
        //    ModelPreferencesManager.get<PassengerSignup>(Constants.PASSENGER)

        if (getDriverProFile(requireContext()) != null){

            val imageProfile=Constants.imageDomain+ getDriverProFile(requireContext())?.info?.driver_info?.get(0)?.insurance_info?.get(0)?.driver_option_value
            Glide.with(this).load(imageProfile)
                .into(binding.uploadPhoto)
        }

        val photo = getSavedObjectFromPreference(
            requireContext(),
            Constants.BAYRIDE_PASSENGER_MODEL,
            Constants.PASSENGER,
            PassengerSignup::class.java
        )?.photo
        if (getEncryptedSharedPreferences(requireContext())?.getInt("type", 0) == 1) {
            if (photo != null) {
                fileUri = photo.toUri()
                binding.uploadPhoto.setImageURI(Uri.parse(photo))
            }
        }

        if (getEncryptedSharedPreferences(requireContext())?.getInt("type", 0) == 1) {
            binding.uploadPhoto.setImageURI(Uri.parse(getPassenger(requireContext())?.photo ?: ""))
        } else {
            when (viewModel.currentState.title) {
                getString(R.string.drive_upload_driving_licences) -> {
                    binding.uploadPhoto.setImageURI(
                        Uri.parse(
                            getDriver(requireContext())?.uploadDriverLicense ?: ""
                        )
                    )
                }
                getString(R.string.drive_upload_insurance_card) -> {
                    binding.uploadPhoto.setImageURI(
                        Uri.parse(
                            getDriver(requireContext())?.uploadInsuranceCard ?: ""
                        )
                    )

                }
                getString(R.string.drive_upload_picture_of_your_vehical) -> {
                    binding.uploadPhoto.setImageURI(
                        Uri.parse(
                            getDriver(requireContext())?.vehicle_photo ?: ""
                        )
                    )
                }
            }

        }

        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))
        request.addListener(this)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val resultCode = result.resultCode
                val data = result.data
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        fileUri = data?.data
                        binding.uploadPhoto.setImageURI(fileUri)
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
        safetyClick.setViewClickSafetyListener(binding.btnSave) {
            //startActivity(Intent(requireContext(), HomeActivity::class.java))
            if (fileUri != null) {
                when (viewModel.currentState.title) {
                    getString(R.string.passenger_upload_photo) -> {
                        viewModel.signUpEdit(
                            user_profile_pic = File(
                                FileUtils.getRealPath(requireContext(), fileUri)
                                    .toString()
                            )
                        )
                    }
                    getString(R.string.drive_upload_driving_licences) -> {
                        viewModel.editDriverOption(
                            1,
                            driver_option_value_file = File(fileUri?.let { it1 ->
                                FileUtils.getRealPath(
                                    requireContext(),
                                    it1
                                ).toString()
                            }.toString())
                        )

                    }
                    getString(R.string.drive_upload_insurance_card) -> {
                        viewModel.editDriverOption(
                            5,
                            driver_option_value_file = File(fileUri?.let { it1 ->
                                FileUtils.getRealPath(
                                    requireContext(),
                                    it1
                                ).toString()
                            }.toString())
                        )
                    }
                    else -> {
                        findNavController().apply {
                            previousBackStackEntry?.savedStateHandle?.set(
                                SELECT_PHOTO_KEY,
                                Pair(viewModel.currentState.title, fileUri)
                            )
                            popBackStack()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Please choose photo", Toast.LENGTH_SHORT).show()
            }


        }
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }
        safetyClick.setViewClickSafetyListener(binding.cardview) {
            request.send()
        }
        safetyClick.setViewClickSafetyListener(binding.uploadIcon) {
            request.send()
        }
    }

    override fun initObservers() {
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.title }
        ) {
            binding.titleUploadPhoto.text =
                if (it == "Upload picture of your vehicle") "Upload Vehicle Photo" else it
        }
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.subTitle }
        ) {
            binding.subtitle.text =
                if (it == "Upload picture of your vehicle") "Upload Vehicle Photo" else it
        }

        viewModel.store.observe(
            viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it == true) showLoading() else hideLoading()
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                PhotoUploadSuccessEvent -> {
                    findNavController().apply {
                        previousBackStackEntry?.savedStateHandle?.set(
                            SELECT_PHOTO_KEY,
                            Pair(viewModel.currentState.title, fileUri)
                        )
                        popBackStack()
                    }
                }
                is PhotoUploadError -> {
                    requireContext().showAlertDialog(
                        "Error",
                        event.code.toString() + " " + event.Message,
                        "ok",
                        true,
                        clickListener = { }
                    )
                }
                is PhotoUploadFailed -> {
                    requireContext().showAlertDialog(
                        "Error",
                        event.throwable.message.toString(),
                        "ok",
                        true,
                        clickListener = {
                        })
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
        binding.uploadPhoto.setImageBitmap(bitmap)

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