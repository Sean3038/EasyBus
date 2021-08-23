package com.examprepare.easybus.core.repository

import android.location.Location
import com.examprepare.easybus.core.database.PTXDataBase
import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.model.StopLocalEntity
import com.examprepare.easybus.core.platform.NetworkHandler
import com.examprepare.easybus.core.service.PTXService
import com.examprepare.easybus.feature.searchnearstop.domain.model.Stop
import timber.log.Timber
import javax.inject.Inject

interface StopRepository {
    companion object {
        const val NEAR_STOP_RADIUS_METERS = 100
    }

    suspend fun findNearStop(positionLat: Double, positionLon: Double): Either<Failure, List<Stop>>


    class Impl
    @Inject constructor(
        private val networkHandler: NetworkHandler,
        private val service: PTXService,
        private val cacheDatabase: PTXDataBase
    ) : StopRepository {

        override suspend fun findNearStop(
            positionLat: Double,
            positionLon: Double
        ): Either<Failure, List<Stop>> {
            return try {
                when (networkHandler.isNetworkAvailable()) {
                    true -> {
                        //若沒有暫存則下載資料
                        val stopEntity = cacheDatabase.stopEntity().getAll()
                            .takeIf { it.isNotEmpty() } ?: fetchAndCache()

                        Either.Right(
                            stopEntity.filter {
                                it.nearPosition(positionLat, positionLat)
                            }.map {
                                it.toStop()
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

        private fun StopLocalEntity.nearPosition(
            targetLat: Double,
            targetLon: Double
        ): Boolean {
            val result = FloatArray(3)
            Location.distanceBetween(positionLat, positionLon, targetLat, targetLon, result)
            return result[0] < NEAR_STOP_RADIUS_METERS
        }

        private suspend fun fetchAndCache(): List<StopLocalEntity> {
            val localEntity = service.getStops()
                .map {
                    StopLocalEntity(
                        it.stopID,
                        it.stopName.zhTw,
                        it.stopPosition.positionLat,
                        it.stopPosition.positionLon
                    )
                }
            cacheDatabase.stopEntity().insertAll(*localEntity.toTypedArray())
            return localEntity
        }
    }
}