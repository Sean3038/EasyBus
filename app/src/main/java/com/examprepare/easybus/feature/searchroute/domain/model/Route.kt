package com.examprepare.easybus.feature.searchroute.domain.model

data class Route(val routeId: String, val routeName: String, var isLiked: Boolean = false) {
    companion object {
        val empty = Route("", "")
    }
}