package com.examprepare.easybus.core.model.local

import androidx.room.*
import com.examprepare.easybus.feature.model.Stop

@Entity(tableName = "stop_entities")
data class StopLocalEntity(
    @PrimaryKey
    val stopId: String,
    val stopName: String,
    val positionLat: Double,
    val positionLon: Double
) {
    fun toStop(): Stop = Stop(stopId, stopName, positionLat, positionLon)
}