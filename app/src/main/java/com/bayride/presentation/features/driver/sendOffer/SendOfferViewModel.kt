package com.bayride.presentation.features.driver.sendOffer

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.driver.addVehicleDetails.AddVehicleDetailsErrorEvent
import com.bayride.presentation.features.driver.addVehicleDetails.AddVehicleDetailsFailedEvent
import com.bayride.presentation.features.homedetails.driverRequest.DriverRequestErrorEvent
import com.bayride.presentation.features.homedetails.driverRequest.DriverRequestFailedEvent
import com.bayride.presentation.features.homedetails.driverRequest.DriverRequestSuccessFully
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SendOfferViewModel @Inject constructor(
    private val driverRepository: DriverRepository,
    val secureStorageManager: SecureStorageManager
) :
    BaseViewModel<SendOfferViewState, SendOfferEvent>() {
    override fun initState(): SendOfferViewState {
        return SendOfferViewState()
    }

    fun sendOfferUser(driver_bid_price: Int?) {
        dispatchState(currentState.copy(loading = true))
        //secureStorageManager.fairId //this fair id using testing purpose
        driverRepository.sendOfferUser(driver_bid_price, secureStorageManager.fairId)
            .applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchEvent(SendOfferSuccessFully)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(SendOfferErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(SendOfferFailedEvent(exception))
                }
            })
    }

    fun getFairList() {
        dispatchState(currentState.copy(loading = true))

        driverRepository.getFairList(fare_id = secureStorageManager.driverFairId)
            .applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchState(currentState.copy(fairList = it))
                dispatchEvent(FairListSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(SendOfferErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(SendOfferFailedEvent(exception))
                }
            })
    }

    fun acceptRequestByDriver(fair_id: Int) {
        dispatchState(currentState.copy(loading = true))
        driverRepository.acceptRequestByDriver(fair_id)
            .applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchState(currentState.copy(response = it))
                dispatchEvent(AcceptRequestSuccessByDriver)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(SendOfferErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(SendOfferFailedEvent(exception))
                }
            })
    }
}