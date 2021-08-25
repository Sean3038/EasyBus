package com.examprepare.easybus.feature.routedetail.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.feature.repository.LikeRouteRepository
import javax.inject.Inject

class IsLikeRoute @Inject constructor(private val likeRouteRepository: LikeRouteRepository) :
    UseCase<Boolean, IsLikeRoute.Params>() {

    override suspend fun run(params: Params): Either<Failure, Boolean> =
        likeRouteRepository.isLikeRoute(params.routeId)

    data class Params(val routeId: String)
}