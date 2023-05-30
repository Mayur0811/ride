package com.bayride.presentation.features.driver.onGoingScreendialog

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.datasources.remote.entities.BookingHistoryInfo
import com.bayride.data.models.dto.DriverOngoingFairDetails
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.driver.bankDetails.BankDetailsErrorEvent
import com.bayride.presentation.features.driver.bankDetails.BankDetailsFailedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class OnGoingBottomSheetViewModel @Inject constructor(
    val driverRepository: DriverRepository,
    val secureStorageManager: SecureStorageManager
) :
    BaseViewModel<OnGoingBottomSheetViewState, OnGoingBottomSheetEvent>() {
    override fun initState(): OnGoingBottomSheetViewState {
        return OnGoingBottomSheetViewState()
    }

    fun loadData(
        bookingHistoryInfo: BookingHistoryInfo? = null,
        driverOngoingFairDetails: DriverOngoingFairDetails? = null
    ) {
        dispatchState(currentState.copy(bookingHistoryInfo = bookingHistoryInfo))
        dispatchState(currentState.copy(driverOngoingFairDetails = driverOngoingFairDetails))
        dispatchState(currentState.copy(driverFairId = driverOngoingFairDetails?.info?.fare_id))
    }

    fun pikUpDoneByDriver(is_confirmed_by: Int) {
        dispatchState(currentState.copy(loading = true))
        driverRepository.pikUpDoneByDriver(currentState.driverFairId, is_confirmed_by)
            .applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchEvent(OnGoingBottomSheetSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(OnGoingBottomSheetErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(OnGoingBottomSheetFailedEvent(exception))
                }
            })
    }

    fun pikUpCompleteByDriver() {
        dispatchState(currentState.copy(loading = true))
        driverRepository.fairCompletedByDriver(currentState.driverFairId)
            .applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(response = it))
                dispatchEvent(PickupCompleteByDriverSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(OnGoingBottomSheetErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(OnGoingBottomSheetFailedEvent(exception))
                }
            })
    }
}