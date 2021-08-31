package com.examprepare.easybus.feature.routedetail.model

import com.examprepare.easybus.feature.model.StopStatus

data class RealTimeRouteInfo(
    val destination: String,
    val stopItems: List<StopItem>
) {
    data class StopItem(
        val stopId: String,
        val stationId: String,
        val stopName: String,
        val stopStatus: StopStatus,
        val stopSequence: Int,
        val plateNumb: String?,
        val estimateTime: Int?,
        val isLastBus: Boolean
    )
}