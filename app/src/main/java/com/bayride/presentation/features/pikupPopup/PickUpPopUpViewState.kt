package com.bayride.presentation.features.pikupPopup

import com.bayride.data.datasources.remote.entities.ResponsePassword

data class PickUpPopUpViewState(
    val loading: Boolean? = false,
    val response: ResponsePassword? = null
)
