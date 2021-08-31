package com.examprepare.easybus.core.model.network

import com.examprepare.easybus.feature.model.Direction
import com.examprepare.easybus.feature.model.EstimateTimeOfArrival
import com.examprepare.easybus.feature.model.StopStatus
import com.google.gson.annotations.SerializedName

data class EstimatedTimeOfArrivalNetworkEntity(
    @SerializedName("CurrentStop")
    val currentStop: String,
    @SerializedName("DataTime")
    val dataTime: String,
    @SerializedName("DestinationStop")
    val destinationStop: String,
    @SerializedName("Direction")
    val direction: Int,
    @SerializedName("EstimateTime")
    val estimateTime: Int?,
    @SerializedName("Estimates")
    val estimates: List<Estimate>,
    @SerializedName("IsLastBus")
    val isLastBus: Boolean,
    @SerializedName("MessageType")
    val messageType: Int,
    @SerializedName("NextBusTime")
    val nextBusTime: String,
    @SerializedName("PlateNumb")
    val plateNumb: String?,
    @SerializedName("RouteID")
    val routeID: String,
    @SerializedName("RouteName")
    val routeName: RouteName,
    @SerializedName("RouteUID")
    val routeUID: String,
    @SerializedName("SrcRecTime")
    val srcRecTime: String,
    @SerializedName("SrcTransTime")
    val srcTransTime: String,
    @SerializedName("SrcUpdateTime")
    val srcUpdateTime: String,
    @SerializedName("StopCountDown")
    val stopCountDown: Int,
    @SerializedName("StopID")
    val stopID: String,
    @SerializedName("StopName")
    val stopName: StopName,
    @SerializedName("StopSequence")
    val stopSequence: Int,
    @SerializedName("StopStatus")
    val stopStatus: Int,
    @SerializedName("StopUID")
    val stopUID: String,
    @SerializedName("SubRouteID")
    val subRouteID: String,
    @SerializedName("SubRouteName")
    val subRouteName: SubRouteName,
    @SerializedName("SubRouteUID")
    val subRouteUID: String,
    @SerializedName("TransTime")
    val transTime: String,
    @SerializedName("UpdateTime")
    val updateTime: String
) {
    fun toEstimateTimeOfArrival(): EstimateTimeOfArrival =
        EstimateTimeOfArrival(
            routeID,
            stopID,
            plateNumb,
            when (direction) {
                0 -> Direction.Departure
                1 -> Direction.Return
                2 -> Direction.Loop
                else -> Direction.UnKnown
            },
            when {
                estimateTime != null -> if (estimateTime < 60) StopStatus.OnPulledIN else StopStatus.Normal
                stopStatus == 0 -> StopStatus.OnPulledIN
                stopStatus == 1 -> StopStatus.NoneDeparture
                stopStatus == 2 -> StopStatus.NonStop
                stopStatus == 3 -> StopStatus.NoShift
                stopStatus == 4 -> StopStatus.NoOperation
                else -> StopStatus.None
            },
            estimateTime,
            isLastBus
        )
}

data class Estimate(
    @SerializedName("EstimateTime")
    val estimateTime: Int,
    @SerializedName("IsLastBus")
    val isLastBus: Boolean,
    @SerializedName("PlateNumb")
    val plateNumb: String,
    @SerializedName("VehicleStopStatus")
    val vehicleStopStatus: Int
)

