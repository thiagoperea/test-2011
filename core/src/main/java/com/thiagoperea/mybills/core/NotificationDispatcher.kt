package com.thiagoperea.mybills.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationDispatcher {

    fun makeStatusNotification(
        title: String,
        message: String,
        @DrawableRes iconRes: Int,
        isCancellable: Boolean,
        context: Context,
    ) {
        createChannelIfNecessary(context)

        // Create the notification
        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setAutoCancel(isCancellable)

        // Show the notification
        NotificationManagerCompat.from(context).notify(Constants.NOTIFICATION_ID, builder.build())
    }

    private fun createChannelIfNecessary(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val description = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance)
            channel.description = description

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(channel)
        }
    }
}