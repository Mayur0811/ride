package com.bayride.presentation.features.driver.addVehicleDetails

sealed class VehicleDetailsEvent

object AddVehicleDetailSuccessFully : VehicleDetailsEvent()

data class AddVehicleDetailsErrorEvent(val error: Int,val Message :String) : VehicleDetailsEvent()
data class AddVehicleDetailsFailedEvent(val error: Throwable) : VehicleDetailsEvent()


