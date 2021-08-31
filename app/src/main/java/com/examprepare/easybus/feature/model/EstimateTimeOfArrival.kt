package com.examprepare.easybus.feature.model

data class EstimateTimeOfArrival(
    val routeId: String,
    val stopId: String,
    val plateNumb: String?,
    val direction: Direction,
    val stopStatus: StopStatus = StopStatus.None,
    val estimateTime: Int?,
    val isLastBus: Boolean
)