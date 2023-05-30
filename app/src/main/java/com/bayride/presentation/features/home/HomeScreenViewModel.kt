package com.bayride.presentation.features.home

import android.content.Context
import com.bayride.common.reactive.applyIO
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.models.dto.DriverOngoingFairDetails
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.data.repositories.passenger.PassengerRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.homedetails.driverRequest.DriverRequestErrorEvent
import com.bayride.presentation.features.homedetails.driverRequest.DriverRequestFailedEvent
import com.bayride.presentation.features.homedetails.driverRequest.DriverRequestSuccessFully
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val passengerRepository: PassengerRepository,
    val secureStorageManager: SecureStorageManager,
    val driverRepository: DriverRepository
) :
    BaseViewModel<HomeScreenViewState, HomeScreenEvent>() {
    override fun initState(): HomeScreenViewState {
        return HomeScreenViewState()
    }

    fun loadFair(context: Context) {
        if (getEncryptedSharedPreferences(context)?.getInt("type", 3) == 1) {
            if (secureStorageManager.fairId == 0) {
                return
            } else {
                getFairList(secureStorageManager.fairId)
            }
        } else {
            dispatchState(currentState.copy(isDriver = true))
        }
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
                dispatchEvent(HomeScreeSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(HomeScreeErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(HomeScreeFailedEvent(exception))
                }
            })
    }

    fun getFairUserList(
        new_distance: Int? = null,
        user_lat: Double?=null,
        user_long: Double?=null
    ) {
        dispatchState(currentState.copy(loading = true))
        driverRepository.listFairUser(new_distance, 0.0, 0.0)
            .applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(fairUserList = it.info))
                dispatchEvent(ListFairUserSuccess)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(HomeScreeErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(HomeScreeFailedEvent(exception))
                }
            })

    }

    fun getDriverOnGoingDetails() {
        dispatchState(currentState.copy(loading = true))
        driverRepository.driverOngoingFareDetails().applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(driverOngoingFairDetails = it))
                secureStorageManager.driverFairId = it.info?.fare_id ?: 0
                dispatchEvent(DriverOnGoingFairDetailsSuccess)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(HomeScreeErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(HomeScreeFailedEvent(exception))
                }
            })
    }


}