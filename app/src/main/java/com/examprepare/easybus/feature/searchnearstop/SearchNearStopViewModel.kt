package com.examprepare.easybus.feature.searchnearstop

import androidx.lifecycle.viewModelScope
import com.examprepare.easybus.core.platform.BaseViewModel
import com.examprepare.easybus.feature.model.Stop
import com.examprepare.easybus.feature.searchnearstop.usecase.GetNearStops
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchNearStopViewModel @Inject constructor(
    private val getNearStops: GetNearStops
) : BaseViewModel() {

    private val _nearStops: MutableStateFlow<List<Stop>> = MutableStateFlow(emptyList())
    val nearStops = _nearStops.asStateFlow()

    fun searchNearStops(currentLatitude: Double, currentLongitude: Double) {
        viewModelScope.launch {
            getNearStops.run(GetNearStops.Params(currentLatitude, currentLongitude))
                .fold(::handleFailure, ::handleGetNearStops)
        }
    }

    private fun handleGetNearStops(stops: List<Stop>) {
        _nearStops.value = stops
    }
}