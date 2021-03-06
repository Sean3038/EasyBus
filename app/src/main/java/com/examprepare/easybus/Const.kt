package com.examprepare.easybus

object Const {
    const val APP_URL = "easybus://easybus.com"
    const val PTX_API_URL = "https://ptx.transportdata.tw/MOTC/"
    const val GET_NEAR_STATION_INTERVAL_MILLISECONDS: Long = 60000
    const val GET_NEAR_STATION_RADIUS_METERS = 600
    const val UPDATE_REALTIME_ROUTE_ARRIVAL_TIME_INTERVAL_MILLISECONDS: Long = 10000
    const val UPDATE_REALTIME_STATION_TIME_INTERVAL_MILLISECONDS: Long = 15000
    val APPROACH_NOTIFY_MINUTES_SETTINGS = listOf(5, 6, 7, 8, 9, 10, 15, 20)
    const val SHARED_PREFERENCE_NAME = "EasyBusSharedPreference"
}