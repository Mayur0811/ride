package com.bayride.data.datasources.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.bayride.StorageConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface IPreferenceStorage {

  fun clearAll()
}

class PreferenceStorage @Inject constructor(
  @ApplicationContext context: Context
) : IPreferenceStorage {
  companion object {
  }

  private val prefs: SharedPreferences =
    context.getSharedPreferences(StorageConfig.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

  override fun clearAll() {
    prefs.edit {
      clear()
    }
  }
}