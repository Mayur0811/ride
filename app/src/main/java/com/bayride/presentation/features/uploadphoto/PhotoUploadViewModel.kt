package com.bayride.presentation.features.uploadphoto

import androidx.navigation.NavArgs
import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.paymentMethod.PaymentMethodErrorEvent
import com.bayride.presentation.features.paymentMethod.PaymentMethodFailEvent
import com.bayride.presentation.features.selection.SelectionErrorEvent
import com.bayride.presentation.features.selection.SelectionFailedEvent
import com.bayride.presentation.features.selection.SelectionSuccessFullEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PhotoUploadViewModel @Inject constructor(
    val driverRepository: DriverRepository,
    val authenticationRepository: AuthenticationRepository
) :
    BaseViewModel<PhotoUploadViewState, PhotoUploadEvent>() {
    override fun initState(): PhotoUploadViewState {
        return PhotoUploadViewState()
    }

    fun loadData(args: PhotoUploadScreenArgs) {
        dispatchState(currentState.copy(title = args.title, subTitle = args.subTitle))
    }

    fun editDriverOption(
        driver_option_id: Int,
        driver_option_value: String? = null,
        driver_option_value_file: File? = null
    ) {
        dispatchState(currentState.copy(loading = true))
        driverRepository.getEditDriverOption(
            driver_option_id,
            driver_option_value,
            driver_option_value_file
        ).applyIO().doFinally {
            dispatchState(currentState.copy(loading = false))
        }.subscribe({
            dispatchEvent(PhotoUploadSuccessEvent)
        }, { error ->
            val errorMessage = BadRequest.parse(error)?.detail
            val exception = errorMessage
                ?.let { RuntimeException(errorMessage) }
                ?: run { error }
            if (error is HttpException) {
                val code = error.code()
                dispatchEvent(PhotoUploadError(code, error.message()))
            } else {
                dispatchEvent(PhotoUploadFailed(exception))
            }
        })
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
        is_crypto: Int? = null
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
            is_crypto
        ).applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe(
                {
//                    iMemoryDataStorage.signupDetails?.let { PaymentMethodSuccessEvent(it) }
//                        ?.let { dispatchEvent(it) }
                    dispatchEvent(PhotoUploadSuccessEvent)
                },
                { error ->
                    val errorMessage = BadRequest.parse(error)?.detail
                    val exception = errorMessage
                        ?.let { RuntimeException(errorMessage) }
                        ?: run { error }
                    if (error is HttpException) {
                        val code = error.code()

                        dispatchEvent(PhotoUploadError(code, error.message()))
                    } else {
                        dispatchEvent(PhotoUploadFailed(exception))
                    }
                },
            )
    }
}