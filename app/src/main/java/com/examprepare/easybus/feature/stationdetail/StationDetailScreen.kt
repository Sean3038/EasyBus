package com.examprepare.easybus.feature.stationdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.examprepare.easybus.Const
import com.examprepare.easybus.core.ui.FailureView
import com.examprepare.easybus.feature.model.Station
import com.examprepare.easybus.feature.model.StopStatus
import com.examprepare.easybus.feature.stationdetail.exception.NoStationFailure
import com.examprepare.easybus.feature.stationdetail.model.EstimateRoute
import com.examprepare.easybus.ui.theme.Blue400
import com.examprepare.easybus.ui.theme.Gray500
import com.examprepare.easybus.ui.theme.Green400
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
    if (failure is NoStationFailure) {
        TODO("Station not found")
    } else {
        FailureView(failure = failure)
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
        TopAppBar(
            title = {
                Text(station.stationName)
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Sharp.ArrowBack, contentDescription = "退回上一頁")
                }
            }
        )
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
                        Surface(
                            modifier = Modifier
                                .width(90.dp)
                                .height(60.dp),
                            color =
                            when (it.stopStatus) {
                                StopStatus.NoOperation, StopStatus.NoShift, StopStatus.NoneDeparture -> Gray500
                                StopStatus.NonStop -> Red400
                                StopStatus.Normal -> if (it.estimateTime != null) Blue400 else MaterialTheme.colors.surface
                                StopStatus.OnPulledIN -> Green400
                                else -> MaterialTheme.colors.surface
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                when (it.stopStatus) {
                                    StopStatus.NoOperation ->
                                        Text(
                                            text = "今日停駛",
                                            style = MaterialTheme.typography.h6,
                                            textAlign = TextAlign.Center
                                        )
                                    StopStatus.NoShift ->
                                        Text(
                                            text = "末班已過",
                                            style = MaterialTheme.typography.h6,
                                            textAlign = TextAlign.Center
                                        )
                                    StopStatus.NonStop ->
                                        Text(
                                            text = "不停靠",
                                            style = MaterialTheme.typography.h6,
                                            textAlign = TextAlign.Center
                                        )
                                    StopStatus.None -> {
                                    }
                                    StopStatus.NoneDeparture ->
                                        Text(
                                            text = "尚未發車",
                                            style = MaterialTheme.typography.h6,
                                            textAlign = TextAlign.Center
                                        )
                                    StopStatus.Normal ->
                                        if (it.estimateTime != null) {
                                            Text(
                                                text = "${it.estimateTime / 60}分",
                                                style = MaterialTheme.typography.h5,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    StopStatus.OnPulledIN -> {
                                        Text(
                                            text = "即將進站",
                                            style = MaterialTheme.typography.h6,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

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