package com.examprepare.easybus.feature.routedetail

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.examprepare.easybus.Const
import com.examprepare.easybus.core.ui.FailureView
import com.examprepare.easybus.feature.model.Route
import com.examprepare.easybus.feature.model.StopStatus
import com.examprepare.easybus.feature.routedetail.model.RealTimeRouteInfo
import com.examprepare.easybus.ui.theme.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RouteScreen(
    viewModel: RouteDetailViewModel,
    routeId: String,
    toStation: (String) -> Unit,
    onBack: () -> Unit
) {
    val route = viewModel.route.collectAsState().value
    val realTimeRouteInfoList = viewModel.realTimeRouteInfoList.collectAsState().value
    val isLike = viewModel.isLike.collectAsState().value
    val failure = viewModel.failure.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.getRoute(routeId)
    }

    if (route != Route.empty) {
        val scope = rememberCoroutineScope()
        DisposableEffect(route) {
            val job: Job = scope.launch {
                while (true) {
                    viewModel.getRealTimeRouteInfo(route.routeId)
                    delay(Const.UPDATE_REALTIME_ROUTE_ARRIVAL_TIME_INTERVAL_MILLISECONDS)
                }
            }
            onDispose {
                job.cancel()
            }
        }
    }

    Route(
        route = route,
        realTimeRouteInfoList = realTimeRouteInfoList,
        isLike = isLike,
        toStation = toStation,
        onBack = onBack,
        onLike = viewModel::addLikeRoute,
        onRemoveLike = viewModel::removeLikeRoute
    )
    FailureView(failure = failure)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Route(
    route: Route,
    realTimeRouteInfoList: List<RealTimeRouteInfo>,
    isLike: Boolean,
    toStation: (String) -> Unit,
    onBack: () -> Unit,
    onLike: (String) -> Unit,
    onRemoveLike: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = realTimeRouteInfoList.size)
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("公車動態-${route.routeName}")
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Sharp.ArrowBack, contentDescription = "退回上一頁")
                        }
                    },
                    actions = {
                        if (isLike) {
                            IconButton(
                                onClick = { onRemoveLike(route.routeId) }
                            ) {
                                Icon(Icons.Filled.Favorite, contentDescription = "移除我的最愛路線")
                            }
                        } else {
                            IconButton(
                                onClick = { onLike(route.routeId) }
                            ) {
                                Icon(Icons.Outlined.FavoriteBorder, contentDescription = "新增我的最愛路線")
                            }
                        }
                    }
                )
                if (pagerState.pageCount != 0) {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage
                    ) {
                        realTimeRouteInfoList.forEachIndexed { index, title ->
                            Tab(
                                text = { Text(title.destination) },
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }) {
            RouteDetail(
                pagerState = pagerState,
                realTimeRouteInfoList = realTimeRouteInfoList,
                toStation = toStation
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RouteDetail(
    pagerState: PagerState,
    realTimeRouteInfoList: List<RealTimeRouteInfo>,
    toStation: (String) -> Unit
) {
    if (realTimeRouteInfoList.isNotEmpty()) {
        HorizontalPager(state = pagerState) { page ->
            LazyColumn {
                items(realTimeRouteInfoList[page].stopItems) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                toStation(it.stationId)
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
                                Text(text = it.stopName, style = MaterialTheme.typography.h5)
                            }
                            it.plateNumb?.let { plateNumber ->
                                Text(
                                    text = plateNumber,
                                    style = MaterialTheme.typography.subtitle1
                                )
                                if (it.isLastBus) {
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
    } else {
        Text("尚無路線資料")
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RouteScreenPreview() {
    EasyBusTheme {
        Route(
            route = Route("01", "紅12", "XXX", "XXX"),
            realTimeRouteInfoList = emptyList(),
            isLike = true,
            toStation = { },
            onBack = { },
            onLike = { },
            onRemoveLike = { }
        )
    }
}