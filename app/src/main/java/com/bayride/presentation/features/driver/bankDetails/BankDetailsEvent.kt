package com.bayride.presentation.features.driver.bankDetails

import com.bayride.data.datasources.remote.entities.SigUpResponse
import com.bayride.data.models.dto.InsuranceInformation
import com.bayride.presentation.features.driver.insuranceInformation.InsurancesInformationEvent

sealed class BankDetailsEvent

data class BankDetailsSuccessEvent(val sigUpResponse: SigUpResponse? = null) : BankDetailsEvent()
data class BankDetailsFailedEvent(val error: Throwable) : BankDetailsEvent()
data class BankDetailsErrorEvent(val code: Int, val Message: String) : BankDetailsEvent()