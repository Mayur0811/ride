package com.bayride.presentation.features.driver.driverOngoing

import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.ongoing.OnGoingViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DriverOnGoingViewModel @Inject constructor() :
    BaseViewModel<DriverOngoingScreenViewState, DriverOnGoingScreenEvent>() {
    override fun initState(): DriverOngoingScreenViewState {
        return DriverOngoingScreenViewState()
    }

    fun loadData(args: DriverOnGoingScreenArgs) {
        dispatchState(currentState.copy(bookingHistoryInfo = args.driverBookingInfo))
        dispatchState(currentState.copy(driverOngoingFairDetails = args.driverOnGoingFairDetails))
    }
}