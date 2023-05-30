package com.bayride.presentation.features.driver.otpdialog

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.driver.onGoingScreendialog.OnGoingBottomSheetErrorEvent
import com.bayride.presentation.features.driver.onGoingScreendialog.OnGoingBottomSheetFailedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import retrofit2.http.Field
import javax.inject.Inject

@HiltViewModel
class PickupOtpViewModel @Inject constructor(
    val driverRepository: DriverRepository,
    val secureStorageManager: SecureStorageManager
) :
    BaseViewModel<PickupOtpViewState, PickupOtpEvent>() {
    override fun initState(): PickupOtpViewState {
        return PickupOtpViewState()
    }

    fun verifyPickupOtp(
        pickup_otp: String? = null,
        is_confirmed_by: Int?
    ) {
        dispatchState(currentState.copy(loading = true))
        driverRepository.verifyPickupOtp(secureStorageManager.driverFairId, pickup_otp, is_confirmed_by)
            .applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(response = it))
                dispatchEvent(PickupOtpSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(PickupOtpErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(PickupOtpFailedEvent(exception))
                }
            })
    }
}