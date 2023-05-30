package com.bayride.presentation.features.changePassword

import com.bayride.data.datasources.remote.entities.ResponsePassword

sealed class ChangePasswordEvent

data class ChangePasswordSuccessEvent(val responsePassword: ResponsePassword) : ChangePasswordEvent()
data class RequestFailedEvent(val error: Throwable) : ChangePasswordEvent()