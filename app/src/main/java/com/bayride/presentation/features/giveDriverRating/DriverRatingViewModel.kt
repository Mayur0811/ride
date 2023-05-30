package com.bayride.presentation.features.giveDriverRating

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.data.repositories.passenger.PassengerRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.ongoing.OngoingErrorEvent
import com.bayride.presentation.features.ongoing.OngoingFailEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class DriverRatingViewModel @Inject constructor(
    val passengerRepository: PassengerRepository,
    val secureStorageManager: SecureStorageManager,
    val driverRepository: DriverRepository
) :
    BaseViewModel<DriverRatingViewState, DriverRatingEvent>() {
    override fun initState(): DriverRatingViewState {
        return DriverRatingViewState()
    }

    fun addReviewToDriver(
        review_to: Int?,
        no_of_star: Float?
    ) {
        dispatchState(currentState.copy(loading = true))
        passengerRepository.addReviewToDriver(review_to, secureStorageManager.fairId, no_of_star)
            .applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchEvent(DriverRatingSuccessFully)
            }) { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    if (error.code() == 422) {
                        error.response()?.errorBody()?.string()
                            ?.let {
                                DriverRatingErrorEvent(
                                    code,
                                    "Thanks,You have already added review for this fare"
                                )
                            }?.let { dispatchEvent(it) }
                    } else {
                        dispatchEvent(DriverRatingErrorEvent(code, error.message()))

                    }
                } else {
                    dispatchEvent(DriverRatingFailedEvent(exception))
                }
            }
    }

    fun getDriverProfile() {
        dispatchState(currentState.copy(loading = true))
        driverRepository.getDriverProfile(secureStorageManager.driverID).applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(profile = it))
                dispatchEvent(DriverProfileSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(DriverRatingErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(DriverRatingFailedEvent(exception))
                }
            })
    }

}
