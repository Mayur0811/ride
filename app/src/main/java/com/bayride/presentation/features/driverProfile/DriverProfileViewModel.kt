package com.bayride.presentation.features.driverProfile

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.data.repositories.driver.DriverRepositoryImpl
import com.bayride.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class DriverProfileViewModel @Inject constructor(private var driverRepository: DriverRepository) :
    BaseViewModel<DriverProfileState, DriverProfileEvent>() {

    override fun initState(): DriverProfileState {
        return DriverProfileState()
    }

    fun loadData(args: DriverProfileScreenArgs) {
        dispatchState(currentState.copy(driverId = args.userid))
    }

    fun gertDriverProfile() {
        val userId = currentState.driverId
        dispatchState(currentState.copy(loading = true))
        if (userId != null) {
            driverRepository.getDriverProfile(userId = userId).applyIO()
                .doFinally { dispatchState(currentState.copy(loading = false)) }
                .subscribe({
                    dispatchState(currentState.copy(profileEntity = it))
                    dispatchEvent(DriverProfileSuccessEvent(it))
                }, { error ->
                    val errorMessage = BadRequest.parse(error)?.detail
                    val exception = errorMessage
                        ?.let { RuntimeException(errorMessage) }
                        ?: run { error }
                    if (error is HttpException) {
                        val code = error.code()
                        dispatchEvent(DriverProfileErrorEvent(code, error.message()))
                    } else {
                        dispatchEvent(DriverProfileFailEvent(exception))
                    }
                })
        } else {
            dispatchState(currentState.copy(loading = false))
        }
    }
}