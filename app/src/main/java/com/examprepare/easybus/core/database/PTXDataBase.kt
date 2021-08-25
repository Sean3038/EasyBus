package com.examprepare.easybus.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.examprepare.easybus.core.database.dao.RouteEntityDao
import com.examprepare.easybus.core.database.dao.StopEntityDao
import com.examprepare.easybus.core.model.local.RouteLocalEntity
import com.examprepare.easybus.core.model.local.StopLocalEntity


@Database(
    entities = [RouteLocalEntity::class, StopLocalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PTXDataBase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "ptx_database"
    }

    abstract fun routeEntity(): RouteEntityDao

    abstract fun stopEntity(): StopEntityDao

}