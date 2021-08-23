package com.examprepare.easybus.feature.searchnearstop.domain.model

data class Stop(
    val stopId: String,
    val stopName: String,
    val positionLatitude: Double,
    val positionLongitude: Double
) {
}