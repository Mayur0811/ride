package com.bayride.presentation.features.bookNow

import com.bayride.data.models.dto.DriverRequestInfo
import com.bayride.data.models.dto.FairList

data class BookNowViewState(
    val loading: Boolean? = false,
    val fairId: Int? = null,
    val fairList: FairList? = null,
    val driverRequestInfo: DriverRequestInfo? = null
)
