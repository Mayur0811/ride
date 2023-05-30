package com.bayride.presentation.features.ongoing

import com.bayride.data.datasources.remote.entities.EmergencyContactInfo
import com.bayride.data.datasources.remote.entities.ProfileEntity
import com.bayride.data.datasources.remote.entities.ResponsePassword
import com.bayride.data.models.dto.FairList

data class OnGoingViewState(
    val loading: Boolean? = false,
    val fairList: FairList? = null,
    val fairId: Int? = null,
    val driverbidprice: Int? = null,
    val profile: ProfileEntity? = null,
    val response: ResponsePassword? = null,
    val contactList: List<EmergencyContactInfo>? = null
)