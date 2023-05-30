package com.bayride.presentation.features.paymentMethod

import android.net.Uri
import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.memory.IMemoryDataStorage
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.signup.SignUpError
import com.bayride.presentation.features.signup.SignUpFailed
import com.bayride.presentation.features.signup.SignUpSuccessEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddPaymentMethodViewModel @Inject constructor(
    val authenticationRepository: AuthenticationRepository,
    val iMemoryDataStorage: IMemoryDataStorage
) :
    BaseViewModel<PaymentMethodViewState, PaymentMethodEvent>() {
    override fun initState(): PaymentMethodViewState {
        return PaymentMethodViewState()
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
                    iMemoryDataStorage.signupDetails?.let { PaymentMethodSuccessEvent(it) }
                        ?.let { dispatchEvent(it) }
                },
                { error ->
                    val errorMessage = BadRequest.parse(error)?.detail
                    val exception = errorMessage
                        ?.let { RuntimeException(errorMessage) }
                        ?: run { error }
                    if (error is HttpException) {
                        val code = error.code()

                        dispatchEvent(PaymentMethodErrorEvent(code, error.message()))
                    } else {
                        dispatchEvent(PaymentMethodFailEvent(exception))
                    }
                },
            )
    }
}