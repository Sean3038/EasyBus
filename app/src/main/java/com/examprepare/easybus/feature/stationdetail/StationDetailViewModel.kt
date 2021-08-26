package com.examprepare.easybus.feature.stationdetail

import androidx.lifecycle.viewModelScope
import com.examprepare.easybus.core.platform.BaseViewModel
import com.examprepare.easybus.feature.model.Station
import com.examprepare.easybus.feature.stationdetail.model.EstimateRoute
import com.examprepare.easybus.feature.stationdetail.usecase.GetEstimateRoutes
import com.examprepare.easybus.feature.stationdetail.usecase.GetStation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StationDetailViewModel @Inject constructor(
    private val getStation: GetStation,
    private val getEstimateRoutes: GetEstimateRoutes
) : BaseViewModel() {

    private val _station = MutableStateFlow(Station.empty)
    val station = _station.asStateFlow()

    private val _routes = MutableStateFlow(emptyList<EstimateRoute>())
    val routes = _routes.asStateFlow()

    fun getStation(stationId: String) {
        viewModelScope.launch {
            getStation(GetStation.Params(stationId)) {
                it.fold(::handleFailure, ::handleGetStation)
            }
        }
    }

    private fun handleGetStation(station: Station) {
        _station.value = station
        viewModelScope.launch {
            val routeList = station.stopItems.map {
                it.stopId
            }
            getEstimateRoutes(GetEstimateRoutes.Params(routeList)) {
                it.fold(::handleFailure, ::handleGetRoutes)
            }
        }
    }

    private fun handleGetRoutes(routes: List<EstimateRoute>) {
        _routes.value = routes
    }
}