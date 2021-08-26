package com.examprepare.easybus.feature.repository

import com.examprepare.easybus.core.di.PTXResourceCityArray
import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.model.network.EstimatedTimeOfArrivalNetworkEntity
import com.examprepare.easybus.core.platform.NetworkHandler
import com.examprepare.easybus.core.service.PTXService
import com.examprepare.easybus.feature.model.EstimateTimeOfArrival
import timber.log.Timber

interface EstimateTimeOfArrivalRepository {

    suspend fun estimate(stopIds: List<String>): Either<Failure, List<EstimateTimeOfArrival>>

    class Impl(
        @PTXResourceCityArray private val resourceCityArray: Array<String>,
        private val networkHandler: NetworkHandler,
        private val service: PTXService
    ) : EstimateTimeOfArrivalRepository {
        override suspend fun estimate(stopIds: List<String>): Either<Failure, List<EstimateTimeOfArrival>> {
            return try {
                when (networkHandler.isNetworkAvailable()) {
                    true -> {
                        //若沒有暫存則下載資料
                        val resultEntities = mutableListOf<EstimatedTimeOfArrivalNetworkEntity>()
                        for (city in resourceCityArray) {
                            resultEntities.addAll(service.estimateTimeOfArrival(city, stopIds))
                        }
                        val result = resultEntities.map {
                            it.toEstimateTimeOfArrival()
                        }
                        Either.Right(result)
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