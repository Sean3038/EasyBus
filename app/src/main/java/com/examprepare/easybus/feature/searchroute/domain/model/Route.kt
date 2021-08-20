package com.examprepare.easybus.feature.searchroute.domain.model

data class Route(val routeId: String, val routeName: String) {
    companion object {
        val empty = Route("", "")
    }
}