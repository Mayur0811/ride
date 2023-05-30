package com.bayride.presentation.features.home

import com.bayride.data.models.dto.DriverOngoingFairDetails
import com.bayride.data.models.dto.FairList
import com.bayride.data.models.dto.ListFairUserInfo

data class HomeScreenViewState(
    val loading: Boolean? = false,
    val fairList: FairList? = null,
    val fairload: Boolean? = false,
    val isDriver: Boolean? = false,
    val fairUserList: List<ListFairUserInfo>? = null,
    val driverOngoingFairDetails: DriverOngoingFairDetails? = null
)