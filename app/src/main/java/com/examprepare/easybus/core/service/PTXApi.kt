package com.examprepare.easybus.core.service

import com.examprepare.easybus.core.model.DisplayStopOfRouteEntity
import com.examprepare.easybus.core.model.RouteNetworkEntity
import com.examprepare.easybus.core.model.SearchRouteEntity
import com.examprepare.easybus.core.model.StopNetworkEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PTXApi {

    companion object {
        const val ROUTES = "v2/Bus/Route/City"
        const val STOP = "v2/Bus/Stop/City"
        const val DISPLAY_STOP_OF_ROUTE = "v2/Bus/DisplayStopOfRoute/City/Taipei/"
    }

    @GET("$ROUTES/{City}")
    suspend fun getRoutes(
        @Path("City") city: String,
        @Query("\$filter") filter: String?,
        @Query("\$format") format: String = "JSON"
    ): List<RouteNetworkEntity>

    @GET("$ROUTES/{City}/{RouteName}")
    suspend fun getRoutes(
        @Path("City") city: String,
        @Path("RouteName") routeName: String,
        @Query("\$filter") filter: String,
        @Query("\$format") format: String = "JSON"
    ): List<RouteNetworkEntity>

    @GET("$ROUTES/{City}?\$select=RouteID, RouteName")
    suspend fun searchRoute(
        @Path("City") city: String,
        @Query("\$filter") filter: String,
        @Query("\$format") format: String = "JSON"
    ): List<SearchRouteEntity>

    @GET("$STOP?\$format=JSON")
    suspend fun getStops(): List<StopNetworkEntity>

    @GET("$DISPLAY_STOP_OF_ROUTE/{RouteName}?\$format=JSON")
    suspend fun getDisplayStopOfRoute(): List<DisplayStopOfRouteEntity>

}