package com.examprepare.easybus.feature.searchnearstop.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.feature.repository.StopRepository
import com.examprepare.easybus.feature.model.Stop
import javax.inject.Inject


class GetNearStops @Inject constructor(private val stopRepository: StopRepository) :
    UseCase<List<Stop>, GetNearStops.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<Stop>> =
        stopRepository.findNearStop(params.lat, params.lon)

    data class Params(val lat: Double, val lon: Double)
}