package com.examprepare.easybus.feature.stationdetail.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.feature.model.Route
import com.examprepare.easybus.feature.repository.RouteRepository
import javax.inject.Inject

class GetRoutes @Inject constructor(private val routeRepository: RouteRepository) :
    UseCase<List<Route>, GetRoutes.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<Route>> =
        routeRepository.getRoutes(params.routeIds)

    data class Params(val routeIds: List<String>)
}