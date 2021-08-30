package com.examprepare.easybus.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.examprepare.easybus.core.model.LikeRouteEntity
import com.examprepare.easybus.core.model.LikeRouteDao

@Database(entities = [LikeRouteEntity::class], version = 1)
abstract class PersonalDataBase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "personal_database"
    }

    abstract fun likeRouteDao(): LikeRouteDao
}