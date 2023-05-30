package com.bayride.presentation.features.driver.sendOffer

import com.bayride.data.datasources.remote.entities.ResponsePassword
import com.bayride.data.models.dto.FairList

data class SendOfferViewState(
    val loading: Boolean? = false,
    val response: ResponsePassword? = null,
    var fairList: FairList? = null
)