package com.bayride.presentation.features.addInformation

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.IPreferenceStorage
import com.bayride.data.datasources.local.ISecureStorageManager
import com.bayride.data.datasources.local.PreferenceStorage
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.passenger.PassengerRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.driver.insuranceInformation.InsurancesInformationErrorEvent
import com.bayride.presentation.features.driver.insuranceInformation.InsurancesInformationFailedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddFairViewModel @Inject constructor(
    val passengerRepository: PassengerRepository,
    val secureStorageManager: SecureStorageManager
) :
    BaseViewModel<AddFairViewState, AddFairEvent>() {
    override fun initState(): AddFairViewState {
        return AddFairViewState()
    }

    fun addFair(
        from_address: String?,
        from_city: String?,
        from_country: String?,
        to_address: String?,
        to_city: String?,
        to_country: String?,
        to_lat: Double?,
        to_long: Double?,
        fare_cost_by_user: Int?,
        payment_method_type: Int?,
        fare_bidding_time: String?,
        vehicle_type_id: Int?,
        fare_comments: String?,
        fare_image_type: Int?,
        fare_image_url: File?,
    ) {
        dispatchState(currentState.copy(loading = true))
        passengerRepository.addFare(
            from_address,
            from_city,
            from_country,
            secureStorageManager.latitude.toDouble(),
            secureStorageManager.longitude.toDouble(),
            to_address,
            to_city,
            to_country,
            to_lat,
            to_long,
            fare_cost_by_user,
            payment_method_type,
            fare_bidding_time,
            vehicle_type_id,
            fare_comments,
            fare_image_type,
            fare_image_url
        ).applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({ info ->
                secureStorageManager.fairId = info.info.fare_id
                dispatchEvent(AddFairSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(AddFairErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(AddFairFailedEvent(exception))
                }
            }).addToCompositeDisposable()
    }

}