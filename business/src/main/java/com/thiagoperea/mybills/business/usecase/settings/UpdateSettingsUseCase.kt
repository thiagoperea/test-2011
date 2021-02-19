package com.thiagoperea.mybills.business.usecase.settings

import android.content.SharedPreferences
import com.thiagoperea.mybills.core.Constants

class UpdateSettingsUseCase(
    private val sharedPreferences: SharedPreferences,
) {

    fun setNotificationOn(hour: Int, minute: Int) {
        sharedPreferences.edit()
            .putBoolean(Constants.SETTINGS_IS_NOTIFICATION_ENABLED, true)
            .putString(Constants.SETTINGS_NOTIFICATION_HOUR, hour.toString())
            .putString(Constants.SETTINGS_NOTIFICATION_MINUTE, minute.toString())
            .apply()
    }

    fun setNotificationOff() {
        sharedPreferences.edit()
            .putBoolean(Constants.SETTINGS_IS_NOTIFICATION_ENABLED, false)
            .putString(Constants.SETTINGS_NOTIFICATION_HOUR, null)
            .putString(Constants.SETTINGS_NOTIFICATION_MINUTE, null)
            .apply()
    }
}
