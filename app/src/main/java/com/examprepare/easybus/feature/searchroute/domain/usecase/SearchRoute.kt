package com.examprepare.easybus.feature.searchroute.domain.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.feature.searchroute.domain.model.Route
import com.examprepare.easybus.core.repository.RouteRepository
import javax.inject.Inject

class SearchRoute @Inject constructor(private val routeRepository: RouteRepository) :
    UseCase<List<Route>, SearchRoute.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<Route>> =
        routeRepository.search(params.routeName)

    data class Params(val routeName: String)

}
