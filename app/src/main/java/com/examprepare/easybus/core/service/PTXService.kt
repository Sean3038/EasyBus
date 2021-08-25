package com.examprepare.easybus.core.service

import com.examprepare.easybus.core.model.network.RouteNetworkEntity
import com.examprepare.easybus.core.model.network.SearchRouteEntity

class PTXService(private val api: PTXApi) {

    suspend fun getRoutes(city: String): List<RouteNetworkEntity> {
        return api.getRoutes(city, null)
    }

    suspend fun getRoute(city: String, routeId: String): RouteNetworkEntity? {
        val filter = "RouteID eq '$routeId'"
        return api.getRoutes(city, filter).firstOrNull()
    }

    suspend fun searchRoute(city: String, keyword: String): List<SearchRouteEntity> {
        val filter = "contains(RouteName/Zh_tw, '$keyword' ) or contains(RouteName/En, '$keyword' )"
        return api.searchRoute(city, filter)
    }

    suspend fun getStops() = api.getStops()
}