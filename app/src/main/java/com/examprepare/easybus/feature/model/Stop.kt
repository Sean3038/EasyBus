package com.examprepare.easybus.feature.model

data class Stop(
    val stopId: String,
    val stopName: String,
    val positionLatitude: Double,
    val positionLongitude: Double
) {
}