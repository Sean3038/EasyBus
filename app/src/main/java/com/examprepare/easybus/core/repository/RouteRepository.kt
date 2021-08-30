package com.examprepare.easybus.core.repository

import com.examprepare.easybus.core.database.PTXDataBase
import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.model.RouteLocalEntity
import com.examprepare.easybus.core.platform.NetworkHandler
import com.examprepare.easybus.core.service.PTXService
import com.examprepare.easybus.feature.searchroute.domain.model.Route
import timber.log.Timber
import javax.inject.Inject

interface RouteRepository {
    suspend fun search(routeName: String): Either<Failure, List<Route>>
    suspend fun routes(): Either<Failure, List<Route>>


    class Impl
    @Inject constructor(
        private val networkHandler: NetworkHandler,
        private val service: PTXService,
        private val cacheDatabase: PTXDataBase
    ) : RouteRepository {

        override suspend fun search(routeName: String): Either<Failure, List<Route>> {
            return try {
                when (networkHandler.isNetworkAvailable()) {
                    true -> {
                        //若沒有暫存則下載資料
                        val routeEntity = cacheDatabase.routeEntity().getAll()
                            .takeIf { it.isNotEmpty() } ?: fetchAndCache()

                        Either.Right(
                            routeEntity
                                .filter { it.RouteName.startsWith(routeName) }
                                .map { Route(it.RouteID, it.RouteName) }
                                .toList()
                        )
                    }
                    false -> Either.Left(Failure.ServerError)
                }
            } catch (exception: Throwable) {
                Timber.e(exception)
                Either.Left(Failure.ServerError)
            }
        }

        override suspend fun routes(): Either<Failure, List<Route>> =
            try {
                when (networkHandler.isNetworkAvailable()) {
                    true -> {
                        //若沒有暫存則下載資料
                        val routeEntities = cacheDatabase.routeEntity().getAll()
                            .takeIf { it.isNotEmpty() } ?: fetchAndCache()

                        Either.Right(
                            routeEntities
                                .map { Route(it.RouteID, it.RouteName) }
                                .toList()
                        )
                    }
                    false -> Either.Left(Failure.ServerError)
                }
            } catch (exception: Throwable) {
                Timber.e(exception)
                Either.Left(Failure.ServerError)
            }

        private suspend fun fetchAndCache(): List<RouteLocalEntity> {
            val localEntity = service.getRoutes()
                .map {
                    RouteLocalEntity(
                        it.RouteID,
                        it.RouteName.Zh_tw,
                        it.DepartureStopNameZh,
                        it.DestinationStopNameZh
                    )
                }
            cacheDatabase.routeEntity().insertAll(*localEntity.toTypedArray())
            return localEntity
        }
    }
}
