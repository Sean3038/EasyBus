package com.examprepare.easybus.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.examprepare.easybus.core.model.local.StopLocalEntity

@Dao
interface StopEntityDao {

    @Query("SELECT * FROM stop_entities")
    suspend fun getAll(): List<StopLocalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg stopLocalEntity: StopLocalEntity)
}