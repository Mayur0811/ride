package com.bayride.data.datasources.remote

import com.bayride.common.utils.FileUtils.getBitmap
import com.bayride.data.datasources.remote.api.ApiService
import com.bayride.data.datasources.remote.entities.BookingHistoryEntity
import com.bayride.data.datasources.remote.entities.ResponsePassword
import com.bayride.data.models.dto.AddFairInfo
import com.bayride.data.models.dto.AddFairResponse
import com.bayride.data.models.dto.DriverRequestListModel
import com.bayride.data.models.dto.FairList
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class PassengerRemoteDataSource @Inject constructor(val apiService: ApiService) :
    IPassengerRemoteDataSource {

    override fun addFare(
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
        fare_image_url: File?
    ): Single<AddFairResponse> {
        val image_url: RequestBody? = fare_image_url?.path?.let {
            RequestBody.create(
                "image/*".toMediaTypeOrNull(),
                it
            )
        }

        val url: MultipartBody.Part? =
            image_url?.let {
                MultipartBody.Part.createFormData(
                    "fare_image_url",
                    fare_image_url.name,
                    it
                )
            }
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("from_address", from_address.toString())
            .addFormDataPart("from_city", from_city.toString())
            .addFormDataPart("from_country", from_country.toString())
            .addFormDataPart("from_lat", from_lat.toString())
            .addFormDataPart("from_long", from_long.toString())
            .addFormDataPart("to_address", to_address.toString())
            .addFormDataPart("to_city", to_city.toString())
            .addFormDataPart("to_country", to_country.toString())
            .addFormDataPart("to_lat", to_lat.toString())
            .addFormDataPart("to_long", to_long.toString())
            .addFormDataPart("fare_cost_by_user", fare_cost_by_user.toString())
            .addFormDataPart("payment_method_type", payment_method_type.toString())
            .addFormDataPart("fare_bidding_time", fare_bidding_time.toString())
            .addFormDataPart("vehicle_type_id", vehicle_type_id.toString())
            .addFormDataPart("fare_comments", fare_comments.toString())
            .addFormDataPart("fare_image_type", fare_image_type.toString())
            .addFormDataPart(
                "fare_image_url",
                fare_image_url?.name,
                RequestBody.create(MultipartBody.FORM, getBitmap(fare_image_url).toByteArray())
            ).addFormDataPart("no_fare_image_url", "1")

        return apiService.addFare(
            body.build()
        )
    }

    override fun addSelfieSignature(
        fare_image_type: Int?,
        fare_id: Int?,
        fare_image_url: File?
    ): Single<ResponsePassword> {
        val image_url: RequestBody? = fare_image_url?.path?.let {
            RequestBody.create(
                "image/*".toMediaTypeOrNull(),
                it
            )
        }
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("fare_image_type", fare_image_type.toString())
            .addFormDataPart("fare_id", fare_id.toString())
            .addFormDataPart("fare_image_type", fare_image_type.toString())
            .addFormDataPart(
                "fare_image_url",
                fare_image_url?.name,
                RequestBody.create(MultipartBody.FORM, getBitmap(fare_image_url).toByteArray())
            ).addFormDataPart("no_fare_image_url", "1")

        return apiService.addSelfieSignature(body.build())
    }

    override fun getFairList(fare_id: Int?): Single<FairList> {
        return apiService.getFairList(fare_id)
    }

    override fun addReviewToDriver(
        review_to: Int?,
        fare_id: Int?,
        no_of_star: Float?
    ): Single<ResponsePassword> {
        return apiService.addReviewToDriver(review_to, fare_id, no_of_star)
    }

    override fun getDriverRequestList(
        fare_id: Int?,
        user_id: Int?
    ): Single<DriverRequestListModel> {
        return apiService.getDriverRequestList(fare_id, user_id)
    }

    override fun getBookingHistoryUser(): Single<BookingHistoryEntity> {
        return apiService.getBookingHistoryUser()
    }

    override fun CancelRide(
        fare_booking_id: String?,
        booking_text: String
    ): Single<ResponsePassword> {
        return apiService.CancelRide(fare_booking_id, booking_text)
    }

    override fun updateRequestStatus(
        fare_driver_bid_id: Int,
        driver_bid_status: Int,
        fair_id: Int
    ): Single<ResponsePassword> {
        return apiService.updateRequestStatus(fare_driver_bid_id, driver_bid_status, fair_id)
    }

    override fun pikUpDoneByUser(fare_id: Int?, is_confirmed_by: Int): Single<ResponsePassword> {
        return apiService.pikUpDoneByUser(fare_id, is_confirmed_by)
    }

    override fun fairCompletedByUser(fare_id: Int?, is_confirmed: Int?): Single<ResponsePassword> {
        return apiService.fairCompletedByUser(fare_id, is_confirmed)
    }


}