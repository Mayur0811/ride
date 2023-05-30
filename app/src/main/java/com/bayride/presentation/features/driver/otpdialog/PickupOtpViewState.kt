package com.bayride.presentation.features.driver.otpdialog

import com.bayride.data.datasources.remote.entities.ResponsePassword

data class PickupOtpViewState(val loading: Boolean? = false, val response: ResponsePassword? = null)