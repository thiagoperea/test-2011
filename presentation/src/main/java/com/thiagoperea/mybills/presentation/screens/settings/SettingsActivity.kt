package com.thiagoperea.mybills.presentation.screens.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.thiagoperea.mybills.core.converter.DateConverter
import com.thiagoperea.mybills.core.extension.setHide
import com.thiagoperea.mybills.core.extension.setVisible
import com.thiagoperea.mybills.datasource.model.AppSettings
import com.thiagoperea.mybills.presentation.databinding.ActivitySettingsBinding
import com.thiagoperea.mybills.presentation.screens.splash.SplashActivity
import org.koin.android.ext.android.inject

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeFields()
        viewModel.loadSettings()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        binding.logoutButton.setOnClickListener { viewModel.doLogout() }

        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.notificationsHourSelector.setVisible()
            } else {
                viewModel.setNotificationOff()
                binding.notificationsHourSelector.setHide()
            }
        }

        binding.notificationsHourSelector.setOnClickListener {
            openNotificationPicker()
        }
    }

    private fun observeFields() {
        viewModel.settingsState.observe(this) { state ->
            when (state) {
                is SettingsState.OnLoadSuccess -> {
                    binding.notificationsSwitch.isChecked = state.settings.notificationEnabled
                    setNotificationTime(state.settings)
                }
                is SettingsState.OnLogout -> doAfterLogout()
                is SettingsState.OnLoadError -> {
                } //TODO()
            }
        }
    }

    private fun setNotificationTime(settings: AppSettings) {
        if (settings.notificationHour != null && settings.notificationMinute != null) {
            val formattedTime = DateConverter.formatTime(
                settings.notificationHour?.toIntOrNull() ?: 0,
                settings.notificationMinute?.toIntOrNull() ?: 0
            )

            binding.notificationsHourValue.text = formattedTime
        }
    }

    private fun doAfterLogout() {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun openNotificationPicker() {
        MaterialTimePicker.Builder()
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(6)
            .setMinute(0)
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    val hour = this.hour
                    val min = this.minute
                    binding.notificationsHourValue.text = DateConverter.formatTime(hour, min)
                    viewModel.setNotificationOn(hour, minute)
                }
            }
            .show(supportFragmentManager, null)
    }
}
