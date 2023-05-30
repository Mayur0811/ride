package com.bayride.common.utils

import android.content.Context
import com.bayride.R
import com.bayride.data.models.dto.CompleteHistoryModel
import com.bayride.data.models.dto.OnGoingHistoryModel
import com.bayride.data.models.dto.Passenger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS


fun separateDate(date: String?): Triple<Int, Int, Int> {
    val current = Calendar.getInstance()
    return date?.split("-")?.let { (day, month, year) ->
        Triple(
            day.toIntOrNull() ?: current.get(Calendar.DATE),
            month.toIntOrNull() ?: current.get(Calendar.MONTH) + 1,
            year.toIntOrNull() ?: current.get(Calendar.YEAR)
        )
    } ?: Triple(
        current.get(Calendar.DATE),
        current.get(Calendar.MONTH) + 1,
        current.get(Calendar.YEAR),
    )
}

fun combineToDate(day: Int, month: Int, year: Int): String =
    String.format("%d-%d-%d", year, month, day)

fun fullDateFormat(): SimpleDateFormat = SimpleDateFormat("MMMM dd, hh:mm:ss", Locale.getDefault())

fun hourFormat(): SimpleDateFormat = SimpleDateFormat("hh:mm", Locale.getDefault())

fun String.parseToDate(): Date? =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN).parse(this)

fun String.toDate(
    dateFormat: String = "yyyy-MM-dd'T'hh:mm",
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun String.toDateToLocal(
    dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss",
    outputDateFormat:String="MMM dd, yyyy, hh:mm a",
    timeZone: TimeZone = TimeZone.getTimeZone("${Calendar.getInstance().timeZone}")
): String? {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    val out=SimpleDateFormat(outputDateFormat,Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)?.let { out.format(it) }
}

fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun Date.getDayTime(): String {
    val isEnglish = Locale.getDefault().language == "en"
    val df = SimpleDateFormat("a", Locale.US)
    // AM or PM
    val dayTime = df.format(this).lowercase()
    return when {
        isEnglish -> " ${df.format(this)}"
        dayTime == "am" -> " قبل‌از‌ظهر "
        else -> " بعدازظهر "
    }
}

fun getListDrive(context: Context): ArrayList<String> {
    return arrayListOf(
        context.resources.getString(R.string.drive_contact_information),
        context.resources.getString(R.string.drive_upload_picture_of_your_vehical),
        context.resources.getString(R.string.drive_Vehicle_details),
        context.resources.getString(R.string.drive_upload_driving_licences),
        context.resources.getString(R.string.drive_insurance_information),
        context.resources.getString(R.string.drive_upload_insurance_card),
        context.resources.getString(R.string.drive_accepts_crypto),
        context.resources.getString(R.string.drive_accepts_pets),
        context.resources.getString(R.string.drive_bank_details),
    )
}

fun getListPassenger(context: Context): ArrayList<Passenger> {
    return arrayListOf(
        Passenger(
            context.resources.getString(R.string.passenger_email),
            "Enter Email",
            "Enter Email"
        ),
        Passenger(
            context.resources.getString(R.string.passenger_upload_photo),
            "Upload Your Photo",
            ""
        ),
        Passenger(context.resources.getString(R.string.passenger_Name), "Enter Name", "Enter Name"),
        Passenger(
            context.resources.getString(R.string.passenger_create_username),
            "Enter Username", "Enter Username"
        ),
        Passenger(
            context.resources.getString(R.string.passenger_phone_number),
            "Enter Phone Number", ""
        ),
        Passenger(
            context.resources.getString(R.string.passenger_address),
            "Enter Address",
            "Enter Address"
        ),
        Passenger(
            context.resources.getString(R.string.passenger_create_password),
            "New Password",
            "New Password"
        ),
        Passenger(context.resources.getString(R.string.passenger_signature), "", ""),
        Passenger(context.resources.getString(R.string.passenger_enter_payment_details), "", ""),
    )
}

fun getOngoingdata(context: Context): ArrayList<OnGoingHistoryModel> {
    return arrayListOf(
        OnGoingHistoryModel(
            0,
            "Timmothy Herzog",
            "+01 1234567890",
            30,
            "#919100",
            "Nov 25, 2021, 09:30 AM",
            "99460 Clovis Inlet, Hamillstad, MO, SB",
            "507 Lang Well, Lake Eliseo, OH, IE"
        ),
        OnGoingHistoryModel(
            0,
            "Timmothy Herzog",
            "+01 1234567890",
            30,
            "#919100",
            "Nov 25, 2021, 09:30 AM",
            "99460 Clovis Inlet, Hamillstad, MO, SB",
            "507 Lang Well, Lake Eliseo, OH, IE"
        ),
    )
}

fun getCompletedData(context: Context): ArrayList<CompleteHistoryModel> {
    return arrayListOf(
        CompleteHistoryModel(
            "Nov 25, 2021, 09:30 AM",
            "Timmothy Herzog",
            3.5F,
            "",
            "507 Lang Well, Lake Eliseo, OH, IE",
            "99460 Clovis Inlet, Hamillstad, MO, SB",
            40
        ),
        CompleteHistoryModel(
            "Nov 25, 2021, 09:30 AM",
            "Timmothy Herzog",
            3.5F,
            "",
            "507 Lang Well, Lake Eliseo, OH, IE",
            "99460 Clovis Inlet, Hamillstad, MO, SB",
            40
        ),
    )
}

fun getVehicleType(type: Int?): String {
    return when (type) {
        1 -> "Sedan"
        2 -> "Normal"
        3 -> "Van"
        4 -> "Xuv"
        else -> ""

    }
}