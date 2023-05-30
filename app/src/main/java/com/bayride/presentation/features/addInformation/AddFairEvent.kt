package com.bayride.presentation.features.addInformation

import com.bayride.presentation.features.driver.addVehicleDetails.VehicleDetailsEvent

sealed class AddFairEvent

object AddFairSuccessEvent : AddFairEvent()
data class AddFairErrorEvent(val error: Int, val Message: String) : AddFairEvent()
data class AddFairFailedEvent(val error: Throwable) : AddFairEvent()
