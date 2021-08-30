package com.examprepare.easybus.feature.home.domain.model

data class FavoriteRoute(val routeId: String, val routeName: String, val isLiked: Boolean) {
    companion object {
        val empty = FavoriteRoute("", "", false)
    }
}