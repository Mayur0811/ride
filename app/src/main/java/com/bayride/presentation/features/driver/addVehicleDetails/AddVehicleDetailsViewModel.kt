package com.bayride.presentation.features.driver.addVehicleDetails

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddVehicleDetailsViewModel @Inject constructor(private val driverRepository: DriverRepository) :
    BaseViewModel<VehicleDetailsViewState, VehicleDetailsEvent>() {
    override fun initState(): VehicleDetailsViewState {
        return VehicleDetailsViewState()
    }

    fun loadData(id: Int) {
        dispatchState(currentState.copy(driverId = id))
    }

    fun addVehicleDetails(
        vehicle_number: String?,
        vehicle_license_number:String?,
        vehicle_year: String?,
        vehicle_model: String?,
        vehicle_colour: String?,
        vehicle_type_id: Int?,
        vehicle_image: File?,
        vehicle_brand: String?
    ) {
        dispatchState(currentState.copy(loading = true))
        driverRepository.addVehicleDetails(
            vehicle_number,
            vehicle_license_number,
            vehicle_year,
            vehicle_model,
            vehicle_colour,
            vehicle_type_id,
            vehicle_image,
            vehicle_brand
        ).applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                dispatchState(currentState.copy(response = it))
                dispatchEvent(AddVehicleDetailSuccessFully)
            }, { error ->
                    val errorMessage = BadRequest.parse(error)?.detail
                    val exception = errorMessage
                        ?.let { RuntimeException(errorMessage) }
                        ?: run { error }
                    if (error is HttpException) {
                        val code = error.code()
                        dispatchEvent(AddVehicleDetailsErrorEvent(code, error.message()))
                    } else {
                        dispatchEvent(AddVehicleDetailsFailedEvent(exception))
                    }
            })
    }

}