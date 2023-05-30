package com.bayride.presentation.homeActivity

import com.bayride.data.models.dto.FairList

data class HomeActivityViewState(val loading: Boolean? = false, val fairList: FairList? = null)
