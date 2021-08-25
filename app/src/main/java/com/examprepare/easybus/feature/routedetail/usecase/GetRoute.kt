package com.examprepare.easybus.feature.routedetail.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.feature.repository.RouteRepository
import com.examprepare.easybus.feature.model.Route
import javax.inject.Inject


class GetRoute @Inject constructor(private val routeRepository: RouteRepository) :
    UseCase<Route, GetRoute.Params>() {

    override suspend fun run(params: Params): Either<Failure, Route> =
        routeRepository.route(params.routeId)

    data class Params(val routeId: String)
}