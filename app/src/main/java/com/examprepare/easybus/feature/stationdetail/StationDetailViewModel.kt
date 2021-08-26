package com.examprepare.easybus.feature.stationdetail

import androidx.lifecycle.viewModelScope
import com.examprepare.easybus.core.platform.BaseViewModel
import com.examprepare.easybus.feature.model.Route
import com.examprepare.easybus.feature.model.Station
import com.examprepare.easybus.feature.stationdetail.usecase.GetRoutes
import com.examprepare.easybus.feature.stationdetail.usecase.GetStation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StationDetailViewModel @Inject constructor(
    private val getStation: GetStation,
    private val getRoutes: GetRoutes
) : BaseViewModel() {

    private val _station = MutableStateFlow(Station.empty)
    val station = _station.asStateFlow()

    private val _routes = MutableStateFlow(emptyList<Route>())
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
            val routeList = station.routeItems.map {
                it.routeId
            }
            getRoutes(GetRoutes.Params(routeList)) {
                it.fold(::handleFailure, ::handleGetRoutes)
            }
        }
    }

    private fun handleGetRoutes(routes: List<Route>) {
        _routes.value = routes
    }
}