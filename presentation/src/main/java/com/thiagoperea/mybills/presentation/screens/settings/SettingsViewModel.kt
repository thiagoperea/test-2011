package com.thiagoperea.mybills.presentation.screens.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thiagoperea.mybills.business.usecase.notification.NotificationScheduleUseCase
import com.thiagoperea.mybills.business.usecase.settings.GetSettingsUseCase
import com.thiagoperea.mybills.business.usecase.settings.UpdateSettingsUseCase
import com.thiagoperea.mybills.business.usecase.user.LogoutUseCase
import com.thiagoperea.mybills.datasource.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val notificationScheduleUseCase: NotificationScheduleUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _settingsState = MutableLiveData<SettingsState>()
    val settingsState: LiveData<SettingsState> = _settingsState

    fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {

            getSettingsUseCase.execute(
                onSuccess = { settings ->
                    _settingsState.postValue(SettingsState.OnLoadSuccess(settings))
                },
                onError = {
                    _settingsState.postValue(SettingsState.OnLoadError)
                }
            )
        }
    }

    fun doLogout() {
        notificationScheduleUseCase.removePreviousSchedules()
        logoutUseCase.execute()
        _settingsState.postValue(SettingsState.OnLogout)
    }

    fun setNotificationOn(hour: Int, minute: Int) {
        updateSettingsUseCase.setNotificationOn(hour, minute)
        notificationScheduleUseCase.schedule(hour, minute)
    }

    fun setNotificationOff() {
        updateSettingsUseCase.setNotificationOff()
        notificationScheduleUseCase.removePreviousSchedules()
    }
}

sealed class SettingsState {
    data class OnLoadSuccess(val settings: AppSettings) : SettingsState()
    object OnLoadError : SettingsState()

    object OnLogout : SettingsState()
}