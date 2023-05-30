package com.bayride.presentation.features.resetPassword

data class ResetPasswordViewState(
    val loading: Boolean? = false,
    val email: String? = null,
    val resetState: ResetPasswordState,

)

enum class ResetPasswordState {
    REQUEST
}
