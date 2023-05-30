package com.bayride.presentation.features.uploadSelfie

import com.bayride.presentation.features.addInformation.AddFairEvent

sealed class UploadSelfieEvent

object UploadSelfieSuccessEvent : UploadSelfieEvent()
data class UploadSelfieErrorEvent(val error: Int, val Message: String) : UploadSelfieEvent()
data class UploadSelfieFailedEvent(val error: Throwable) : UploadSelfieEvent()
