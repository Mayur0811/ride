package com.bayride.presentation.features.home

import com.bayride.presentation.features.giveDriverRating.DriverRatingEvent

sealed class HomeScreenEvent


object HomeScreeSuccessEvent : HomeScreenEvent()
object ListFairUserSuccess : HomeScreenEvent()
object DriverOnGoingFairDetailsSuccess : HomeScreenEvent()

data class HomeScreeErrorEvent(val code: Int, val Message: String) : HomeScreenEvent()
data class HomeScreeFailedEvent(val error: Throwable) : HomeScreenEvent()