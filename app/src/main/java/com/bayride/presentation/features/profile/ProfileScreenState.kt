package com.bayride.presentation.features.profile

import com.bayride.data.datasources.remote.entities.ProfileEntity

data class ProfileScreenState(
    val loading: Boolean? = false,
    val profileEntity: ProfileEntity?=null
)