package com.bayride.presentation.features.forgotPassword

import com.bayride.data.datasources.remote.entities.ResponsePassword

sealed class ForgotPasswordEvent

data class ForgotPasswordSuccessEvent(val responsePassword: ResponsePassword) : ForgotPasswordEvent()

data class ForgotPasswordErrorEvent(val error: Int) : ForgotPasswordEvent()
data class ForgotPasswordFailEvent(val error: Throwable) : ForgotPasswordEvent()
