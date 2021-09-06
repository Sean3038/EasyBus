package com.examprepare.easybus.feature.repository

import com.examprepare.easybus.core.di.PTXResourceCityArray
import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.platform.NetworkHandler
import com.examprepare.easybus.core.service.PTXService
import com.examprepare.easybus.feature.model.Station
import com.examprepare.easybus.feature.repository.exception.NoStationFailure
import timber.log.Timber

interface StationRepository {

    suspend fun getStation(stationID: String): Either<Failure, Station>
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
        override suspend fun getStation(stationID: String): Either<Failure, Station> {
            return try {
                when (networkHandler.isNetworkAvailable()) {
                    true -> {
                        resourceCityArray.forEach { city ->
                            val result = service.getStations(city, stationID)
                            if (result.isNotEmpty()) return Either.Right(result[0].toStation())
                        }

                        Either.Left(NoStationFailure)
                    }
                    false -> Either.Left(Failure.NetworkConnection)
                }
            } catch (exception: Throwable) {
                Timber.e(exception)
                Either.Left(Failure.ServerError)
            }
        }

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

                        Either.Right(stationEntities)
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