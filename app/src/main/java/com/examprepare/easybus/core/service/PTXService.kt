package com.examprepare.easybus.core.service

import com.examprepare.easybus.core.model.network.*

class PTXService(private val api: PTXApi) {

    suspend fun getRoutes(city: String): List<RouteNetworkEntity> {
        return api.getRoutes(city, null)
    }

    suspend fun getRoute(city: String, routeId: String): RouteNetworkEntity? {
        val filter = "RouteID eq '$routeId'"
        return api.getRoutes(city, filter).firstOrNull()
    }

    suspend fun getRoutes(city: String, routeIds: List<String>): List<RouteNetworkEntity> {
        var filter: String? = null
        routeIds.forEachIndexed { index, routeId ->
            if (index == 0) {
                filter = "RouteID eq '$routeId'"
            } else {
                filter += " or RouteID eq '$routeId'"
            }
        }
        return api.getRoutes(city, filter)
    }


    suspend fun searchRoute(city: String, keyword: String): List<SearchRouteNetworkEntity> {
        val filter = "contains(RouteName/Zh_tw, '$keyword' ) or contains(RouteName/En, '$keyword' )"
        return api.searchRoute(city, filter)
    }

    suspend fun findNearStation(
        city: String,
        lat: Double,
        lon: Double,
        distance: Int
    ): List<StationNetworkEntity> {
        val spatialFilter = "nearby($lat,$lon,$distance)"
        return api.getStations(city, null, spatialFilter)
    }

    suspend fun getStations(city: String, stationId: String): List<StationNetworkEntity> {
        val filter = "StationID eq '$stationId'"
        return api.getStations(city, filter, null)
    }

    suspend fun estimateTimeOfArrival(
        city: String,
        stopIds: List<String>
    ): List<EstimatedTimeOfArrivalNetworkEntity> {
        var filter: String? = null
        stopIds.forEachIndexed { index, stopId ->
            if (index == 0) {
                filter = "StopID eq '$stopId'"
            } else {
                filter += " or StopID eq '$stopId'"
            }
        }
        return api.estimatedTimeOfArrival(city, filter, "RouteName/Zh_tw")
    }

    suspend fun estimateTimeOfArrival(
        city: String,
        routeId: String
    ): List<EstimatedTimeOfArrivalNetworkEntity> {
        val filter = "RouteID eq '$routeId'"
        return api.estimatedTimeOfArrival(city, filter, "RouteName/Zh_tw")
    }

    suspend fun estimateTimeOfArrival(
        city: String,
        routeId: String,
        stopId: String,
        direction: Int,
    ): List<EstimatedTimeOfArrivalNetworkEntity> {
        val filter = "RouteID eq '$routeId' and StopID eq '$stopId' and Direction eq $direction"
        return api.estimatedTimeOfArrival(city, filter, null)
    }

    suspend fun getDisplayStopOfRoute(
        city: String,
        routeId: String
    ): List<DisplayStopOfRouteEntity> {
        val filter = "RouteID eq '$routeId'"
        return api.getDisplayStopOfRoute(city, filter)
    }

    suspend fun getStops() = api.getStops()
}