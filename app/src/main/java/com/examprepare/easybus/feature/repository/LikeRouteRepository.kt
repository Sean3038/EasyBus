package com.examprepare.easybus.feature.repository

import com.examprepare.easybus.core.database.PersonalDataBase
import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.model.local.LikeRouteEntity
import com.examprepare.easybus.feature.model.LikeRoute
import javax.inject.Inject

interface LikeRouteRepository {
    suspend fun likesRoutes(): Either<Failure, List<LikeRoute>>
    suspend fun addLikeRoute(likeRoute: LikeRoute): Either<Failure, Unit>
    suspend fun removeLikeRoute(likeRoute: LikeRoute): Either<Failure, Unit>
    suspend fun isLikeRoute(routeId: String): Either<Failure, Boolean>

    class Impl
    @Inject constructor(
        private val personalDataBase: PersonalDataBase
    ) : LikeRouteRepository {
        override suspend fun likesRoutes(): Either<Failure, List<LikeRoute>> =
            Either.Right(personalDataBase.likeRouteDao().getAll().map { it.toLikeRoute() })


        override suspend fun addLikeRoute(likeRoute: LikeRoute): Either<Failure, Unit> {
            personalDataBase.likeRouteDao().insertAll(LikeRouteEntity(likeRoute.routeId))
            return Either.Right(Unit)
        }

        override suspend fun removeLikeRoute(likeRoute: LikeRoute): Either<Failure, Unit> {
            personalDataBase.likeRouteDao().delete(LikeRouteEntity(likeRoute.routeId))
            return Either.Right(Unit)
        }

        override suspend fun isLikeRoute(routeId: String): Either<Failure, Boolean> {
            return Either.Right(personalDataBase.likeRouteDao().get(routeId) != null)
        }

    }
}