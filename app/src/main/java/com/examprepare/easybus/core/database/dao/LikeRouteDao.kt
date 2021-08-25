package com.examprepare.easybus.core.database.dao

import androidx.room.*
import com.examprepare.easybus.core.model.local.LikeRouteEntity

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