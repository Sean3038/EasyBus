package com.examprepare.easybus.feature.stationdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.examprepare.easybus.core.ui.FailureView
import com.examprepare.easybus.core.ui.TitleBar
import com.examprepare.easybus.feature.model.Station
import com.examprepare.easybus.feature.stationdetail.exception.NoStationFailure

@Composable
fun StationDetailScreen(
    viewModel: StationDetailViewModel,
    stationId: String,
    toRoute: (String) -> Unit,
    navigateBack: () -> Unit
) {
    val station = viewModel.station.collectAsState().value
    val failure = viewModel.failure.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.getStation(stationId)
    }

    StationDetail(
        station = station,
        toRoute = toRoute,
        navigateBack = navigateBack
    )
    if (failure is NoStationFailure) {
        TODO("Station not found")
    } else {
        FailureView(failure = failure)
    }

}

@Composable
fun StationDetail(station: Station, toRoute: (String) -> Unit, navigateBack: () -> Unit) {
    Scaffold(topBar = { TitleBar() }) {
        Column {
            Text(station.stationName)
        }
    }
}