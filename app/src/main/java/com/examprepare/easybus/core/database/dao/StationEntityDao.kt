package com.examprepare.easybus.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.examprepare.easybus.core.model.local.StationLocalEntity

@Dao
interface StationEntityDao {

    @Query("SELECT * FROM station_entities")
    suspend fun getAll(): List<StationLocalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg stationLocalEntity: StationLocalEntity)
}