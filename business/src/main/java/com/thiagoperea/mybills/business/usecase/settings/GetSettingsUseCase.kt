package com.thiagoperea.mybills.business.usecase.settings

import android.content.SharedPreferences
import com.thiagoperea.mybills.core.Constants
import com.thiagoperea.mybills.datasource.model.AppSettings

class GetSettingsUseCase(
    private val sharedPreferences: SharedPreferences,
) {

    fun execute(
        onSuccess: (AppSettings) -> Unit,
        onError: () -> Unit,
    ) = try {
        val isNotificationEnabled = sharedPreferences.getBoolean(Constants.SETTINGS_IS_NOTIFICATION_ENABLED, false)
        val notificationHour = sharedPreferences.getString(Constants.SETTINGS_NOTIFICATION_HOUR, null)
        val notificationMinute = sharedPreferences.getString(Constants.SETTINGS_NOTIFICATION_MINUTE, null)

        val settings = AppSettings(isNotificationEnabled, notificationHour, notificationMinute)
        onSuccess(settings)
    } catch (error: Exception) {
        onError()
    }

}
