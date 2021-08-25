package com.examprepare.easybus.feature.repository

import com.examprepare.easybus.core.di.PTXResourceCityArray
import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
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
        private val service: PTXService
    ) : StationRepository {

        override suspend fun findNearStation(
            radius: Int,
            positionLat: Double,
            positionLon: Double
        ): Either<Failure, List<Station>> {
            return try {
                when (networkHandler.isNetworkAvailable()) {
                    true -> {
                        val stationEntities = mutableListOf<Station>()
                        resourceCityArray.forEach { city ->
                            stationEntities.addAll(service.findNearStation(
                                city,
                                positionLat,
                                positionLon,
                                radius
                            ).map { it.toStation() })
                        }

                        Either.Right(
                            stationEntities
                        )
                    }
                    false -> Either.Left(Failure.ServerError)
                }
            } catch (exception: Throwable) {
                Timber.e(exception)
                Either.Left(Failure.ServerError)
            }
        }
    }
}