package com.examprepare.easybus.feature.repository

import com.examprepare.easybus.core.database.PTXDataBase
import com.examprepare.easybus.core.di.PTXResourceCityArray
import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.model.local.RouteLocalEntity
import com.examprepare.easybus.core.model.network.RouteNetworkEntity
import com.examprepare.easybus.core.platform.NetworkHandler
import com.examprepare.easybus.core.service.PTXService
import com.examprepare.easybus.feature.model.Route
import com.examprepare.easybus.feature.repository.exception.NoRouteFailure
import timber.log.Timber
import javax.inject.Inject

interface RouteRepository {
    suspend fun route(routeId: String): Either<Failure, Route>

    class Impl
    @Inject constructor(
        @PTXResourceCityArray private val resourceCityArray: Array<String>,
        private val networkHandler: NetworkHandler,
        private val service: PTXService,
        private val cacheDatabase: PTXDataBase
    ) : RouteRepository {

        override suspend fun route(routeId: String): Either<Failure, Route> {
            return try {
                when (networkHandler.isNetworkAvailable()) {
                    true -> {
                        //若沒有暫存則下載資料
                        val routeEntity = cacheDatabase.routeEntity().get(routeId).firstOrNull()
                        if (routeEntity == null) {
                            when (val fetchResult = fetchAndCache(routeId)) {
                                is Either.Left -> {
                                    Either.Left(fetchResult.a)
                                }
                                is Either.Right -> {
                                    Either.Right(fetchResult.b.toRoute())
                                }
                            }
                        } else {
                            Either.Right(routeEntity.toRoute())
                        }
                    }
                    false -> Either.Left(Failure.NetworkConnection)
                }
            } catch (exception: Throwable) {
                Timber.e(exception)
                Either.Left(Failure.ServerError)
            }
        }

        private suspend fun fetchAndCache(routeId: String): Either<Failure, RouteLocalEntity> {
            var entity: RouteNetworkEntity? = null
            for (city in resourceCityArray) {
                entity = service.getRoute(city, routeId)
                if (entity != null) break
            }
            val localEntity = entity?.let {
                RouteLocalEntity(
                    it.RouteID,
                    it.RouteName.Zh_tw,
                    it.DepartureStopNameZh,
                    it.DestinationStopNameZh
                )
            } ?: return Either.Left(NoRouteFailure)
            cacheDatabase.routeEntity().insertAll(localEntity)
            return Either.Right(localEntity)
        }
    }
}
