package com.examprepare.easybus.core.model.network

import com.examprepare.easybus.feature.model.Station
import com.google.gson.annotations.SerializedName

data class StationNetworkEntity(
    @SerializedName("Bearing")
    val bearing: String,
    @SerializedName("LocationCityCode")
    val locationCityCode: String,
    @SerializedName("StationAddress")
    val stationAddress: String,
    @SerializedName("StationGroupID")
    val stationGroupID: String,
    @SerializedName("StationID")
    val stationID: String,
    @SerializedName("StationName")
    val stationName: StationName,
    @SerializedName("StationPosition")
    val stationPosition: StationPosition,
    @SerializedName("StationUID")
    val stationUID: String,
    @SerializedName("Stops")
    val stops: List<Stop>,
    @SerializedName("UpdateTime")
    val updateTime: String,
    @SerializedName("VersionID")
    val versionID: Int
) {
    fun toStation(): Station = Station(
        stationID,
        stationName.zhTw,
        stationAddress,
        stationPosition.positionLat,
        stationPosition.positionLon,
        stops.map { Station.StopItem(it.stopID, it.stopName.zhTw) },
        stops.map { Station.RouteItem(it.routeID, it.routeName.Zh_tw) })
}

data class StationName(
    @SerializedName("En")
    val en: String,
    @SerializedName("Zh_tw")
    val zhTw: String
)

data class StationPosition(
    @SerializedName("GeoHash")
    val geoHash: String,
    @SerializedName("PositionLat")
    val positionLat: Double,
    @SerializedName("PositionLon")
    val positionLon: Double
)

data class Stop(
    @SerializedName("RouteID")
    val routeID: String,
    @SerializedName("RouteName")
    val routeName: RouteName,
    @SerializedName("RouteUID")
    val routeUID: String,
    @SerializedName("StopID")
    val stopID: String,
    @SerializedName("StopName")
    val stopName: StopName,
    @SerializedName("StopUID")
    val stopUID: String
)