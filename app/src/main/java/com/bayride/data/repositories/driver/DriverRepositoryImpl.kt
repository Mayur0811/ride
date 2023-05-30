package com.bayride.data.repositories.driver

import com.bayride.data.datasources.remote.DriverRemoteDataSource
import com.bayride.data.datasources.remote.entities.BookingHistoryEntity
import com.bayride.data.datasources.remote.entities.ProfileEntity
import com.bayride.data.datasources.remote.entities.ResponsePassword
import com.bayride.data.models.dto.DriverOngoingFairDetails
import com.bayride.data.models.dto.EditDriverOption
import com.bayride.data.models.dto.FairList
import com.bayride.data.models.dto.ListFairUser
import io.reactivex.rxjava3.core.Single
import java.io.File
import javax.inject.Inject

class DriverRepositoryImpl @Inject constructor(private val driverRemoteDataSource: DriverRemoteDataSource) :
    DriverRepository {

    override fun addVehicleDetails(
        vehicle_number: String?,
        vehicle_license_number:String?,
        vehicle_year: String?,
        vehicle_model: String?,
        vehicle_colour: String?,
        vehicle_type_id: Int?,
        vehicle_image: File?,
        vehicle_brand: String?
    ): Single<ResponsePassword> {
        return driverRemoteDataSource.addVehicleDetails(
            vehicle_number,
            vehicle_license_number,
            vehicle_year,
            vehicle_model,
            vehicle_colour,
            vehicle_type_id,
            vehicle_image,
            vehicle_brand
        )
    }

    override fun getDriverProfile(userId: Int): Single<ProfileEntity> {
        return driverRemoteDataSource.getDriverProfile(userId)
    }

    override fun getEditDriverOption(
        driver_option_id: Int,
        driver_option_value: String?,
        driver_option_value_file: File?
    ): Single<EditDriverOption> {
        return driverRemoteDataSource.getEditDriverOption(
            driver_option_id,
            driver_option_value,
            driver_option_value_file
        )
    }

    override fun getFairList(fare_id: Int?): Single<FairList> {
        return driverRemoteDataSource.getFairList(fare_id)
    }


    override fun sendOfferUser(driver_bid_price: Int?, fare_id: Int?): Single<ResponsePassword> {
        return driverRemoteDataSource.sendOfferUser(driver_bid_price, fare_id)
    }

    override fun getBookingHistoryDriver(): Single<BookingHistoryEntity> {
        return driverRemoteDataSource.getBookingHistoryDriver()
    }

    override fun fareBooking(
        fare_id: Int?,
        fare_booking_amount: Int?,
        fare_booking_transaction_id: Int?,
        user_id: Int?,
        payment_method_id: Int?,
        fair_driver_bid_id: Int?

    ): Single<ResponsePassword> {
        return driverRemoteDataSource.fareBooking(
            fare_id,
            fare_booking_amount,
            fare_booking_transaction_id,
            user_id,
            payment_method_id,
            fair_driver_bid_id
        )
    }

    override fun listFairUser(
        new_distance: Int?,
        user_lat: Double,
        user_long: Double
    ): Single<ListFairUser> {
        return driverRemoteDataSource.listFairUser(new_distance, user_lat, user_long)
    }

    override fun addBankDetails(
        user_bank_account_no: String?,
        user_bank_account_name: String?,
        user_bank_ifsc_code: String?,
        user_bank_name: String?,
        user_bank_branch: String?
    ): Single<ResponsePassword> {
        return driverRemoteDataSource.addBankDetails(
            user_bank_account_no,
            user_bank_account_name,
            user_bank_ifsc_code,
            user_bank_name,
            user_bank_branch
        )
    }

    override fun pikUpDoneByDriver(fare_id: Int?, is_confirmed_by: Int): Single<ResponsePassword> {
        return driverRemoteDataSource.pikUpDoneByDriver(fare_id, is_confirmed_by)
    }

    override fun verifyPickupOtp(
        fair_id: Int?,
        pickup_otp: String?,
        is_confirmed_by: Int?
    ): Single<ResponsePassword> {
        return driverRemoteDataSource.verifyPickupOtp(fair_id, pickup_otp, is_confirmed_by)
    }

    override fun fairCompletedByDriver(fare_id: Int?): Single<ResponsePassword> {
        return driverRemoteDataSource.fairCompletedByDriver(fare_id)
    }

    override fun driverOngoingFareDetails(): Single<DriverOngoingFairDetails> {
        return driverRemoteDataSource.driverOngoingFareDetails()
    }

    override fun acceptRequestByDriver(fair_id: Int?): Single<ResponsePassword> {
        return driverRemoteDataSource.acceptRequestByDriver(fair_id)
    }

}