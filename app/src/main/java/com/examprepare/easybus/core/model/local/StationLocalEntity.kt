package com.examprepare.easybus.core.model.local

import androidx.room.*

@Entity(tableName = "station_entities")
data class StationLocalEntity(
    @PrimaryKey
    val stationID: String,
    val stationName: String,
    val stationAddress: String,
    val positionLat: Int,
    val positionLon: Int
) {

}