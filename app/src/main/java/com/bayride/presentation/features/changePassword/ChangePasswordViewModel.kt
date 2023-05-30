package com.bayride.presentation.features.changePassword

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.models.exceptions.EmptyPasswordException
import com.bayride.data.models.exceptions.InvalidPasswordNotMatchException
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(val authenticationRepository: AuthenticationRepository) :
    BaseViewModel<ChangePasswordViewState, ChangePasswordEvent>() {
    override fun initState(): ChangePasswordViewState {
        return ChangePasswordViewState()
    }

    fun changePassword(user_password: String, new_password: String, confirm_password: String) {

        if (user_password.isEmpty()) return dispatchEvent(RequestFailedEvent(EmptyPasswordException()))
        if (new_password != confirm_password) return dispatchEvent(
            RequestFailedEvent(
                InvalidPasswordNotMatchException()
            )
        )
        dispatchState(currentState.copy(loading = true))
        authenticationRepository.changePassword(user_password, new_password)
            .applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(responsePassword = it))
                dispatchEvent(ChangePasswordSuccessEvent(it))
            }, { error ->
                dispatchEvent(RequestFailedEvent(parseError(error)))
            }).addToCompositeDisposable()
    }

    private fun parseError(error: Throwable): Throwable {
        val errorMessage = BadRequest.parse(error)?.detail
        return errorMessage
            ?.let { RuntimeException(errorMessage) }
            ?: run { error }
    }
}