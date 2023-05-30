package com.bayride.presentation.features.creditCardPayment

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.driver.addVehicleDetails.AddVehicleDetailsErrorEvent
import com.bayride.presentation.features.driver.addVehicleDetails.AddVehicleDetailsFailedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class CreditCardViewModel @Inject constructor(
    val driverRepository: DriverRepository,
    val secureStorageManager: SecureStorageManager
) :
    BaseViewModel<CreditCardViewState, CreditCardEvent>() {
    override fun initState(): CreditCardViewState {
        return CreditCardViewState()
    }

    fun fairBooked(
        fare_booking_amount: Int?,
        fare_booking_transaction_id: Int?,
        payment_method_id: Int?,
        fair_driver_bid_id: Int
    ) {
        secureStorageManager.driverbidprice = fare_booking_amount ?: 0
        dispatchState(currentState.copy(loading = true))
        driverRepository.fareBooking(
            secureStorageManager.fairId,
            fare_booking_amount,
            fare_booking_transaction_id,
            secureStorageManager.driverID,
            payment_method_id,
            fair_driver_bid_id
        ).applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchEvent(CreditCardSuccess)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(CreditCardErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(CreditCardFailedEvent(exception))
                }
            })
    }

}