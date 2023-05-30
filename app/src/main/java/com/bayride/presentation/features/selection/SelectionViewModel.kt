package com.bayride.presentation.features.selection

import android.content.Context
import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.ISecureStorageManager
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.memory.IMemoryDataStorage
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.authentication.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.paymentMethod.PaymentMethodErrorEvent
import com.bayride.presentation.features.paymentMethod.PaymentMethodFailEvent
import com.bayride.presentation.features.paymentMethod.PaymentMethodSuccessEvent
import com.bayride.presentation.features.signup.SignUpError
import com.bayride.presentation.features.signup.SignUpFailed
import com.bayride.presentation.features.signup.SignUpSuccessEvent
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor(
    val authenticationRepository: AuthenticationRepository,
    val iMemoryDataStorage: IMemoryDataStorage,
    val secureStorageManager: ISecureStorageManager
) :
    BaseViewModel<SelectionViewState, SelectionEvent>() {
    private var context: Context? = null

    override fun initState(): SelectionViewState {
        return SelectionViewState()
    }

    fun loadData(args: SelectionScreenArgs, ctx: Context) {
        context = ctx
        dispatchState(
            currentState.copy(
                name = args.title,
                fieldTitle = args.fieldTitle,
                hint = args.hint,
                inputType = args.inputType
            )
        )
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
                    iMemoryDataStorage.signupUserDetails?.let { SelectionSuccessEvent(it) }
                        ?.let { dispatchEvent(it) }
                },
                { error ->
                    val errorMessage = BadRequest.parse(error)?.detail
                    val exception = errorMessage
                        ?.let { RuntimeException(errorMessage) }
                        ?: run { error }
                    if (error is HttpException) {
                        val code = error.code()
                        dispatchEvent(SelectionErrorEvent(code, error.message()))
                    } else {
                        dispatchEvent(SelectionFailedEvent(exception))
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
        country_code: String? = null
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
                    dispatchEvent(SelectionSuccessFullEvent)
                },
                { error ->
                    val errorMessage = BadRequest.parse(error)?.detail
                    val exception = errorMessage
                        ?.let { RuntimeException(errorMessage) }
                        ?: run { error }
                    if (error is HttpException) {
                        val code = error.code()

                        dispatchEvent(SelectionErrorEvent(code, error.message()))
                    } else {
                        dispatchEvent(SelectionFailedEvent(exception))
                    }
                },
            )
    }
}