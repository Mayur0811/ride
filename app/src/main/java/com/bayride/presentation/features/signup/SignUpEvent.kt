package com.bayride.presentation.features.signup

import com.bayride.data.datasources.remote.entities.SigUpResponse
import com.bayride.data.datasources.remote.entities.SignUpEmailResponse

sealed class SignUpEvent

data class SignUpSuccessEvent(val signUpEmailResponse: SignUpEmailResponse? =null) : SignUpEvent()

data class SignUpError(val code: Int) : SignUpEvent()
data class SignUpFailed(val throwable: Throwable) : SignUpEvent()
