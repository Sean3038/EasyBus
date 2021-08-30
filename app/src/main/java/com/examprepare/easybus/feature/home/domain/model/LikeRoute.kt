package com.examprepare.easybus.feature.home.domain.model

data class LikeRoute(val routeId: String) {
    companion object {
        val empty = LikeRoute("")
    }
}
