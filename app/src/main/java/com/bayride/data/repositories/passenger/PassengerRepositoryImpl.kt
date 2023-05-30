package com.bayride.data.repositories.passenger

import com.bayride.data.datasources.remote.PassengerRemoteDataSource
import com.bayride.data.datasources.remote.entities.BookingHistoryEntity
import com.bayride.data.datasources.remote.entities.ResponsePassword
import com.bayride.data.models.dto.AddFairInfo
import com.bayride.data.models.dto.AddFairResponse
import com.bayride.data.models.dto.DriverRequestListModel
import com.bayride.data.models.dto.FairList
import io.reactivex.rxjava3.core.Single
import java.io.File
import javax.inject.Inject

class PassengerRepositoryImpl @Inject constructor(val passengerRemoteDataSource: PassengerRemoteDataSource) :
    PassengerRepository {

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
        return passengerRemoteDataSource.addFare(
            from_address,
            from_city,
            from_country,
            from_lat,
            from_long,
            to_address,
            to_city,
            to_country,
            to_lat,
            to_long,
            fare_cost_by_user,
            payment_method_type,
            fare_bidding_time,
            vehicle_type_id,
            fare_comments,
            fare_image_type,
            fare_image_url
        )
    }

    override fun addSelfieSignature(
        fare_image_type: Int?,
        fare_id: Int?,
        fare_image_url: File?
    ): Single<ResponsePassword> {
        return passengerRemoteDataSource.addSelfieSignature(
            fare_image_type,
            fare_id,
            fare_image_url
        )
    }

    override fun getFairList(fare_id: Int?): Single<FairList> {
        return passengerRemoteDataSource.getFairList(fare_id)
    }

    override fun addReviewToDriver(
        review_to: Int?,
        fare_id: Int?,
        no_of_star: Float?
    ): Single<ResponsePassword> {
        return passengerRemoteDataSource.addReviewToDriver(review_to, fare_id, no_of_star)
    }

    override fun getDriverRequestList(
        fare_id: Int?,
        user_id: Int?
    ): Single<DriverRequestListModel> {
        return passengerRemoteDataSource.getDriverRequestList(fare_id, user_id)
    }


    override fun getBookingHistoryUser(): Single<BookingHistoryEntity> {
        return passengerRemoteDataSource.getBookingHistoryUser()
    }

    override fun CancelRide(
        fare_booking_id: String?,
        booking_text: String
    ): Single<ResponsePassword> {
        return passengerRemoteDataSource.CancelRide(fare_booking_id, booking_text)
    }

    override fun updateRequestStatus(
        fare_driver_bid_id: Int,
        driver_bid_status: Int,
        fair_id: Int
    ): Single<ResponsePassword> {
        return passengerRemoteDataSource.updateRequestStatus(
            fare_driver_bid_id,
            driver_bid_status,
            fair_id
        )
    }

    override fun pikUpDoneByUser(fare_id: Int?, is_confirmed: Int): Single<ResponsePassword> {
        return passengerRemoteDataSource.pikUpDoneByUser(fare_id, is_confirmed)
    }

    override fun fairCompletedByUser(fare_id: Int?, is_confirmed: Int?): Single<ResponsePassword> {
        return passengerRemoteDataSource.fairCompletedByUser(fare_id, is_confirmed)
    }


}