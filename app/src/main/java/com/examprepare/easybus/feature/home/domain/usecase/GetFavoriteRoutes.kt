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
        val favoriteRoutes = mutableListOf<FavoriteRoute>()
        val likeRoutes = when (val result = likeRouteRepository.likesRoutes()) {
            is Either.Left -> {
                return result
            }
            is Either.Right -> {
                result.b
            }
        }
        for (likeItem in likeRoutes) {
            val routesResult = when (val result = routeRepository.route(likeItem.routeId)) {
                is Either.Left -> {
                    return result
                }
                is Either.Right -> {
                    result.b
                }
            }
            favoriteRoutes.add(FavoriteRoute(routesResult.routeId, routesResult.routeName, true))
        }

        return Either.Right(favoriteRoutes)
    }

}