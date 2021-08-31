package com.examprepare.easybus.feature.model

data class Route(
    val routeId: String,
    val routeName: String,
    val departureStopName: String,
    val destinationStopName: String
) {
    companion object {
        val empty = Route("", "", "", "")
    }
}