package com.examprepare.easybus.feature.stationdetail.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.feature.model.Station
import com.examprepare.easybus.feature.repository.StationRepository
import javax.inject.Inject

class GetStation @Inject constructor(private val stationRepository: StationRepository) :
    UseCase<Station, GetStation.Params>() {

    override suspend fun run(params: Params): Either<Failure, Station> =
        stationRepository.getStation(params.stationId)

    data class Params(val stationId: String)

}