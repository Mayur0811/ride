package com.bayride.presentation.features.driverProfile

import com.bayride.data.datasources.remote.entities.ProfileEntity

data class DriverProfileState(
    val loading: Boolean? = false,
    val profileEntity: ProfileEntity? = null,
    val user_role: Int? = null,
    val driverId: Int? = null
)