package com.bayride.presentation.features.driver.contactInformation

import com.bayride.data.datasources.remote.entities.SigUpResponse
import com.bayride.data.datasources.remote.entities.SignUpEmailResponse

sealed class ContactInformationEvent

data class ContactInformationSuccessEvent(val signUpEmailResponse: SignUpEmailResponse? = null) :
    ContactInformationEvent()

data class ContactInformationSuccessFullEvent(val sigUpResponse: SigUpResponse? =null) : ContactInformationEvent()
data class ContactInformationFailedEvent(val error: Throwable) : ContactInformationEvent()
data class ContactInformationErrorEvent(val code: Int, val Message: String) :
    ContactInformationEvent()