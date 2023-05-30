package com.bayride.presentation.features.signature

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.sharedpreference.saveModelObjectToSharedPreference
import com.bayride.common.sharedpreference.saveObjectToSharedPreference
import com.bayride.common.utils.Constants
import com.bayride.common.utils.FileUtils.loadImageFromStorage
import com.bayride.common.utils.FileUtils.saveToInternalStorage
import com.bayride.common.views.getLoginDetails
import com.bayride.common.views.getPassenger
import com.bayride.common.views.showAlertDialog
import com.bayride.common.views.visible
import com.bayride.data.models.dto.PassengerSignup
import com.bayride.databinding.SignatureScreenBinding
import com.bayride.presentation.base.BaseFragment
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


@AndroidEntryPoint
class SignatureScreen : BaseFragment<SignatureScreenBinding>() {

    private val viewModel: SignatureViewModel by viewModels()

    companion object {
        const val USER_SIGNATURE = "USER_SIGNATURE"
    }

    override fun initData(data: Bundle?) {

    }

    override fun initViews() {

    }

    override fun initActions() {

        val imageProfile =
            Constants.imageDomain + getLoginDetails(requireActivity())?.info?.user_signature

        loadImageFromStorage(imageProfile)?.let { binding.signatureView.setSignatureBitmap(it) }

        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))
        if (getPassenger(
                requireContext()
            )?.signature != null
        ) {
            loadImageFromStorage(
                getPassenger(
                    requireContext()
                )?.signature.toString()
            )?.let { binding.signatureView.setSignatureBitmap(it) }

        }
        if (getLoginDetails(requireContext())?.info?.user_signature != null) {
            Glide.with(requireContext())
                .load(Constants.imageDomain + getLoginDetails(requireContext())?.info?.user_signature)
                .into(binding.signatureImage)
            binding.signatureImage.visible(true)
        }
        safetyClick.setViewClickSafetyListener(binding.confirmSignature) {
            if (!binding.signatureView.isSignatureEmpty()) {
                binding.signatureView.getSignatureBitmap()
                    ?.let { it1 ->
                        viewModel.signUpEdit(
                            user_signature =
                            saveToInternalStorage(
                                it1, requireContext()
                            )?.let { it2 ->
                                File(
                                    it2
                                )
                            }
                        )
                    }

            } else {
                Toast.makeText(context, "Please draw signature", Toast.LENGTH_SHORT).show()
            }
        }
        safetyClick.setViewClickSafetyListener(binding.btnCancelSignature) {
            // binding.signaturePad.clear()
            binding.signatureView.clear()
            binding.signatureImage.visible(false)
            savePassenger(PassengerSignup(signature = null))
        }
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }

    }

    override fun initObservers() {

        viewModel.store.observe(owner = viewLifecycleOwner, selector = { it.loading }) {
            if (it == true) showLoading() else hideLoading()
        }
        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                is SignatureSuccessEvent -> {
                    if (event.signUpResponse?.Status == 1) {
                        saveModelObjectToSharedPreference(
                            requireContext(),
                            Constants.SIGN_UP_DETAILS,
                            Constants.SIGN_UP,
                            event.signUpResponse
                        )
                        saveModelObjectToSharedPreference(
                            requireContext(),
                            Constants.LOGIN_DETAILS,
                            Constants.LOGIN,
                            event.signUpResponse
                        )
                        getEncryptedSharedPreferences(requireContext())?.edit()
                            ?.putBoolean("isSignup", false)?.apply()
                        findNavController().apply {
                            previousBackStackEntry?.savedStateHandle?.set(USER_SIGNATURE,
                                binding.signatureView.getTransparentSignatureBitmap()
                                    ?.let { it1 ->
                                        saveToInternalStorage(
                                            it1, requireContext()
                                        )
                                    })
                            popBackStack()
                        }
                    }

                }
                is SignatureError -> {
                    requireContext().showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.Message,
                        button = getString(R.string.btn_ok)
                    )
                }
                is SignatureFailed -> {
                    requireContext().showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.throwable.message.toString(),
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

    private fun savePassenger(signup: PassengerSignup) {
        saveObjectToSharedPreference(
            requireContext(),
            Constants.BAYRIDE_PASSENGER_MODEL,
            Constants.PASSENGER,
            signup,
            true,
            signatureClear = true
        )
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}