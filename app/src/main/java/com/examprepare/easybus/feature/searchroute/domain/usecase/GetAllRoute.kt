package com.examprepare.easybus.feature.searchroute.domain.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.feature.searchroute.domain.model.Route
import com.examprepare.easybus.core.repository.RouteRepository
import javax.inject.Inject

class GetAllRoute @Inject constructor(private val routeRepository: RouteRepository) :
    UseCase<List<Route>, UseCase.None>() {

    override suspend fun run(params: None): Either<Failure, List<Route>> = routeRepository.routes()
}