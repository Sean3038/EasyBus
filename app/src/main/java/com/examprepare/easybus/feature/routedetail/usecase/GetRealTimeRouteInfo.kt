package com.examprepare.easybus.feature.routedetail.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.feature.model.*
import com.examprepare.easybus.feature.repository.DisplayStopOfRouteRepository
import com.examprepare.easybus.feature.repository.EstimateTimeOfArrivalRepository
import com.examprepare.easybus.feature.repository.RouteRepository
import com.examprepare.easybus.feature.routedetail.model.RealTimeRouteInfo
import javax.inject.Inject

class GetRealTimeRouteInfo @Inject constructor(
    private val routeRepository: RouteRepository,
    private val displayStopOfRouteRepository: DisplayStopOfRouteRepository,
    private val estimateTimeOfArrivalRepository: EstimateTimeOfArrivalRepository
) : UseCase<List<RealTimeRouteInfo>, GetRealTimeRouteInfo.Params>() {

    private val realTimeRouteInfoFactory = RealTimeRouteInfoFactory()

    override suspend fun run(params: Params): Either<Failure, List<RealTimeRouteInfo>> {
        val route =
            when (val result = routeRepository.route(params.routeId)) {
                is Either.Left -> {
                    return result
                }
                is Either.Right -> {
                    result.b
                }
            }

        val displayStopOfRoutes =
            when (val result = displayStopOfRouteRepository.getDisplayStopOfRoute(params.routeId)) {
                is Either.Left -> {
                    return result
                }
                is Either.Right -> {
                    result.b
                }
            }

        val estimateTimeOfArrival =
            when (val result = estimateTimeOfArrivalRepository.estimate(params.routeId)) {
                is Either.Left -> {
                    return result
                }
                is Either.Right -> {
                    result.b
                }
            }

        return Either.Right(
            realTimeRouteInfoFactory.create(
                route,
                displayStopOfRoutes,
                estimateTimeOfArrival
            )
        )
    }

    data class Params(val routeId: String)

    private class RealTimeRouteInfoFactory {

        fun create(
            route: Route,
            displayStopOfRoutes: List<DisplayStopOfRoute>,
            estimateTimeOfArrivals: List<EstimateTimeOfArrival>
        ): List<RealTimeRouteInfo> {

            val infoMap = mutableMapOf<Direction, List<RealTimeRouteInfo.StopItem>>()
            displayStopOfRoutes.forEach {
                infoMap[it.direction] = it.getEstimateTimeStops(estimateTimeOfArrivals)
            }
            return infoMap.toSortedMap { o1, o2 ->
                o1.index.compareTo(o2.index)
            }.map {
                RealTimeRouteInfo(getDestinationName(route, it.key), it.value)
            }
        }

        private fun DisplayStopOfRoute.getEstimateTimeStops(
            estimateTimeOfArrivals: List<EstimateTimeOfArrival>
        ): List<RealTimeRouteInfo.StopItem> {
            val result = mutableListOf<RealTimeRouteInfo.StopItem>()
            stops.forEach { stop ->

                val estimateTimeOfArrival = estimateTimeOfArrivals
                    .find { estimateArrival ->
                        estimateArrival.stopId == stop.stopId
                                && estimateArrival.direction == direction
                    }

                result.add(
                    RealTimeRouteInfo.StopItem(
                        stop.stopId,
                        stop.stationId,
                        stop.stopName,
                        estimateTimeOfArrival?.stopStatus ?: StopStatus.None,
                        stop.stopSequence,
                        estimateTimeOfArrival?.direction ?: Direction.UnKnown,
                        estimateTimeOfArrival?.plateNumb,
                        estimateTimeOfArrival?.estimateTime,
                        estimateTimeOfArrival?.isLastBus == true
                    )
                )
            }
            result.sortBy { it.stopSequence }
            return result
        }

        private fun getDestinationName(
            route: Route,
            direction: Direction
        ): String = when (direction) {
            Direction.UnKnown -> "未知"
            Direction.Departure -> "往${route.destinationStopName}"
            Direction.Loop -> "${route.departureStopName}<->${route.destinationStopName}"
            Direction.Return -> "往${route.departureStopName}"
        }
    }
}