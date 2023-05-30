package com.bayride.presentation.features.driver.acceptCrypto

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AcceptCryptoViewModel @Inject constructor(var authenticationRepository:AuthenticationRepository ):BaseViewModel<AcceptCryptoState,AcceptCryptoEvent>()  {
    override fun initState(): AcceptCryptoState {
        return AcceptCryptoState()
    }

    fun signUpEdit(
        is_crypto: Int
    ) {
        dispatchState(currentState.copy(loading = true))
        authenticationRepository.signUpEdit(
            is_crypto = is_crypto
        ).applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe(
                {
                    dispatchEvent(AcceptCryptoSuccessEvent)
                },
                { error ->
                    val errorMessage = BadRequest.parse(error)?.detail
                    val exception = errorMessage
                        ?.let { RuntimeException(errorMessage) }
                        ?: run { error }
                    if (error is HttpException) {
                        val code = error.code()
                        dispatchEvent(AcceptCryptoErrorEvent(code, error.message()))
                    } else {
                        dispatchEvent(AcceptCryptoFailedEvent(exception))
                    }
                },
            )
    }
}