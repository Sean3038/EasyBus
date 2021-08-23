package com.examprepare.easybus.core.model

import com.google.gson.annotations.SerializedName

data class DisplayStopOfRouteEntity(
    @SerializedName("Direction")
    val direction: Int,
    @SerializedName("RouteID")
    val routeID: String,
    @SerializedName("RouteName")
    val routeName: RouteName,
    @SerializedName("RouteUID")
    val routeUID: String,
    @SerializedName("Stops")
    val stops: List<StopInfo>,
    @SerializedName("UpdateTime")
    val updateTime: String,
    @SerializedName("VersionID")
    val versionID: Int
)

data class StopInfo(
    @SerializedName("StationID")
    val stationID: String,
    @SerializedName("StopBoarding")
    val stopBoarding: Int,
    @SerializedName("StopID")
    val stopID: String,
    @SerializedName("StopName")
    val stopName: StopName,
    @SerializedName("StopPosition")
    val stopPosition: StopPosition,
    @SerializedName("StopSequence")
    val stopSequence: Int,
    @SerializedName("StopUID")
    val stopUID: String
)
