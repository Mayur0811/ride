package com.bayride.presentation.features.forgotPassword

import android.util.Patterns
import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.models.exceptions.EmptyEmailException
import com.bayride.data.models.exceptions.InvalidEmailException
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(val authenticationRepository: AuthenticationRepository) :
    BaseViewModel<ForgotPasswordViewState, ForgotPasswordEvent>() {

    override fun initState(): ForgotPasswordViewState {
        return ForgotPasswordViewState()
    }

    fun forgotPassword(email: String) {
        dispatchState(currentState.copy(loading = true))
        if (email.isEmpty()) return dispatchEvent(ForgotPasswordFailEvent(EmptyEmailException()))

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return dispatchEvent(ForgotPasswordFailEvent(InvalidEmailException()))
        }

        authenticationRepository.forgotPassword(
            email
        ).applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchEvent(ForgotPasswordSuccessEvent(it))
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(ForgotPasswordErrorEvent(code))
                } else {
                    dispatchEvent(ForgotPasswordFailEvent(exception))
                }
            }).addToCompositeDisposable()
    }
}