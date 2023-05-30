package com.bayride.presentation.homeActivity

import com.bayride.presentation.features.bookNow.BookNowEvent

sealed class HomeActivityEvent

object HomeActivitySuccessEvent : HomeActivityEvent()
data class HomeActivityErrorEvent(val Code: Int, val Message: String) : HomeActivityEvent()
data class HomeActivityFailEvent(val error: Throwable) : HomeActivityEvent()
