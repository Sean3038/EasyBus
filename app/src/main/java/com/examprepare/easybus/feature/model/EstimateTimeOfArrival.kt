package com.examprepare.easybus.feature.model

data class EstimateTimeOfArrival(
    val routeId: String,
    val stopId: String,
    val routeName: String,
    val stopName: String,
    val plateNumb: String?,
    val direction: Direction,
    val stopStatus: StopStatus = StopStatus.None,
    val estimateTime: Int?,
    val isLastBus: Boolean
) {
    companion object {
        val empty = EstimateTimeOfArrival(
            "",
            "",
            "",
            "",
            null,
            Direction.UnKnown,
            StopStatus.None,
            null,
            false
        )
    }
}