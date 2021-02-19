package com.thiagoperea.mybills.datasource.model

data class AppSettings(
    val notificationEnabled: Boolean,
    val notificationHour: String?,
    val notificationMinute: String?,
)
