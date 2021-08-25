package com.examprepare.easybus.feature.searchnearstop.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.feature.model.Station
import com.examprepare.easybus.feature.repository.StationRepository
import javax.inject.Inject

class GetNearStation @Inject constructor(private val stationRepository: StationRepository) :
    UseCase<List<Station>, GetNearStation.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<Station>> =
        stationRepository.findNearStation(params.lat, params.lon)

    data class Params(val lat: Double, val lon: Double)
}