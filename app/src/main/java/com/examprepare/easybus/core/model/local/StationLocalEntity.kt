package com.examprepare.easybus.core.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.examprepare.easybus.feature.model.Station

@Entity(tableName = "station_entities")
data class StationLocalEntity(
    @PrimaryKey
    val stationID: String,
    val stationName: String,
    val stationAddress: String,
    val positionLat: Double,
    val positionLon: Double
) {
    fun toStation(): Station = Station(
        stationID, stationName, stationAddress, positionLat, positionLon
    )
}