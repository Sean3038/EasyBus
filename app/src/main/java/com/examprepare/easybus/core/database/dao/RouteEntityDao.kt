package com.examprepare.easybus.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.examprepare.easybus.core.model.local.RouteLocalEntity

@Dao
interface RouteEntityDao {

    @Query("SELECT * FROM route_entities")
    suspend fun getAll(): List<RouteLocalEntity>

    @Query("SELECT * FROM route_entities WHERE RouteID == :routeId")
    suspend fun get(routeId: String): List<RouteLocalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg routeLocalEntity: RouteLocalEntity)
}