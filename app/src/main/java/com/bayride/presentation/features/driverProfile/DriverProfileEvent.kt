package com.bayride.presentation.features.driverProfile

import com.bayride.data.datasources.remote.entities.ProfileEntity

sealed class DriverProfileEvent

data class DriverProfileSuccessEvent(var profileEntity: ProfileEntity):DriverProfileEvent()
data class DriverProfileErrorEvent(val errorCode: Int,val errorMessage: String) : DriverProfileEvent()
data class DriverProfileFailEvent(val error: Throwable) : DriverProfileEvent()