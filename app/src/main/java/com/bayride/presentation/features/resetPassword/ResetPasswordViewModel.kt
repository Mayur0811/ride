package com.bayride.presentation.features.resetPassword

import android.util.Patterns
import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.memory.IMemoryDataStorage
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.models.exceptions.EmptyEmailException
import com.bayride.data.models.exceptions.InvalidEmailException
import com.bayride.data.models.exceptions.InvalidPasswordNotMatchException
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.forgotPassword.ForgotPasswordErrorEvent
import com.bayride.presentation.features.forgotPassword.ForgotPasswordFailEvent
import com.bayride.presentation.features.forgotPassword.ForgotPasswordSuccessEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    val authenticationRepository: AuthenticationRepository,
    val iMemoryDataStorage: IMemoryDataStorage
) :
    BaseViewModel<ResetPasswordViewState, ResetPasswordEvent>() {

    override fun initState(): ResetPasswordViewState {
        return ResetPasswordViewState(loading = false, resetState = ResetPasswordState.REQUEST)
    }

    fun loadData(args: ResetPasswordScreenArgs) {
        dispatchState(currentState.copy(email = args.email?.trim()))
    }

    fun resetPassword(tempPassword: String, newPassword: String, confirm_password: String) {
        dispatchState(currentState.copy(loading = true))

        if (currentState.email == null) return dispatchEvent(
            ResetPasswordFailEvent(
                EmptyEmailException()
            )
        )

        if (!currentState.email.let { email ->
                Patterns.EMAIL_ADDRESS.matcher(
                    email
                ).matches()
            }
        ) {
            return dispatchEvent(ResetPasswordFailEvent(InvalidEmailException()))
        }

        if (newPassword != confirm_password) return dispatchEvent(
            ResetPasswordFailEvent(
                InvalidPasswordNotMatchException()
            )
        )
        authenticationRepository.resetPassword(
            currentState.email,
            tempPassword,
            newPassword
        ).applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchEvent(ResetPasswordSuccessEvent(it))
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(ResetPasswordErrorEvent(code))
                } else {
                    dispatchEvent(ResetPasswordFailEvent(exception))
                }
            }).addToCompositeDisposable()
    }

    fun forgotPassword(email: String) {
        dispatchState(currentState.copy(loading = true))
        if (email.isEmpty()) return dispatchEvent(ResetPasswordFailEvent(EmptyEmailException()))

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return dispatchEvent(ResetPasswordFailEvent(InvalidEmailException()))
        }

        authenticationRepository.forgotPassword(
            email
        ).applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchEvent(ResetPasswordSuccessEvent(it))
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(ResetPasswordErrorEvent(code))
                } else {
                    dispatchEvent(ResetPasswordFailEvent(exception))
                }
            }).addToCompositeDisposable()
    }
}