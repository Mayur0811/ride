package com.bayride.presentation.features.homedetails.driverRequest

import com.bayride.data.datasources.remote.entities.ResponsePassword
import com.bayride.data.models.dto.Driver
import com.bayride.data.models.dto.DriverRequestInfo
import com.bayride.data.models.dto.DriverRequestListModel
import com.bayride.data.models.dto.FairList

data class DriverRequestViewState(
    val loading: Boolean? = false,
    val driverRequestListModel: DriverRequestListModel? = null,
    val fairList: FairList? = null,
    val driver: DriverRequestInfo? = null,
    val response: ResponsePassword? = null,
    val fairId: Int? = null
)
