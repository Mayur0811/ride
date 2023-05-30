package com.bayride.presentation.features.profile

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.driver.addVehicleDetails.AddVehicleDetailsErrorEvent
import com.bayride.presentation.features.driver.addVehicleDetails.AddVehicleDetailsFailedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(private val driverRepository: DriverRepository) :
    BaseViewModel<ProfileScreenState, ProfileScreenEvent>() {
    override fun initState(): ProfileScreenState {
        return ProfileScreenState()
    }

    fun gertDriverProfile(userId: Int?) {
        dispatchState(currentState.copy(loading = true))
        if (userId != null) {
            driverRepository.getDriverProfile(userId = userId).applyIO()
                .doFinally { dispatchState(currentState.copy(loading = false)) }
                .subscribe({
                    dispatchEvent(ProfileScreenSuccessFullyEvent)
                    dispatchState(currentState.copy(profileEntity = it))
                }, { error ->
                    val errorMessage = BadRequest.parse(error)?.detail
                    val exception = errorMessage
                        ?.let { RuntimeException(errorMessage) }
                        ?: run { error }
                    if (error is HttpException) {
                        val code = error.code()
                        dispatchEvent(ProfileScreenErrorEvent(code, error.message()))
                    } else {
                        dispatchEvent(ProfileScreenFailedEvent(exception))
                    }
                })
        } else {
            dispatchState(currentState.copy(loading = false))
        }
    }
}