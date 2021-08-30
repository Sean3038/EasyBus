package com.examprepare.easybus.feature.home.domain.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.core.repository.LikeRouteRepository
import com.examprepare.easybus.core.repository.RouteRepository
import com.examprepare.easybus.feature.home.domain.model.FavoriteRoute
import javax.inject.Inject

class GetFavoriteRoutes @Inject constructor(
    private val likeRouteRepository: LikeRouteRepository,
    private val routeRepository: RouteRepository
) :
    UseCase<List<FavoriteRoute>, UseCase.None>() {


    override suspend fun run(params: None): Either<Failure, List<FavoriteRoute>> {
        val likeRoutes = when (val result = likeRouteRepository.likesRoutes()) {
            is Either.Left -> {
                return result
            }
            is Either.Right -> {
                result.b
            }
        }
        val routesResult = when (val result = routeRepository.routes()) {
            is Either.Left -> {
                return result
            }
            is Either.Right -> {
                result.b
            }
        }

        return Either.Right(routesResult
            .filter { route ->
                likeRoutes.any { it.routeId == route.routeId }
            }.map {
                FavoriteRoute(it.routeId, it.routeName, true)
            })
    }

}