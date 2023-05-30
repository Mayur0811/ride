package com.bayride.data.datasources.remote.entities

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.net.UnknownHostException

@Serializable
data class BadRequest(val detail: String? = null, val code: Int? = null) {
    companion object {
        fun parse(exception: Throwable): BadRequest? {
            return when (exception) {
                is HttpException -> {
                    try {
                        exception.response()?.errorBody()?.string()
                            ?.let { Json.decodeFromString<BadRequest>(it) }
                            ?: run { null }
                    } catch (ex: Exception) {
                        BadRequest(detail = exception.message())
                    }
                }
                is UnknownHostException -> {
                    BadRequest(detail = "Network error. Check your internet connection", )
                }
                else -> null
            }
        }
    }
}