package com.bayride.presentation.features.signup

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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.NavGraphDirections
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.sharedpreference.saveDriverObjectToSharedPreference
import com.bayride.common.sharedpreference.saveObjectToSharedPreference
import com.bayride.common.utils.Constants
import com.bayride.common.utils.getListDrive
import com.bayride.common.utils.getListPassenger
import com.bayride.common.views.showAlertDialog
import com.bayride.data.datasources.remote.entities.SelectionScreenInputType
import com.bayride.data.models.dto.*
import com.bayride.databinding.FragmentSignUpScreenBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.driver.acceptCrypto.AcceptCryptoScreen.Companion.ACCEPT_CRYPTO
import com.bayride.presentation.features.driver.acceptPets.AcceptPetsScreen.Companion.ACCEPT_PETS
import com.bayride.presentation.features.driver.addVehicleDetails.AddVehicleDetailsScreen.Companion.VEHICLE_DETAILS
import com.bayride.presentation.features.driver.contactInformation.ContactInformationScreen.Companion.CONTACT_INFORMATION
import com.bayride.presentation.features.driver.insuranceInformation.InsuranceInformationScreen.Companion.INSURANCE_INFORMATION
import com.bayride.presentation.features.selection.SelectionScreen
import com.bayride.presentation.features.signature.SignatureScreen.Companion.USER_SIGNATURE
import com.bayride.presentation.features.uploadphoto.PhotoUploadScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpScreen : BaseFragment<FragmentSignUpScreenBinding>() {

    companion object {
        private const val PHOTO_UPLOAD_KEY = Constants.UPLOAD_PHOTO
        private const val NAME = Constants.NAME
        private const val CREATE_USERNAME = Constants.CREATE_USERNAME
        private const val EMAIL = Constants.EMAIL
        private const val PHONE_NUMBER = Constants.PHONE_NUMBER
        private const val ADDRESS = Constants.ADDRESS
        private const val CREATE_PASSWORD = Constants.CREATE_PASSWORD
        private const val VEHICLE_PHOTO = Constants.VEHICLE_PHOTO
        private const val UPLOAD_DRIVING_LICENSE = Constants.UPLOAD_DRIVING_LICENCES
        private const val UPLOAD_INSURANCE_CARD = Constants.UPLOAD_INSURANCE_CARD

    }

    private lateinit var adapter: SignUpDetailsAdapter
    val viewModel: SignUpViewModel by viewModels()

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
        adapter = SignUpDetailsAdapter(requireContext())
        context?.let { getListPassenger(it) }?.let { adapter.setDetailsFillList(it) }
        binding.detailsList.adapter = adapter
        getEncryptedSharedPreferences(requireContext())?.edit()?.putInt("type", 1)
            ?.apply()

        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.lightGreen))

        binding.toggle.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.btnPassenger.id -> {
                    getEncryptedSharedPreferences(requireContext())?.edit()?.putInt("type", 1)
                        ?.apply()
                    binding.btnPassenger.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.white
                        )
                    )
                    binding.btnDrive.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.gray
                        )
                    )
                    binding.btnPassenger.background = context?.getDrawable(R.drawable.selected_tab)
                    binding.btnDrive.background = null
                    context?.let { getListPassenger(it) }?.let { adapter.setDetailsFillList(it) }
                    binding.detailsList.adapter = adapter
                    adapter.notifyDataSetChanged()

                }
                binding.btnDrive.id -> {
                    getEncryptedSharedPreferences(requireContext())?.edit()?.putInt("type", 2)
                        ?.apply()

                    binding.btnDrive.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.white
                        )
                    )
                    binding.btnPassenger.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.gray
                        )
                    )
                    binding.btnDrive.background = context?.getDrawable(R.drawable.selected_tab)
                    binding.btnPassenger.background = null
                    context?.let { getListDrive(it) }?.let { adapter.setDetailsFillList(null, it) }
                    binding.detailsList.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
        }

    }

    override fun initActions() {

        adapter.onItemClick = { passenger ->
            if (binding.termsAndConditionsCheckbox.isChecked) {
                when (passenger.title) {
                    resources.getString(R.string.passenger_upload_photo) -> findNavController().navigate(
                        SignUpScreenDirections.actionSignUpScreenToPhotoUploadScreen(
                            title = passenger.title,
                            subTitle = passenger.fieldName
                        )
                    )
                    resources.getString(R.string.passenger_signature) -> findNavController().navigate(
                        SignUpScreenDirections.actionSignUpScreenToSignatureScreen()
                    )
                    resources.getString(R.string.passenger_enter_payment_details) -> {
                        getEncryptedSharedPreferences(requireContext())?.edit()
                            ?.putBoolean(Constants.ADD_PAYMENT_METHOD_SCREEN, true)?.apply()
                        Constants.isFromSignUp = true
                        findNavController().navigate(NavGraphDirections.actionGlobalAddPaymentMethodScreen())
                    }
                    else -> {
                        val inputType =
                            when (passenger.title) {
                                context?.resources?.getString(R.string.passenger_phone_number) -> SelectionScreenInputType.NUMBER
                                context?.resources?.getString(
                                    R.string.passenger_create_password
                                ) -> SelectionScreenInputType.PASSWORD
                                else -> SelectionScreenInputType.TEXT
                            }

                        findNavController().navigate(
                            SignUpScreenDirections.actionSignUpScreenToSelectionScreen(
                                title = passenger.title,
                                fieldTitle = passenger.fieldName,
                                hint = passenger.hint,
                                inputType = inputType
                            )
                        )
                    }
                }
            } else {
                Toast.makeText(context, "Please Accept Terms & Conditions", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        adapter.onItemClickDriver = {
            if (binding.termsAndConditionsCheckbox.isChecked) {
                when (it) {
                    resources.getString(R.string.drive_contact_information) -> {
                        findNavController().navigate(SignUpScreenDirections.actionSignUpScreenToContactInformationScreen())
                    }
                    resources.getString(R.string.drive_Vehicle_details) -> {
                        findNavController().navigate(SignUpScreenDirections.actionSignUpScreenToAddVehicleDetailsScreen())
                    }
                    resources.getString(R.string.drive_insurance_information) -> {
                        findNavController().navigate(SignUpScreenDirections.actionSignUpScreenToInsuranceInformationScreen())
                    }
                    resources.getString(R.string.drive_bank_details) -> {
                        findNavController().navigate(SignUpScreenDirections.actionSignUpScreenToBankDetailsScreen())
                    }
                    resources.getString(R.string.drive_accepts_crypto) -> {
                        findNavController().navigate(SignUpScreenDirections.actionSignUpScreenToAcceptCryptoScreen())
                    }
                    resources.getString(R.string.drive_accepts_pets) -> {
                        findNavController().navigate(SignUpScreenDirections.actionSignUpScreenToAcceptPetsScreen())
                    }
                    else -> {
                        findNavController().navigate(
                            SignUpScreenDirections.actionSignUpScreenToPhotoUploadScreen(
                                title = it,
                                subTitle = it
                            )
                        )
                    }
                }
            } else {
                Toast.makeText(context, "Please Accept Terms & Conditions", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        safetyClick.setViewClickSafetyListener(binding.termsAndConditions) {
            binding.termsAndConditionsCheckbox.isChecked =
                !binding.termsAndConditionsCheckbox.isChecked
        }
    }

    override fun initObservers() {
        val ctx = context ?: return

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                is SignUpSuccessEvent -> {
                    if (event.signUpEmailResponse?.Status == 1) {

                    } else {
                        event.signUpEmailResponse?.Message?.let {
                            requireContext().showAlertDialog(
                                "Error",
                                it,
                                "ok",
                                true,
                                clickListener = {

                                }

                            )
                        }
                    }
                }
                is SignUpError -> {
                    Toast.makeText(context, event.code, Toast.LENGTH_SHORT).show()
                }
                is SignUpFailed -> {
                    requireContext().showAlertDialog(
                        "Error",
                        "dcdscwdcvwsvfe",
                        "ok",
                        true,
                        clickListener = {
                        })
                }
            }

        }

        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<Pair<String, Uri>>(PhotoUploadScreen.SELECT_PHOTO_KEY)
                .observe(viewLifecycleOwner) { (key, imageUri) ->
                    when (key) {
                        PHOTO_UPLOAD_KEY -> {
                            savePassenger(PassengerSignup(photo = imageUri.toString()))
                            viewModel.selectedPhoto(imageUri)
                        }
                        VEHICLE_PHOTO -> {
                            saveDriverInformation(Driver(vehicle_photo = imageUri.toString()))
                        }
                        UPLOAD_DRIVING_LICENSE -> {
                            saveDriverInformation(Driver(uploadDriverLicense = imageUri.toString()))
                        }
                        UPLOAD_INSURANCE_CARD -> {
                            saveDriverInformation(Driver(uploadInsuranceCard = imageUri.toString()))
                        }
                    }
                    remove<Pair<String, Uri>>(PhotoUploadScreen.SELECT_PHOTO_KEY)
                }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<Pair<String, Pair<String, Pair<String, String>>>>(SelectionScreen.SELECTION_OPTION_KEY)
                .observe(viewLifecycleOwner) { (key, value) ->
                    when (key) {
                        NAME -> {
                            savePassenger(PassengerSignup(name = value.second.first))
                        }
                        CREATE_USERNAME -> {
                            savePassenger(PassengerSignup(username = value.second.first))
                        }
                        EMAIL -> {
                            savePassenger(PassengerSignup(email = value.second.first))
                        }
                        PHONE_NUMBER -> {
                            savePassenger(
                                PassengerSignup(
                                    phoneNumber = value.second.first,
                                    country_code = value.first
                                )
                            )
                        }
                        ADDRESS -> {
                            savePassenger(PassengerSignup(address = value.second.first))

                        }
                        CREATE_PASSWORD -> {
                            savePassenger(PassengerSignup(createPassword = value.second))
                        }
                    }
                    remove<Pair<String, String>>(SelectionScreen.SELECTION_OPTION_KEY)
                }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<Pair<String, ContactInformation>>(CONTACT_INFORMATION)
                .observe(viewLifecycleOwner) { (_, contactInformation) ->
                    saveDriverInformation(Driver(contactInformation = contactInformation))
                    remove<ContactInformation>(CONTACT_INFORMATION)
                }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<Pair<String, VehicleDetails>>(VEHICLE_DETAILS)
                .observe(viewLifecycleOwner) { (_, vehicleDetails) ->
                    saveDriverInformation(Driver(vehicleDetails = vehicleDetails))
                    remove<VehicleDetails>(VEHICLE_DETAILS)
                }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<Pair<String, InsuranceInformation>>(INSURANCE_INFORMATION)
                .observe(viewLifecycleOwner) { (_, insuranceInformation) ->
                    saveDriverInformation(Driver(insuranceInformation = insuranceInformation))
                    remove<VehicleDetails>(INSURANCE_INFORMATION)
                }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<Int>(ACCEPT_CRYPTO)
                .observe(viewLifecycleOwner) { acceptCrypto ->
                    saveDriverInformation(Driver(accepts_crypto = acceptCrypto))
                    remove<VehicleDetails>(ACCEPT_CRYPTO)
                }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<Int>(ACCEPT_PETS)
                .observe(viewLifecycleOwner) { acceptPets ->
                    saveDriverInformation(Driver(accepts_pets = acceptPets))
                    remove<VehicleDetails>(ACCEPT_PETS)
                }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<String>(USER_SIGNATURE)
                .observe(viewLifecycleOwner) { signature ->
                    savePassenger(PassengerSignup(signature = signature))
                    remove<VehicleDetails>(USER_SIGNATURE)
                }
        }
    }

    private fun saveDriverInformation(driver: Driver) {
        saveDriverObjectToSharedPreference(
            requireContext(), Constants.BAYRIDE_DRIVER_MODEL,
            Constants.DRIVER,
            driver,
            true
        )
    }

    private fun savePassenger(signup: PassengerSignup) {
        saveObjectToSharedPreference(
            requireContext(),
            Constants.BAYRIDE_PASSENGER_MODEL,
            Constants.PASSENGER,
            signup,
            flag = true,
            signatureClear = false
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()

    }


}