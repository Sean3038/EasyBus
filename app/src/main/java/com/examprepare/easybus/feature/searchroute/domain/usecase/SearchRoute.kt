package com.examprepare.easybus.feature.searchroute.domain.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.core.repository.SearchRouteRepository
import com.examprepare.easybus.feature.searchroute.domain.model.Route
import com.examprepare.easybus.feature.searchroute.domain.model.SearchRouteResult
import javax.inject.Inject

class SearchRoute @Inject constructor(private val searchRouteRepository: SearchRouteRepository) :
    UseCase<SearchRouteResult, SearchRoute.Params>() {

    override suspend fun run(params: Params): Either<Failure, SearchRouteResult> =
        searchRouteRepository.searchRoute(params.keyword)

    data class Params(val keyword: String)

}
