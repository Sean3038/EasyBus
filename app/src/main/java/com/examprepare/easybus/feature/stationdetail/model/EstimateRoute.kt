package com.examprepare.easybus.feature.stationdetail.model

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

sealed class StopStatus {
    object None : StopStatus()//無
    object Normal : StopStatus()//正常
    object NoneDeparture : StopStatus()//尚未發車
    object NoShift : StopStatus()//末班車已過
    object NonStop : StopStatus()//交管不停靠
    object NoOperation : StopStatus()//今日未營運
}
