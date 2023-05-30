package com.bayride.presentation.features.driver.insuranceInformation

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.driver.DriverRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.uploadphoto.PhotoUploadError
import com.bayride.presentation.features.uploadphoto.PhotoUploadFailed
import com.bayride.presentation.features.uploadphoto.PhotoUploadSuccessEvent
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class InsuranceInformationViewModel @Inject constructor(val driverRepository: DriverRepository) :
    BaseViewModel<InsurancesInformationViewState, InsurancesInformationEvent>() {
    override fun initState(): InsurancesInformationViewState {
        return InsurancesInformationViewState()
    }

    fun EditDriverOption(
        driver_option_id: Int,
        driver_option_value: String? = null,
        driver_option_value_file: File? = null
    ) {
        dispatchState(currentState.copy(loading = true))
        driverRepository.getEditDriverOption(
            driver_option_id,
            driver_option_value,
            driver_option_value_file
        ).applyIO().doFinally {
            dispatchState(currentState.copy(loading = false))
        }.subscribe({
            dispatchEvent(InsurancesInformationSuccessEvent)
        }, { error ->
            val errorMessage = BadRequest.parse(error)?.detail
            val exception = errorMessage
                ?.let { RuntimeException(errorMessage) }
                ?: run { error }
            if (error is HttpException) {
                val code = error.code()
                dispatchEvent(InsurancesInformationErrorEvent(code, error.message()))
            } else {
                dispatchEvent(InsurancesInformationFailedEvent(exception))
            }
        })
    }

}