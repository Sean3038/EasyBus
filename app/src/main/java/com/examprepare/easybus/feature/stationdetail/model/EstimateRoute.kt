package com.examprepare.easybus.feature.stationdetail.model

import com.examprepare.easybus.feature.model.StopStatus

data class EstimateRoute(
    val routeId: String,
    val routeName: String,
    val departureStopName: String,
    val destinationStopName: String,
    val stopStatus: StopStatus,
    val plateNumber: String?,
    val estimateTime: Int?,
    val isLastBus: Boolean?
)
