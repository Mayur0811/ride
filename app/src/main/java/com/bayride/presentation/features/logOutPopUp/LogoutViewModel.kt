package com.bayride.presentation.features.logOutPopUp

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.bookingHistory.BookingHistoryErrorEvent
import com.bayride.presentation.features.bookingHistory.BookingHistoryFailEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    val secureStorageManager: SecureStorageManager,
    val authenticationRepository: AuthenticationRepository
) : BaseViewModel<LogOutViewState, LogoutEvent>() {
    override fun initState(): LogOutViewState {
        return LogOutViewState()
    }

    fun logout() {
        dispatchState(currentState.copy(loading = true))
        authenticationRepository.logout(secureStorageManager.deviceId)
            .applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchEvent(LogoutSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(LogoutErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(LogoutFailedEvent(exception))
                }
            })
    }
}