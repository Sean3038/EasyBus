package com.examprepare.easybus.core.model.network

import com.google.gson.annotations.SerializedName

data class StopNetworkEntity(
    @SerializedName("AuthorityID")
    val authorityID: String,
    @SerializedName("Bearing")
    val bearing: String,
    @SerializedName("City")
    val city: String,
    @SerializedName("CityCode")
    val cityCode: String,
    @SerializedName("LocationCityCode")
    val locationCityCode: String,
    @SerializedName("StationID")
    val stationID: String,
    @SerializedName("StopAddress")
    val stopAddress: String,
    @SerializedName("StopID")
    val stopID: String,
    @SerializedName("StopName")
    val stopName: StopName,
    @SerializedName("StopPosition")
    val stopPosition: StopPosition,
    @SerializedName("StopUID")
    val stopUID: String,
    @SerializedName("UpdateTime")
    val updateTime: String,
    @SerializedName("VersionID")
    val versionID: Int
)

data class StopName(
    @SerializedName("En")
    val en: String,
    @SerializedName("Zh_tw")
    val zhTw: String
)

data class StopPosition(
    @SerializedName("GeoHash")
    val geoHash: String,
    @SerializedName("PositionLat")
    val positionLat: Double,
    @SerializedName("PositionLon")
    val positionLon: Double
)