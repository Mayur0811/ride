package com.bayride.presentation.features.homedetails.driverRequest

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.models.dto.Driver
import com.bayride.data.models.dto.DriverRequestInfo
import com.bayride.data.repositories.passenger.PassengerRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.giveDriverRating.DriverRatingErrorEvent
import com.bayride.presentation.features.giveDriverRating.DriverRatingFailedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class DriverRequestViewModel @Inject constructor(
    val passengerRepository: PassengerRepository,
    val secureStorageManager: SecureStorageManager
) :
    BaseViewModel<DriverRequestViewState, DriverRequestEvent>() {
    override fun initState(): DriverRequestViewState {
        return DriverRequestViewState()
    }

    init {
        dispatchState(currentState.copy(fairId = secureStorageManager.fairId))
    }

    fun loadData(driver: DriverRequestInfo) {
        dispatchState(currentState.copy(driver = driver))
    }

    fun getDriverList(userId: Int) {
        dispatchState(currentState.copy(loading = true))
        passengerRepository.getDriverRequestList(secureStorageManager.fairId, userId)
            .applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchState(currentState.copy(driverRequestListModel = it))
                dispatchEvent(DriverRequestSuccessFully)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(DriverRequestErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(DriverRequestFailedEvent(exception))
                }
            })
    }

    fun getFairList(fare_id: Int?) {
        dispatchState(currentState.copy(loading = true))

        passengerRepository.getFairList(fare_id = fare_id)
            .applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchState(currentState.copy(fairList = it))
                dispatchEvent(DriverRequestSuccessFully)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(DriverRequestErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(DriverRequestFailedEvent(exception))
                }
            })
    }

    fun updateRequestStatus(
        fare_driver_bid_id: Int,
        driver_bid_status: Int,
    ) {
        dispatchState(currentState.copy(loading = true))
        passengerRepository.updateRequestStatus(
            fare_driver_bid_id,
            driver_bid_status,
            secureStorageManager.fairId
        )
            .applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(response = it))
                dispatchEvent(UpdateRequestSuccess)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(DriverRequestErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(DriverRequestFailedEvent(exception))
                }
            })
    }

}