package com.bayride.presentation.features.selection

import com.bayride.data.datasources.remote.entities.SignUpEmailResponse

sealed class SelectionEvent

data class SelectionSuccessEvent(val signUpEmailResponse: SignUpEmailResponse) : SelectionEvent()
object SelectionSuccessFullEvent : SelectionEvent()

data class SelectionFailedEvent(val error: Throwable) : SelectionEvent()
data class SelectionErrorEvent(val code: Int, val Message: String):SelectionEvent()

