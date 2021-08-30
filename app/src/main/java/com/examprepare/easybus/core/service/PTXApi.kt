package com.examprepare.easybus.core.service

import com.examprepare.easybus.core.model.RouteNetworkEntity
import retrofit2.http.GET
import retrofit2.http.Path

interface PTXApi {

    companion object {
        const val ROUTES = "v2/Bus/Route/City/NewTaipei/"
    }

    @GET("$ROUTES?\$format=JSON")
    suspend fun getRoutes(): List<RouteNetworkEntity>

    @GET("$ROUTES/{RouteName}?\$format=JSON")
    suspend fun getRoutes(@Path("RouteName") routeName: String): List<RouteNetworkEntity>
}