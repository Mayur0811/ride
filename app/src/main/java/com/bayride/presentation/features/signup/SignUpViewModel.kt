package com.bayride.presentation.features.signup

import android.net.Uri
import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.memory.IMemoryDataStorage
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.models.dto.PassengerSignup
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    val authenticationRepository: AuthenticationRepository,
    val iMemoryDataStorage: IMemoryDataStorage
) :
    BaseViewModel<SignUpViewState, SignUpEvent>() {
    override fun initState(): SignUpViewState {
        return SignUpViewState()
    }

    fun selectedPhoto(image: Uri) {
        dispatchState(currentState.copy(passenger = PassengerSignup(photo = image.toString())))
    }

    fun signUpEmail(
        user_role: Int,
        email: String,
        device_token: String,
        device_type: Int,
        device_id: String
    ) {
        authenticationRepository.signUpEmail(user_role, email, device_token, device_type, device_id)
            .applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe(
                {
                    iMemoryDataStorage.signupUserDetails?.let { SignUpSuccessEvent(it) }
                        ?.let { dispatchEvent(it) }
                },
                { error ->
                        val errorMessage = BadRequest.parse(error)?.detail
                        val exception = errorMessage
                            ?.let { RuntimeException(errorMessage) }
                            ?: run { error }
                        if (error is HttpException) {
                            val code = error.code()
                            dispatchEvent(SignUpError(code))
                        } else {
                            dispatchEvent(SignUpFailed(exception))
                        }
                },
            )
    }

    fun signUpEdit(
        user_phone_number: String,
        user_name: String,
        user_first_name: String,
        user_address:String?,
        user_password: String,
        user_profile_pic: File?,
        user_lat: Double,
        user_long: Double,
        user_signature: File?,
        is_accept_pets:Int?,
        is_crypto:Int?
    ) {
        authenticationRepository.signUpEdit(
            user_phone_number,
            user_name,
            user_first_name,
            user_password,
            user_address.toString(),
            user_profile_pic,
            user_lat,
            user_long,
            user_signature,
            is_accept_pets,
            is_crypto
        ).applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe(
                { dispatchEvent(SignUpSuccessEvent()) },
                { error ->
                    val errorMessage = BadRequest.parse(error)?.detail
                    val exception = errorMessage
                        ?.let { RuntimeException(errorMessage) }
                        ?: run { error }
                    if (error is HttpException) {
                        val code = error.code()
                        dispatchEvent(SignUpError(code))
                    } else {
                        dispatchEvent(SignUpFailed(exception))
                    }
                },
            )
    }
}