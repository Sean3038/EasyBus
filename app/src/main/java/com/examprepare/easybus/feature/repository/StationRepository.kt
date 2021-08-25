package com.examprepare.easybus.feature.repository

import android.location.Location
import com.examprepare.easybus.core.database.PTXDataBase
import com.examprepare.easybus.core.di.PTXResourceCityArray
import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.model.local.StationLocalEntity
import com.examprepare.easybus.core.platform.NetworkHandler
import com.examprepare.easybus.core.service.PTXService
import com.examprepare.easybus.feature.model.Station
import timber.log.Timber

interface StationRepository {

    suspend fun findNearStation(
        radius: Int,
        positionLat: Double,
        positionLon: Double
    ): Either<Failure, List<Station>>

    class Impl constructor(
        @PTXResourceCityArray private val resourceCityArray: Array<String>,
        private val networkHandler: NetworkHandler,
        private val service: PTXService,
        private val cacheDatabase: PTXDataBase
    ) : StationRepository {

        override suspend fun findNearStation(
            radius: Int,
            positionLat: Double,
            positionLon: Double
        ): Either<Failure, List<Station>> {
            return try {
                when (networkHandler.isNetworkAvailable()) {
                    true -> {
                        //若沒有暫存則下載資料
                        val stationEntities = cacheDatabase.stationEntity().getAll()
                            .takeIf { it.isNotEmpty() } ?: fetchAndCache()

                        Either.Right(
                            stationEntities.filter {
                                it.nearPosition(radius, positionLat, positionLon)
                            }.map {
                                it.toStation()
                            }
                        )
                    }
                    false -> Either.Left(Failure.ServerError)
                }
            } catch (exception: Throwable) {
                Timber.e(exception)
                Either.Left(Failure.ServerError)
            }
        }

        private suspend fun fetchAndCache(): List<StationLocalEntity> {
            val entities = mutableListOf<StationLocalEntity>()
            for (city in resourceCityArray) {
                entities.addAll(
                    service.getStations(city).map {
                        StationLocalEntity(
                            it.stationID,
                            it.stationName.zhTw,
                            it.stationAddress,
                            it.stationPosition.positionLat,
                            it.stationPosition.positionLon
                        )
                    })
            }
            cacheDatabase.stationEntity().insertAll(*entities.toTypedArray())
            return entities
        }

        private fun StationLocalEntity.nearPosition(
            radius: Int,
            targetLat: Double,
            targetLon: Double
        ): Boolean {
            val result = FloatArray(3)
            Location.distanceBetween(positionLat, positionLon, targetLat, targetLon, result)
            return result[0] < radius
        }
    }
}