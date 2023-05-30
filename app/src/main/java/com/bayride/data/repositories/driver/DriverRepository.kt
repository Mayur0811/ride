package com.bayride.data.repositories.driver

import com.bayride.data.datasources.remote.entities.BookingHistoryEntity
import com.bayride.data.datasources.remote.entities.ProfileEntity
import com.bayride.data.datasources.remote.entities.ResponsePassword
import com.bayride.data.models.dto.DriverOngoingFairDetails
import com.bayride.data.models.dto.EditDriverOption
import com.bayride.data.models.dto.FairList
import com.bayride.data.models.dto.ListFairUser
import io.reactivex.rxjava3.core.Single
import java.io.File

interface DriverRepository {
    fun addVehicleDetails(
        vehicle_number: String?,
        vehicle_license_number:String?,
        vehicle_year: String?,
        vehicle_model: String?,
        vehicle_colour: String?,
        vehicle_type_id: Int?,
        vehicle_image: File?,
        vehicle_brand: String?
    ): Single<ResponsePassword>

    fun getDriverProfile(userId: Int): Single<ProfileEntity>

    fun getEditDriverOption(
        driver_option_id: Int,
        driver_option_value: String?,
        driver_option_value_file: File?
    ): Single<EditDriverOption>

    fun getFairList(fare_id: Int?): Single<FairList>

    fun sendOfferUser(
        driver_bid_price: Int?,
        fare_id: Int?
    ): Single<ResponsePassword>

    fun getBookingHistoryDriver(): Single<BookingHistoryEntity>

    fun fareBooking(
        fare_id: Int?,
        fare_booking_amount: Int?,
        fare_booking_transaction_id: Int?,
        user_id: Int?,
        payment_method_id: Int?,
        fair_driver_bid_id: Int?
    ): Single<ResponsePassword>


    fun listFairUser(
        new_distance: Int? = null,
        user_lat: Double,
        user_long: Double
    ): Single<ListFairUser>

    fun addBankDetails(
        user_bank_account_no: String?,
        user_bank_account_name: String?,
        user_bank_ifsc_code: String?,
        user_bank_name: String?,
        user_bank_branch: String?
    ): Single<ResponsePassword>

    fun pikUpDoneByDriver(
        fare_id: Int?,
        is_confirmed_by: Int // 1=otp way,2=location way //1 apso to pickup done thai ne otp send thase and 2 apso to only pickup done thase
    ): Single<ResponsePassword>

    fun verifyPickupOtp(
        fair_id: Int?,
        pickup_otp: String? = null,
        is_confirmed_by: Int? //1 apo to pickup_otp params apjo and 2 apo to nhi apta
    ): Single<ResponsePassword>

    fun fairCompletedByDriver(fare_id: Int?): Single<ResponsePassword>

    fun driverOngoingFareDetails(): Single<DriverOngoingFairDetails>

    fun acceptRequestByDriver(fair_id: Int?): Single<ResponsePassword>


}