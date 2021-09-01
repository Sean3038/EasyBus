package com.examprepare.easybus.core.notification

import android.content.SharedPreferences

interface NotificationIDGenerator {
    fun generateID(notifyChannel: String): Int

    class SharedPreferenceImpl constructor(
        private val sharedPreferences: SharedPreferences
    ) : NotificationIDGenerator {

        companion object {
            const val NotificationPrefix = "notification_"
        }

        override fun generateID(notifyChannel: String): Int {
            val id = sharedPreferences.getInt(NotificationPrefix + notifyChannel, 0)
            sharedPreferences.edit().putInt(NotificationPrefix + notifyChannel, id + 1).apply()
            return id
        }
    }
}