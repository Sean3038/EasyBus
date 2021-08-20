package com.examprepare.easybus.core.model

import androidx.room.*
import com.examprepare.easybus.feature.home.domain.model.LikeRoute

@Entity(tableName = "like_route")
data class LikeRouteEntity(@PrimaryKey val RouteID: String) {
    fun toLikeRoute(): LikeRoute = LikeRoute(RouteID)
}

@Dao
interface LikeRouteDao {

    @Query("SELECT * FROM like_route")
    suspend fun getAll(): List<LikeRouteEntity>

    @Query("SELECT * FROM like_route WHERE RouteID == :routeId")
    suspend fun get(routeId: String): LikeRouteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg likeRouteEntity: LikeRouteEntity)

    @Delete
    suspend fun delete(vararg likeRouteEntity: LikeRouteEntity)
}