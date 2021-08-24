package com.examprepare.easybus.core.model

import com.google.gson.annotations.SerializedName

data class SearchRouteEntity(
    @SerializedName("RouteID")
    val routeID: String,
    @SerializedName("RouteName")
    val routeName: RouteName
)