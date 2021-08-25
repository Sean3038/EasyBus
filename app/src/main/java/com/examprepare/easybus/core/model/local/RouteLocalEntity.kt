package com.examprepare.easybus.core.model.local

import androidx.room.*
import com.examprepare.easybus.feature.model.Route

@Entity(tableName = "route_entities")
data class RouteLocalEntity(
    @PrimaryKey
    val RouteID: String,
    val RouteName: String,
    val DepartureStopName: String,
    val DestinationStopName: String
) {
    fun toRoute(): Route = Route(RouteID, RouteName)
}