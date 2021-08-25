package com.examprepare.easybus.feature.repository

import com.examprepare.easybus.core.di.PTXResourceCityArray
import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.model.network.SearchRouteNetworkEntity
import com.examprepare.easybus.core.platform.NetworkHandler
import com.examprepare.easybus.core.service.PTXService
import com.examprepare.easybus.feature.model.SearchRouteResult
import timber.log.Timber
import javax.inject.Inject

interface SearchRouteRepository {
    suspend fun searchRoute(keyword: String): Either<Failure, SearchRouteResult>

    class Impl @Inject constructor(
        @PTXResourceCityArray private val resourceCityArray: Array<String>,
        private val networkHandler: NetworkHandler,
        private val service: PTXService
    ) : SearchRouteRepository {

        override suspend fun searchRoute(keyword: String): Either<Failure, SearchRouteResult> {
            return try {
                when (networkHandler.isNetworkAvailable()) {
                    true -> {
                        val resultEntity = mutableListOf<SearchRouteNetworkEntity>()
                        for (city in resourceCityArray) {
                            resultEntity.addAll(service.searchRoute(city, keyword))
                        }

                        Either.Right(
                            SearchRouteResult(resultEntity.map {
                                SearchRouteResult.Item(it.routeID, it.routeName.Zh_tw)
                            })
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