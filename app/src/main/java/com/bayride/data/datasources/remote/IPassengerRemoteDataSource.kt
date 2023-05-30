package com.bayride.data.datasources.remote

import com.bayride.data.datasources.remote.entities.BookingHistoryEntity
import com.bayride.data.datasources.remote.entities.ResponsePassword
import com.bayride.data.models.dto.AddFairInfo
import com.bayride.data.models.dto.AddFairResponse
import com.bayride.data.models.dto.DriverRequestListModel
import com.bayride.data.models.dto.FairList
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import java.io.File

interface IPassengerRemoteDataSource {
    fun addFare(
        from_address: String?,
        from_city: String?,
        from_country: String?,
        from_lat: Double?,
        from_long: Double?,
        to_address: String?,
        to_city: String?,
        to_country: String?,
        to_lat: Double?,
        to_long: Double?,
        fare_cost_by_user: Int?,
        payment_method_type: Int?,
        fare_bidding_time: String?,
        vehicle_type_id: Int?,
        fare_comments: String?,
        fare_image_type: Int?,
        fare_image_url: File?,
    ): Single<AddFairResponse>

    fun addSelfieSignature(
        fare_image_type: Int?,
        fare_id: Int?,
        fare_image_url: File?
    ): Single<ResponsePassword>

    fun getFairList(fare_id: Int?): Single<FairList>

    fun addReviewToDriver(
        review_to: Int?,
        fare_id: Int?,
        no_of_star: Float?
    ): Single<ResponsePassword>

    fun getDriverRequestList(
        fare_id: Int?,
        user_id: Int?
    ): Single<DriverRequestListModel>

    fun getBookingHistoryUser(): Single<BookingHistoryEntity>

    fun CancelRide(
        fare_booking_id: String?,
        booking_text: String
    ): Single<ResponsePassword>

    fun updateRequestStatus(
        fare_driver_bid_id: Int,
        driver_bid_status: Int,
        fair_id: Int
    ): Single<ResponsePassword>

    fun pikUpDoneByUser(
        fare_id: Int?,
        is_confirmed_by: Int // 1=otp way,2=location way //1 apso to pickup done thai ne otp send thase and 2 apso to only pickup done thase
    ): Single<ResponsePassword>


    fun fairCompletedByUser(
        fare_id: Int?,
        is_confirmed: Int?
    ): Single<ResponsePassword>
}