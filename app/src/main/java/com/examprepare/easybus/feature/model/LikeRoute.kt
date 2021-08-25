package com.examprepare.easybus.feature.model

data class LikeRoute(val routeId: String) {
    companion object {
        val empty = LikeRoute("")
    }
}
