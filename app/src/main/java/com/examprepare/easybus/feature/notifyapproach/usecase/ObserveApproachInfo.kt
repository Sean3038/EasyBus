package com.examprepare.easybus.feature.notifyapproach.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.feature.model.Direction
import com.examprepare.easybus.feature.notifyapproach.exception.NotifyApproachFailure
import com.examprepare.easybus.feature.notifyapproach.model.ApproachInfo
import com.examprepare.easybus.feature.repository.EstimateTimeOfArrivalRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class ObserveApproachInfo @Inject constructor(
    private val estimateTimeOfArrivalRepository: EstimateTimeOfArrivalRepository
) : UseCase<ApproachInfo, ObserveApproachInfo.Params>() {

    override suspend fun run(params: Params): Either<Failure, ApproachInfo> {
        while (true) {
            val estimateResult = when (val result = estimateTimeOfArrivalRepository.estimate(
                params.routeId,
                params.stopId,
                params.direction
            )) {
                is Either.Left -> {
                    return result
                }
                is Either.Right -> {
                    result.b
                }
            }

            if (estimateResult.estimateTime == null) {
                return Either.Left(NotifyApproachFailure.NoEstimateArrivalFailure)
            }

            if (estimateResult.estimateTime < params.targetMinute * 60) return Either.Right(
                ApproachInfo(
                    estimateResult.routeId,
                    estimateResult.routeName,
                    estimateResult.stopName,
                    params.targetMinute
                )
            ) else {
                //estimate arrival time of route every minute
                delay(60000)
            }
        }
    }

    data class Params(
        val routeId: String,
        val stopId: String,
        val direction: Direction,
        val targetMinute: Int
    )
}