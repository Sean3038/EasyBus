package com.examprepare.easybus.feature.model

data class DisplayStopOfRoute(
    val routeId: String,
    val routeName: String,
    val direction: Direction,
    val stops: List<Stop>
) {
    data class Stop(
        val stopId: String,
        val stationId: String,
        val stopName: String,
        val stopSequence: Int
    )
}

sealed class Direction(val index: Int) {
    object UnKnown : Direction(255)
    object Departure : Direction(0)
    object Return : Direction(1)
    object Loop : Direction(2)
}
