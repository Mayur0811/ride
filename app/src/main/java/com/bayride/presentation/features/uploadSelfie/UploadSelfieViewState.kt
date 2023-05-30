package com.bayride.presentation.features.uploadSelfie

import com.bayride.data.models.dto.FairList

data class UploadSelfieViewState(val loading: Boolean = false, val fairList: FairList? = null)
