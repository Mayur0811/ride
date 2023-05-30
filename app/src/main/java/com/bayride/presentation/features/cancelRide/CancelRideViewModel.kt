package com.bayride.presentation.features.cancelRide

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.passenger.PassengerRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.bookingHistory.BookingHistoryErrorEvent
import com.bayride.presentation.features.bookingHistory.BookingHistoryFailEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class CancelRideViewModel @Inject constructor(
    val passengerRepository: PassengerRepository,
    val secureStorageManager: SecureStorageManager
) :
    BaseViewModel<CancelRideViewState, CancelRideEvent>() {
    override fun initState(): CancelRideViewState {
        return CancelRideViewState()
    }

    fun cancelRide(booking_text: String) {
        val bookingId = secureStorageManager.fairBookingId
        val id = bookingId.replace("#", "")
        dispatchState(currentState.copy(loading = true))
        passengerRepository.CancelRide(id, booking_text).applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }.subscribe({
                dispatchEvent(CancelRideSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(CancelRideErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(CancelRideFailEvent(exception))
                }
            })
    }
}