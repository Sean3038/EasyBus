package com.examprepare.easybus.feature.searchnearstop

import androidx.lifecycle.viewModelScope
import com.examprepare.easybus.core.platform.BaseViewModel
import com.examprepare.easybus.feature.model.Station
import com.examprepare.easybus.feature.searchnearstop.usecase.GetNearStation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchNearStopViewModel @Inject constructor(
    private val getNearStation: GetNearStation
) : BaseViewModel() {

    private val _nearStations: MutableStateFlow<List<Station>> = MutableStateFlow(emptyList())
    val nearStations = _nearStations.asStateFlow()

    fun searchNearStops(radius: Int, currentLatitude: Double, currentLongitude: Double) {
        viewModelScope.launch {
            getNearStation(GetNearStation.Params(radius, currentLatitude, currentLongitude)) {
                it.fold(::handleFailure, ::handleGetNearStops)
            }
        }
    }

    private fun handleGetNearStops(stations: List<Station>) {
        _nearStations.value = stations
    }
}