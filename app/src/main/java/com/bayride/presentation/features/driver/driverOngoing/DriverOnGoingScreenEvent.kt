package com.bayride.presentation.features.driver.driverOngoing

import com.bayride.data.datasources.remote.entities.SigUpResponse
import com.bayride.presentation.features.driver.bankDetails.BankDetailsEvent

sealed class DriverOnGoingScreenEvent

object DriverOnGoingScreenSuccessEvent : DriverOnGoingScreenEvent()
data class DriverOnGoingScreenFailedEvent(val error: Throwable) : DriverOnGoingScreenEvent()
data class DriverOnGoingScreenErrorEvent(val code: Int, val Message: String) :
    DriverOnGoingScreenEvent()