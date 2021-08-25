package com.examprepare.easybus.feature.stationdetail

import androidx.lifecycle.viewModelScope
import com.examprepare.easybus.core.platform.BaseViewModel
import com.examprepare.easybus.feature.model.Station
import com.examprepare.easybus.feature.stationdetail.usecase.GetStation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class StationDetailViewModel constructor(
    private val getStation: GetStation
) : BaseViewModel() {

    private val _station = MutableStateFlow(Station.empty)
    val station = _station.asStateFlow()

    fun getStation(stationId: String) {
        viewModelScope.launch {
            getStation(GetStation.Params(stationId)) {
                it.fold(::handleFailure, ::handleGetStation)
            }
        }
    }

    private fun handleGetStation(station: Station) {
        _station.value = station
    }
}