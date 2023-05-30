package com.bayride.presentation.homeActivity

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.passenger.PassengerRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.home.HomeScreeErrorEvent
import com.bayride.presentation.features.home.HomeScreeFailedEvent
import com.bayride.presentation.features.home.HomeScreeSuccessEvent
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(val passengerRepository: PassengerRepository) :
    BaseViewModel<HomeActivityViewState, HomeActivityEvent>() {
    override fun initState(): HomeActivityViewState {
        return HomeActivityViewState()
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
                dispatchEvent(HomeActivitySuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(HomeActivityErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(HomeActivityFailEvent(exception))
                }
            })
    }
}