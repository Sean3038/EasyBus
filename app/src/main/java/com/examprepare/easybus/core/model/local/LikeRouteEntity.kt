package com.examprepare.easybus.core.model.local

import androidx.room.*
import com.examprepare.easybus.feature.model.LikeRoute

@Entity(tableName = "like_route")
data class LikeRouteEntity(@PrimaryKey val RouteID: String) {
    fun toLikeRoute(): LikeRoute = LikeRoute(RouteID)
}