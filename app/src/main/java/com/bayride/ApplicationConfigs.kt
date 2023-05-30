package com.bayride

object NetworkConfig {
  const val CALL_TIMEOUT: Long = 3

  const val CONNECT_TIMEOUT: Long = 3

  const val READ_TIMEOUT: Long = 3

  const val WRITE_TIMEOUT: Long = 3

  const val API_DOMAIN_DEFAULT = "http://18.205.157.115:4000/"


}

object StorageConfig {
  const val SECURE_SHARED_PREFERENCES_FILE_NAME = "BayRide_SECURE_FILE"

  const val SHARED_PREFERENCES_FILE_NAME = "BayRide_SHARED_PREFERENCE"
}