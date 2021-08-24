package com.examprepare.easybus.feature.searchroute.domain.model

data class SearchRouteResult(
    val results: List<Item>,
) {
    data class Item(
        val routeID: String,
        val routeName: String,
    )
}


