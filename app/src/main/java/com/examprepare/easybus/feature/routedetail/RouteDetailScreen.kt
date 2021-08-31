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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.examprepare.easybus.Const
import com.examprepare.easybus.core.ui.FailureView
import com.examprepare.easybus.core.ui.StopStatusBadge
import com.examprepare.easybus.feature.model.Route
import com.examprepare.easybus.feature.routedetail.model.RealTimeRouteInfo
import com.examprepare.easybus.ui.theme.EasyBusTheme
import com.examprepare.easybus.ui.theme.Red400
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(topBar = {
            RouteDetailTitleBar(
                route = route,
                realTimeRouteInfoList = realTimeRouteInfoList,
                isLike = isLike,
                pagerState = pagerState,
                onBack = onBack,
                onLike = onLike,
                onRemoveLike = onRemoveLike
            )
        }) {
            if (realTimeRouteInfoList.isNotEmpty()) {
                RouteDetail(
                    pagerState = pagerState,
                    realTimeRouteInfoList = realTimeRouteInfoList,
                    toStation = toStation
                )
            } else {
                Text("尚無路線資料")
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun RouteDetailTitleBar(
    route: Route,
    realTimeRouteInfoList: List<RealTimeRouteInfo>,
    isLike: Boolean,
    pagerState: PagerState,
    onBack: () -> Unit,
    onLike: (String) -> Unit,
    onRemoveLike: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
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
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun RouteDetail(
    pagerState: PagerState,
    realTimeRouteInfoList: List<RealTimeRouteInfo>,
    toStation: (String) -> Unit
) {
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
                        StopStatusBadge(it.estimateTime, it.stopStatus)

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