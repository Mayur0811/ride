package com.bayride.presentation.features.driver.contactInformation

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.ISecureStorageManager
import com.bayride.data.datasources.memory.IMemoryDataStorage
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.driver.acceptPets.AcceptPetsSuccessEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.File
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ContactInformationViewModel @Inject constructor(
    val authenticationRepository: AuthenticationRepository,
    val iMemoryDataStorage: IMemoryDataStorage,
    val secureStorageManager: ISecureStorageManager
) : BaseViewModel<ContactInformationViewState, ContactInformationEvent>() {
    override fun initState(): ContactInformationViewState {
        return ContactInformationViewState()
    }

    fun signUpEmail(
        user_role: Int,
        email: String,
        device_type: Int,
    ) {
        dispatchState(currentState.copy(loading = true))
        authenticationRepository.signUpEmail(
            user_role,
            email,
            secureStorageManager.Device_token,
            device_type,
            secureStorageManager.deviceId
        )
            .applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe(
                {
                    iMemoryDataStorage.signupUserDetails?.let {
                        ContactInformationSuccessEvent(
                            signUpEmailResponse = it
                        )
                    }
                        ?.let { dispatchEvent(it) }
                },
                { error ->
                    val errorMessage = BadRequest.parse(error)?.detail
                    val exception = errorMessage
                        ?.let { RuntimeException(errorMessage) }
                        ?: run { error }
                    if (error is HttpException) {
                        val code = error.code()
                        dispatchEvent(ContactInformationErrorEvent(code, error.message()))
                    } else {
                        dispatchEvent(ContactInformationFailedEvent(exception))
                    }
                },
            )
    }


    fun signUpEdit(
        user_phone_number: String? = null,
        user_name: String? = null,
        user_first_name: String? = null,
        user_password: String? = null,
        user_address: String? = null,
        user_profile_pic: File? = null,
        user_lat: Double? = null,
        user_long: Double? = null,
        user_signature: File? = null,
        is_accept_pets: Int? = null,
        is_crypto: Int? = null,
        country_code:String? =null
    ) {
        dispatchState(currentState.copy(loading = true))
        authenticationRepository.signUpEdit(
            user_phone_number,
            user_name,
            user_first_name,
            user_address,
            user_password,
            user_profile_pic,
            user_lat,
            user_long,
            user_signature,
            is_accept_pets,
            is_crypto,
            country_code
        ).applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe(
                {
                    iMemoryDataStorage.signupDetails?.let { ContactInformationSuccessFullEvent(it) }
                        ?.let { dispatchEvent(it) }
                },
                { error ->
                    val errorMessage = BadRequest.parse(error)?.detail
                    val exception = errorMessage
                        ?.let { RuntimeException(errorMessage) }
                        ?: run { error }
                    if (error is HttpException) {
                        val code = error.code()

                        dispatchEvent(ContactInformationErrorEvent(code, error.message()))
                    } else {
                        dispatchEvent(ContactInformationFailedEvent(exception))
                    }
                },
            )
    }
}