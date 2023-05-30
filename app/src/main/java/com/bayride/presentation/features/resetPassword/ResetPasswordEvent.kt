package com.bayride.presentation.features.resetPassword

import com.bayride.data.datasources.remote.entities.ResponsePassword

sealed class ResetPasswordEvent

data class ResetPasswordSuccessEvent(val responsePassword: ResponsePassword) : ResetPasswordEvent()

data class ResetPasswordErrorEvent(val error: Int) : ResetPasswordEvent()
data class ResetPasswordFailEvent(val error: Throwable) : ResetPasswordEvent()
