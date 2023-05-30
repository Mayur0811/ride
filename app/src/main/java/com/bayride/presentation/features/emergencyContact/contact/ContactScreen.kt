package com.bayride.presentation.features.emergencyContact.contact

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.permission.showPermanentlyDeniedDialog
import com.bayride.common.permission.showRationaleDialog
import com.bayride.common.utils.FileUtils
import com.bayride.common.views.getPassenger
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.databinding.ContactScreenBinding
import com.bayride.databinding.FragmentAddEmergencyContactBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.emergencyContact.adapter.ContactAdapter
import com.bayride.presentation.features.emergencyContact.adapter.EmergencyContactAdapter
import com.bayride.presentation.features.uploadphoto.PhotoUploadScreen
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
class ContactScreen : BaseFragment<ContactScreenBinding>(), PermissionRequest.Listener {
    companion object {
        const val CONTACT = "CONTACT"
    }

    private val contactViewModel: ContactViewModel by viewModels()
    val contactAdapter = ContactAdapter()

    override fun initData(data: Bundle?) {
    }

    override fun initViews() {
        request.addListener(this)
        request.send()
        contactAdapter.onContactClick = {
            contactViewModel.addEmergencyContact(
                it.name,
                it.mobileNumber,
                null,
                "pic"
            )
        }
    }

    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }
    }

    override fun initObservers() {
        contactViewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.contactList }
        ) {
            if (it != null) {
                contactAdapter.ContactList(it)
                binding.rvContact.adapter = contactAdapter
            }
        }

        contactViewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it == true) binding.progress.visible(true) else binding.progress.visible(false)
        }

        contactViewModel.liveEvent.observe(this) { event ->
            when (event) {
                ContactGetSuccessFully -> {
                    findNavController().apply {
                        previousBackStackEntry?.savedStateHandle?.set(
                            CONTACT,
                            true
                        )
                        popBackStack()
                    }

                }
                is ContactErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.Message,
                        button = getString(R.string.btn_ok),
                        clickListener = {
                            findNavController().popBackStack()
                        }
                    )
                }
                is ContactFailedEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        button = getString(R.string.btn_ok)
                    )
                }
            }
        }
    }

    private val request by lazy {
        permissionsBuilder(Manifest.permission.READ_CONTACTS).build()
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> requireContext().showPermanentlyDeniedDialog(result)
            result.anyShouldShowRationale() -> requireContext().showRationaleDialog(result, request)
            result.allGranted() -> {
                contactViewModel.getContacts(requireContext())
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }
}