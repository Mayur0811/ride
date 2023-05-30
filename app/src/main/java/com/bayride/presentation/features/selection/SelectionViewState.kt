package com.bayride.presentation.features.selection

import com.bayride.data.datasources.remote.entities.SelectionScreenInputType

data class SelectionViewState(
    val name: String? = null,
    val fieldTitle: String? = null,
    val hint: String? = null,
    val inputType: SelectionScreenInputType? = null,
    val loading: Boolean? = false
)

