package com.bayride.presentation.features.ongoing

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.ISecureStorageManager
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.data.repositories.passenger.PassengerRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.driverProfile.DriverProfileErrorEvent
import com.bayride.presentation.features.driverProfile.DriverProfileFailEvent
import com.bayride.presentation.features.driverProfile.DriverProfileSuccessEvent
import com.bayride.presentation.features.emergencyContact.AddEmergencyContactErrorEvent
import com.bayride.presentation.features.emergencyContact.AddEmergencyContactFailedEvent
import com.bayride.presentation.features.emergencyContact.AddEmergencyContactSuccessEvent
import com.bayride.presentation.features.homedetails.driverRequest.DriverRequestErrorEvent
import com.bayride.presentation.features.homedetails.driverRequest.DriverRequestFailedEvent
import com.bayride.presentation.features.homedetails.driverRequest.DriverRequestSuccessFully
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class OnGoingViewModel @Inject constructor(
    val passengerRepository: PassengerRepository,
    val secureStorageManager: SecureStorageManager,
    val authenticationRepository: AuthenticationRepository,
    val driverRepository: DriverRepository
) :
    BaseViewModel<OnGoingViewState, OngoingEvent>() {
    override fun initState(): OnGoingViewState {
        return OnGoingViewState()
    }

    init {
        //secureStorageManager.fairId
    }

    fun loadData() {
        dispatchState(currentState.copy(fairId = secureStorageManager.fairId))
        dispatchState(currentState.copy(driverbidprice = secureStorageManager.driverbidprice))
        secureStorageManager.flag = true
    }

    fun getFairList(fare_id: Int?) {
        passengerRepository.getFairList(fare_id = fare_id)
            .applyIO()
            .doFinally { }
            .subscribe({
                secureStorageManager.fairBookingId = it.Info?.fare_booking_unique_id ?: ""
                dispatchState(currentState.copy(fairList = it))
                dispatchEvent(OngoingSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(OngoingErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(OngoingFailEvent(exception))
                }
            })
    }


    fun gertDriverProfile() {
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
                    dispatchEvent(OngoingErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(OngoingFailEvent(exception))
                }
            })
    }


    fun listEmergencyContact() {
        dispatchState(currentState.copy(loading = true))
        authenticationRepository.listEmergencyContact().applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchState(currentState.copy(contactList = it.info))
                dispatchEvent(GetContactSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(OngoingErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(OngoingFailEvent(exception))
                }
            }).addToCompositeDisposable()
    }

    fun pickupDoneByUser(is_confirmed: Int) {
        dispatchState(currentState.copy(loading = true))
        passengerRepository.pikUpDoneByUser(secureStorageManager.fairId, is_confirmed)
            .applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(response = it))

            }, {

            })
    }
}
