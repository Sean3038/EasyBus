package com.examprepare.easybus.feature.model

data class Station(
    val stationID: String,
    val stationName: String,
    val stationAddress: String,
    val positionLat: Double,
    val positionLon: Double,
    val stopItems: List<StopItem>,
    val routeItems: List<RouteItem>
) {
    data class StopItem(val stopId: String, val stopName: String)
    data class RouteItem(val routeId: String, val routeName: String)
}