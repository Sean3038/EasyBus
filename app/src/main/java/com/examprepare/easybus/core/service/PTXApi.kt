package com.examprepare.easybus.core.service

import com.examprepare.easybus.core.model.network.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PTXApi {

    companion object {
        const val ROUTES = "v2/Bus/Route/City"
        const val STOP = "v2/Bus/Stop/City"
        const val STATION = "v2/Bus/Station/City"
        const val ESTIMATED_TIME_OF_ARRIVAL = "v2/Bus/EstimatedTimeOfArrival/City/"
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
        @Query("\$filter") filter: String?,
        @Query("\$format") format: String = "JSON"
    ): List<SearchRouteNetworkEntity>

    @GET("$STATION/{City}")
    suspend fun getStations(
        @Path("City") city: String,
        @Query("\$filter") filter: String?,
        @Query("\$spatialFilter") spatialFilter: String?,
        @Query("\$format") format: String = "JSON"
    ): List<StationNetworkEntity>

    @GET("$ESTIMATED_TIME_OF_ARRIVAL/{City}")
    suspend fun estimatedTimeOfArrival(
        @Path("City") city: String,
        @Query("\$filter") filter: String?,
        @Query("\$orderby") orderby: String?,
        @Query("\$format") format: String = "JSON"
    ): List<EstimatedTimeOfArrivalNetworkEntity>


    @GET("$STOP?\$format=JSON")
    suspend fun getStops(): List<StopNetworkEntity>

    @GET("$DISPLAY_STOP_OF_ROUTE/{RouteName}?\$format=JSON")
    suspend fun getDisplayStopOfRoute(): List<DisplayStopOfRouteEntity>

}