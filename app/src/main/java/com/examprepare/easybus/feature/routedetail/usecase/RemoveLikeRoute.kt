package com.examprepare.easybus.feature.routedetail.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.feature.repository.LikeRouteRepository
import com.examprepare.easybus.feature.model.LikeRoute
import javax.inject.Inject

class RemoveLikeRoute @Inject constructor(private val likeRouteRepository: LikeRouteRepository) :
    UseCase<Unit, RemoveLikeRoute.Params>() {

    override suspend fun run(params: Params): Either<Failure, Unit> =
        likeRouteRepository.removeLikeRoute(LikeRoute(params.routeId))

    data class Params(val routeId: String)
}