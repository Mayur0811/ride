package com.bayride.data.datasources.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.bayride.StorageConfig
import com.bayride.common.sharedpreference.BooleanPreferenceDelegate
import com.bayride.common.sharedpreference.IntPreferenceDelegate
import com.bayride.common.sharedpreference.LongPreferenceDelegate
import com.bayride.common.sharedpreference.StringPreferenceDelegate

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ISecureStorageManager {
    var token: String

    var userID: Int

    var userEmail: String

    var language: String

    var user_role: Int

    var Device_token: String

    var deviceId: String

    var fairId: Int

    var driverID: Int

    var fairBookingId: String

    var latitude: String

    var longitude: String

    var driverbidprice: Int

    var driverFairId: Int

    var flag: Boolean
    fun clearAll()
}

class SecureStorageManager @Inject constructor(
    @ApplicationContext context: Context
) : ISecureStorageManager {
    //    val mAead = getOrGenerateNewKeysetHandle().getPrimitive(Aead::class.java)
    companion object {
        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val USER_ID_KEY = "USER_ID_KEY"
        private const val USER_ROLE_KEY = "USER_ROLE_KEY"
        private const val USER_EMAIL_KEY = "USER_EMAIL_KEY"
        private const val DEVICE_TOKEN = "DEVICE_TOKEN"
        private const val DEVICE_ID = "DEVICE_ID"
        private const val FAIR_ID = "FAIR_ID"
        private const val LANGUAGE = "language"
        private const val DRIVER_ID = "DRIVER_ID"
        private const val FAIR_BOOKING_ID = "FAIR_BOOKING_ID"
        private const val LATITUDE = "LATITUDE"
        private const val LONGITUDE = "LONGITUDE"
        private const val DRIVER_BID_PRICE = "DRIVER_BID_PRICE"
        private const val DRIVER_FAIR_ID = "DRIVER_FAIR_ID"
        private const val FLAG = "FLAG"
    }

    private val masterKey: MasterKey =
        MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    private val encryptedPrefs: SharedPreferences =
        context.getSharedPreferences(StorageConfig.SECURE_SHARED_PREFERENCES_FILE_NAME, 0)
//        EncryptedSharedPreferences.create(
//            context,
//            StorageConfig.SECURE_SHARED_PREFERENCES_FILE_NAME,
//            masterKey, // masterKey created above
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )

    override var token: String by StringPreferenceDelegate(encryptedPrefs, TOKEN_KEY)

    override var userID: Int by IntPreferenceDelegate(encryptedPrefs, USER_ID_KEY)

    override var userEmail: String by StringPreferenceDelegate(encryptedPrefs, USER_EMAIL_KEY)

    override var language: String by StringPreferenceDelegate(
        context.getSharedPreferences("APP_DATA", MODE_PRIVATE),
        LANGUAGE,
        //  Language.ENGLISH
    )
    override var user_role: Int by IntPreferenceDelegate(encryptedPrefs, USER_ROLE_KEY)
    override var Device_token: String by StringPreferenceDelegate(encryptedPrefs, DEVICE_TOKEN)
    override var deviceId: String by StringPreferenceDelegate(encryptedPrefs, DEVICE_ID)
    override var fairId: Int by IntPreferenceDelegate(encryptedPrefs, FAIR_ID)
    override var driverID: Int by IntPreferenceDelegate(encryptedPrefs, DRIVER_ID)
    override var fairBookingId: String by StringPreferenceDelegate(encryptedPrefs, FAIR_BOOKING_ID)
    override var latitude: String by StringPreferenceDelegate(encryptedPrefs, LATITUDE)
    override var longitude: String by StringPreferenceDelegate(encryptedPrefs, LONGITUDE)
    override var driverbidprice: Int by IntPreferenceDelegate(encryptedPrefs, DRIVER_BID_PRICE)
    override var driverFairId: Int by IntPreferenceDelegate(encryptedPrefs, DRIVER_FAIR_ID)
    override var flag: Boolean by BooleanPreferenceDelegate(encryptedPrefs,FLAG)



//    private fun getOrGenerateNewKeysetHandle(): KeysetHandle {
//        return AndroidKeysetManager.Builder()
//            .withSharedPref(api, tinkKeySetName)
//            .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
//            .withMasterKeyUri(masterKeyUri)
//            .build()
//            .keysetHandle
//    }

    override fun clearAll() {
        encryptedPrefs.edit {
            clear()
        }
    }
}