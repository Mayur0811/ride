package com.bayride.presentation.features.pikupPopup

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.passenger.PassengerRepository
import com.bayride.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class PickUpPopupViewModel @Inject constructor(
    val passengerRepository: PassengerRepository,
    val secureStorageManager: SecureStorageManager
) :
    BaseViewModel<PickUpPopUpViewState, PickUpPopupEvent>() {
    override fun initState(): PickUpPopUpViewState {
        return PickUpPopUpViewState()
    }

    fun pickupDoneByUser(is_confirmed: Int) {
        dispatchState(currentState.copy(loading = true))
        passengerRepository.pikUpDoneByUser(secureStorageManager.fairId, is_confirmed)
            .applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(response = it))
                dispatchEvent(PickUpPopupSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(PickUpPopupErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(PickUpPopupFailEvent(exception))
                }
            })
    }

    fun fairCompleteByUser(is_confirmed: Int) {
        dispatchState(currentState.copy(loading = true))
        passengerRepository.fairCompletedByUser(secureStorageManager.fairId, is_confirmed)
            .applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(response = it))
                dispatchEvent(FairCompleteUserSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(PickUpPopupErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(PickUpPopupFailEvent(exception))
                }
            })
    }
}