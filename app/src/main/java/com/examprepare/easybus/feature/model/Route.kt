package com.examprepare.easybus.feature.model

data class Route(val routeId: String, val routeName: String) {
    companion object {
        val empty = Route("", "")
    }
}