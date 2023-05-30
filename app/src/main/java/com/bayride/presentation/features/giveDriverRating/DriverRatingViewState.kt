package com.bayride.presentation.features.giveDriverRating

import com.bayride.data.datasources.remote.entities.ProfileEntity

data class DriverRatingViewState(val loading: Boolean? = false, val profile: ProfileEntity? = null)