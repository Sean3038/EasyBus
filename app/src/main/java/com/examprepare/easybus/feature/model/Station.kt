package com.examprepare.easybus.feature.model

data class Station(
    val stationID: String,
    val stationName: String,
    val stationAddress: String,
    val positionLat: Double,
    val positionLon: Double
)