package com.bayride.presentation.features.driver.driverBookingHistory

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.data.repositories.driver.DriverRepositoryImpl
import com.bayride.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class DriverBookingHistoryViewModel @Inject constructor(var driverBookingHistory: DriverRepository) :
    BaseViewModel<DriverBookingHistoryState, DriverBookingHistoryEvent>() {
    override fun initState(): DriverBookingHistoryState {
        return DriverBookingHistoryState()
    }

    fun getBookingHistoryDriver() {
        dispatchState(currentState.copy(loading = true))
        driverBookingHistory.getBookingHistoryDriver().applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(bookingHistoryEntity = it))
                dispatchEvent(DriverBookingHistorySuccessEvent(it))
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(DriverBookingHistoryErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(DriverBookingHistoryFailEvent(exception))
                }
            })
    }

}