package com.examprepare.easybus.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.examprepare.easybus.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val idGenerator: NotificationIDGenerator
) {

    companion object {
        const val APPROACH_NOTIFY_CHANNEL_ID = "EasyBusApproachNotify"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Approach Notify Channel"
            val descriptionText = "notified dialog when estimated time less than setting minute"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(APPROACH_NOTIFY_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                setShowBadge(true)
                enableVibration(false)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun notifyApproach(title: String, message: String, pendingIntent: PendingIntent?) {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, APPROACH_NOTIFY_CHANNEL_ID)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setFullScreenIntent(pendingIntent, true)

        with(NotificationManagerCompat.from(context)) {
            notify(idGenerator.generateID(APPROACH_NOTIFY_CHANNEL_ID), builder.build())
        }
    }
}