package com.examprepare.easybus.feature.stationdetail.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.feature.model.EstimateTimeOfArrival
import com.examprepare.easybus.feature.model.StopStatus
import com.examprepare.easybus.feature.repository.EstimateTimeOfArrivalRepository
import com.examprepare.easybus.feature.repository.RouteRepository
import com.examprepare.easybus.feature.stationdetail.model.EstimateRoute
import javax.inject.Inject

class GetEstimateRoutes @Inject constructor(
    private val estimateTimeOfArrivalRepository: EstimateTimeOfArrivalRepository,
    private val routeRepository: RouteRepository
) :
    UseCase<List<EstimateRoute>, GetEstimateRoutes.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<EstimateRoute>> {
        val estimateTimeOfArrivals = mutableListOf<EstimateTimeOfArrival>()
        when (val result = estimateTimeOfArrivalRepository.estimate(params.stopIds)) {
            is Either.Left -> {
                return result
            }
            is Either.Right -> {
                estimateTimeOfArrivals.addAll(result.b)
            }
        }
        val estimateRoutes = estimateTimeOfArrivals.map {
            when (val result = routeRepository.route(it.routeId)) {
                is Either.Left -> {
                    return result
                }
                is Either.Right -> {
                    val route = result.b
                    EstimateRoute(
                        route.routeId,
                        route.routeName,
                        route.departureStopName,
                        route.destinationSStopName,
                        when (it.stopStatus) {
                            0 -> if (it.estimateTime < 60) StopStatus.OnPulledIN else StopStatus.Normal
                            1 -> StopStatus.NoneDeparture
                            2 -> StopStatus.NonStop
                            3 -> StopStatus.NoShift
                            4 -> StopStatus.NoOperation
                            else -> StopStatus.None
                        },
                        it.plateNumb,
                        it.estimateTime,
                        it.isLastBus
                    )
                }
            }

        }
        return Either.Right(estimateRoutes)
    }

    data class Params(val stopIds: List<String>)
}