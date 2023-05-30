package com.bayride.presentation.features.bookingHistory

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.passenger.PassengerRepository
import com.bayride.data.repositories.passenger.PassengerRepositoryImpl
import com.bayride.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class BookingHistoryViewModel @Inject constructor(var passengerRepository: PassengerRepository) :
    BaseViewModel<BookingHistoryState, BookingHistoryEvent>() {

    override fun initState(): BookingHistoryState {
        return BookingHistoryState()
    }

    fun getBookingHistoryUser() {
        dispatchState(currentState.copy(loading = true))
        passengerRepository.getBookingHistoryUser().applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(bookingHistoryEntity = it))
                dispatchEvent(BookingHistorySuccessEvent(it))
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(BookingHistoryErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(BookingHistoryFailEvent(exception))
                }
            })
    }

}