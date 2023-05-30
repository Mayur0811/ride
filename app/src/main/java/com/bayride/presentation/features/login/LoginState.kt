package com.bayride.presentation.features.login

import com.bayride.data.datasources.remote.entities.SignInResponse

data class LoginState(
    val loading: Boolean? = false,
    val signInResponse: SignInResponse? = null,
    val user_role: Int? = null
)
