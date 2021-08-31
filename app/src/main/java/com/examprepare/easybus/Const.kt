package com.examprepare.easybus

object Const {
    const val PTX_API_URL = "https://ptx.transportdata.tw/MOTC/"
    const val PTX_APP_ID = "***REMOVED***"
    const val PTX_APP_KEY = "***REMOVED***"
    const val GET_NEAR_STATION_INTERVAL_MILLISECONDS: Long = 60000
    const val GET_NEAR_STATION_RADIUS_METERS = 600
    const val UPDATE_REALTIME_ROUTE_ARRIVAL_TIME_INTERVAL_MILLISECONDS: Long = 10000
    const val UPDATE_REALTIME_STATION_TIME_INTERVAL_MILLISECONDS: Long = 15000
    val APPROACH_NOTIFY_MINUTES_SETTINGS = listOf(5, 6, 7, 8, 9, 10, 15, 20)
}