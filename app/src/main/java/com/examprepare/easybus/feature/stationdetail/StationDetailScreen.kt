package com.examprepare.easybus.feature.stationdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.examprepare.easybus.Const
import com.examprepare.easybus.core.ui.FailureDialog
import com.examprepare.easybus.core.ui.FailureView
import com.examprepare.easybus.core.ui.StopStatusBadge
import com.examprepare.easybus.core.ui.TitleBar
import com.examprepare.easybus.feature.model.Station
import com.examprepare.easybus.feature.repository.exception.NoStationFailure
import com.examprepare.easybus.feature.stationdetail.model.EstimateRoute
import com.examprepare.easybus.ui.theme.Red400
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StationDetailScreen(
    viewModel: StationDetailViewModel,
    stationId: String,
    toRoute: (String) -> Unit,
    navigateBack: () -> Unit
) {
    val station = viewModel.station.collectAsState().value
    val routes = viewModel.routes.collectAsState().value
    val failure = viewModel.failure.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.getStation(stationId)
    }

    if (station != Station.empty) {
        val scope = rememberCoroutineScope()
        DisposableEffect(station) {
            val job: Job = scope.launch {
                while (true) {
                    viewModel.getEstimateRoutes(station.stopItems)
                    delay(Const.UPDATE_REALTIME_STATION_TIME_INTERVAL_MILLISECONDS)
                }
            }
            onDispose {
                job.cancel()
            }
        }
    }

    StationDetail(
        station = station,
        routes = routes,
        toRoute = toRoute,
        onBack = navigateBack
    )

    when (failure) {
        NoStationFailure -> {
            FailureDialog("找不到該站位", onDismissCallback = viewModel::onDismissFailure)
        }
        else -> {
            FailureView(failure = failure, onDismissCallback = viewModel::onDismissFailure)
        }
    }
}

@Composable
fun StationDetail(
    station: Station,
    routes: List<EstimateRoute>,
    toRoute: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(topBar = {
        TitleBar(station.stationName, onBack)
    }) {
        LazyColumn {
            items(routes) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            toRoute(it.routeId)
                        },
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp
                ) {
                    Row(modifier = Modifier.padding(8.dp)) {
                        StopStatusBadge(estimateTime = it.estimateTime, stopStatus = it.stopStatus)

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(text = it.routeName, style = MaterialTheme.typography.h5)
                            Text(
                                "${it.departureStopName} - ${it.destinationStopName}",
                                style = MaterialTheme.typography.subtitle1
                            )
                        }
                        it.plateNumber?.let { plateNumber ->
                            Text(text = plateNumber, style = MaterialTheme.typography.subtitle1)
                            if (it.isLastBus == true) {
                                Text(
                                    text = "(末班車)",
                                    color = Red400,
                                    style = MaterialTheme.typography.subtitle1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}