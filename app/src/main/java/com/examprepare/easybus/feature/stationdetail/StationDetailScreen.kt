package com.examprepare.easybus.feature.stationdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.examprepare.easybus.core.ui.FailureView
import com.examprepare.easybus.feature.model.Station
import com.examprepare.easybus.feature.stationdetail.exception.NoStationFailure
import com.examprepare.easybus.feature.stationdetail.model.EstimateRoute
import com.examprepare.easybus.feature.stationdetail.model.StopStatus

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

                        when (it.stopStatus) {
                            StopStatus.NoOperation ->
                                Text(
                                    modifier = Modifier.width(80.dp),
                                    text = "今日未營運"
                                )
                            StopStatus.NoShift ->
                                Text(
                                    modifier = Modifier.width(80.dp),
                                    text = "末班車已過"
                                )
                            StopStatus.NonStop ->
                                Text(
                                    modifier = Modifier.width(80.dp),
                                    text = "不停靠"
                                )
                            StopStatus.None ->
                                Spacer(modifier = Modifier.width(80.dp))
                            StopStatus.NoneDeparture ->
                                Text(
                                    modifier = Modifier.width(80.dp),
                                    text = "尚未發車"
                                )
                            StopStatus.Normal ->
                                if (it.estimateTime != null) {
                                    Text(
                                        modifier = Modifier.width(80.dp),
                                        text = "${it.estimateTime / 60}分"
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(80.dp))
                                }
                        }

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
                                    color = Color.Red,
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