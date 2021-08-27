package com.examprepare.easybus.feature.model

data class EstimateTimeOfArrival(
    val routeId: String,
    val stopId: String,
    val plateNumb: String?,
    val stopStatus: Int,
    val estimateTime: Int,
    val isLastBus: Boolean
)