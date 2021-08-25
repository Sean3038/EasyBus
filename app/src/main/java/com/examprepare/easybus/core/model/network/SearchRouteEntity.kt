package com.examprepare.easybus.core.model.network

import com.google.gson.annotations.SerializedName

data class SearchRouteEntity(
    @SerializedName("RouteID")
    val routeID: String,
    @SerializedName("RouteName")
    val routeName: RouteName
)