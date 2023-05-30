package com.bayride.presentation.features.login

import com.bayride.data.datasources.remote.entities.SignInResponse
import java.lang.Error

sealed class LoginEvent

data class LoginSuccessEvent(val signInResponse: SignInResponse?) : LoginEvent()

data class LoginErrorEvent(val error: Int) : LoginEvent()
data class LoginFailEvent(val error: Throwable) : LoginEvent()
