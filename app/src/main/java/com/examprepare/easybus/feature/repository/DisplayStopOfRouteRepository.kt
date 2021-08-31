package com.examprepare.easybus.feature.repository

import com.examprepare.easybus.core.database.PTXDataBase
import com.examprepare.easybus.core.di.PTXResourceCityArray
import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.model.network.DisplayStopOfRouteEntity
import com.examprepare.easybus.core.platform.NetworkHandler
import com.examprepare.easybus.core.service.PTXService
import com.examprepare.easybus.feature.model.DisplayStopOfRoute
import timber.log.Timber

interface DisplayStopOfRouteRepository {

    suspend fun getDisplayStopOfRoute(routeId: String): Either<Failure, List<DisplayStopOfRoute>>

    class Impl(
        @PTXResourceCityArray private val resourceCityArray: Array<String>,
        private val networkHandler: NetworkHandler,
        private val service: PTXService,
        private val cacheDataBase: PTXDataBase
    ) : DisplayStopOfRouteRepository {

        override suspend fun getDisplayStopOfRoute(routeId: String): Either<Failure, List<DisplayStopOfRoute>> {
            return try {
                when (networkHandler.isNetworkAvailable()) {
                    true -> {
                        val resultEntities = mutableListOf<DisplayStopOfRouteEntity>()
                        for (city in resourceCityArray) {
                            resultEntities.addAll(service.getDisplayStopOfRoute(city, routeId))
                        }

                        Either.Right(resultEntities.map {
                            it.toDisplayStopOfRoute()
                        })
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