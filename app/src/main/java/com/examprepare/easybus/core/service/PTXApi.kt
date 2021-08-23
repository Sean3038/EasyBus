package com.examprepare.easybus.core.service

import com.examprepare.easybus.core.model.DisplayStopOfRouteEntity
import com.examprepare.easybus.core.model.RouteNetworkEntity
import com.examprepare.easybus.core.model.StopNetworkEntity
import retrofit2.http.GET
import retrofit2.http.Path

interface PTXApi {

    companion object {
        const val ROUTES = "v2/Bus/Route/City/NewTaipei/"
        const val STOP = "v2/Bus/Stop/City/NewTaipei/"
        const val DISPLAY_STOP_OF_ROUTE = "v2/Bus/DisplayStopOfRoute/City/NewTaipei/"
    }

    @GET("$ROUTES?\$format=JSON")
    suspend fun getRoutes(): List<RouteNetworkEntity>

    @GET("$ROUTES/{RouteName}?\$format=JSON")
    suspend fun getRoutes(@Path("RouteName") routeName: String): List<RouteNetworkEntity>

    @GET("$STOP?\$format=JSON")
    suspend fun getStops(): List<StopNetworkEntity>

    @GET("$DISPLAY_STOP_OF_ROUTE/{RouteName}?\$format=JSON")
    suspend fun getDisplayStopOfRoute(): List<DisplayStopOfRouteEntity>

}