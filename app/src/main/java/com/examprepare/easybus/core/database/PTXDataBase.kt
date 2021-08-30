package com.examprepare.easybus.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.examprepare.easybus.core.model.RouteEntityDao
import com.examprepare.easybus.core.model.RouteLocalEntity

@Database(entities = [(RouteLocalEntity::class)], version = 1)
abstract class PTXDataBase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "ptx_database"
    }

    abstract fun routeEntity(): RouteEntityDao
}