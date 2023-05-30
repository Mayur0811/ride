package com.bayride.common.utils

class Constants {
    companion object {
        const val DURATION_SPLASH: Long = 2000
        const val DURATION_LOGIN_SCREEN_ANIMATION: Long = 500
        const val ADD_PAYMENT_METHOD_SCREEN = "addPaymentMethodScreen"
        const val REMOVE_BLUR: String = "RemoveBlur"
        const val UPLOAD_PHOTO = "Upload Photo"
        const val NAME = "Name"
        const val PASSENGER = "passenger"
        const val DRIVER = "driver"
        const val BAYRIDE_PASSENGER_MODEL = "Bayride_passenger_model"
        const val SIGN_UP_DETAILS = "SIGN_UP_DETAILS"
        const val SIGN_UP = "sign_upl"
        const val BAYRIDE_DRIVER_MODEL = "Bayride_driver_model"
        const val DRIVER_BANK_DETAILS = "Driver_bank_details"
        const val LOGIN_DETAILS = "LOGIN_DETAILS"
        const val LOGIN = "LOGIN"
        const val isLogin = "isLogin"
        const val CREATE_USERNAME = "Create Username"
        const val EMAIL = "Email"
        const val PHONE_NUMBER = "Phone Number"
        const val ADDRESS = "Address"
        const val CREATE_PASSWORD = "Create Password"
        const val SIGNATURE = "Signature"
        const val ENTER_PAYMENT_DETAILS = "Enter Payment Details"
        const val CONTACT_INFORMATION = "Contact Information"
        const val VEHICLE_PHOTO = "Upload picture of your vehicle"
        const val VEHICLE_DETAILS = "Vehicle Details"
        const val UPLOAD_DRIVING_LICENCES = "Upload Driving License"
        const val INSURANCE_INFORMATION = "Insurance Information"
        const val UPLOAD_INSURANCE_CARD = "Upload Insurance Card"
        const val ACCEPTS_CRYPTO = "Accepts Crypto"
        const val ACCEPTS_PETS = "Accepts Pets"
        const val BANK_DETAILS = "Bank Details"
        const val PROFILE="Profile"
        val regExp = Regex("[^0-9]")
        val FIREBASE_TOKEN = "FIREBASE_TOKEN"
        var ongoing = false
        var back = false
        var isFromSignUp=false
        const val imageDomain = "http://18.205.157.115/bayride/"

    }

    interface PickupOTPListener {
        fun onDone(otp: String)
    }

    interface PickupPopUpListener {
        fun onYes()
        fun OnNo()
    }

    interface LocationListener {
        fun onOk()
    }

    interface OnCountryCodeListener {
        fun countryCode(code: String, flag: String)
    }

}