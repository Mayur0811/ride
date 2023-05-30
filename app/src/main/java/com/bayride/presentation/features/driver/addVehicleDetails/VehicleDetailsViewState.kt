package com.bayride.presentation.features.driver.addVehicleDetails

import com.bayride.data.datasources.remote.entities.ResponsePassword

data class VehicleDetailsViewState(
    val loading: Boolean? = false,
    val response: ResponsePassword? = null,
    val driverId: Int? = null,
)


