package com.examprepare.easybus.feature.routedetail

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.examprepare.easybus.Const
import com.examprepare.easybus.core.ui.FailureView
import com.examprepare.easybus.core.ui.StopStatusBadge
import com.examprepare.easybus.feature.model.Direction
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
    observeApproachNotify: (routeId: String, stopId: String, direction: Direction, minute: Int) -> Unit,
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
        observeApproachNotify = observeApproachNotify,
        toStation = toStation,
        onBack = onBack,
        onLike = viewModel::addLikeRoute,
        onRemoveLike = viewModel::removeLikeRoute
    )
    FailureView(failure = failure,onDismissCallback = viewModel::onDismissFailure)
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun Route(
    route: Route,
    realTimeRouteInfoList: List<RealTimeRouteInfo>,
    isLike: Boolean,
    observeApproachNotify: (routeId: String, stopId: String, direction: Direction, minute: Int) -> Unit,
    toStation: (String) -> Unit,
    onBack: () -> Unit,
    onLike: (String) -> Unit,
    onRemoveLike: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = realTimeRouteInfoList.size)
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var bottomSheet by remember { mutableStateOf<BottomSheet>(BottomSheet.None) }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            when (bottomSheet) {
                is BottomSheet.Menu -> {
                    RouteMenu(
                        onClickApproachNotify = {
                            scope.launch {
                                bottomSheet = BottomSheet.ApproachNotify(
                                    bottomSheet.item,
                                    route.routeId
                                )
                            }
                        },
                        onClickToStation = {
                            scope.launch {
                                bottomSheetState.hide()
                                toStation(bottomSheet.item.stationId)
                            }
                        }
                    )
                }
                is BottomSheet.ApproachNotify -> {
                    val settings = Const.APPROACH_NOTIFY_MINUTES_SETTINGS
                    RouteNotifyApproachSetting(settings.map { "$it ??????" }) {
                        scope.launch {
                            observeApproachNotify(
                                route.routeId,
                                bottomSheet.item.stopId,
                                bottomSheet.item.direction,
                                settings[it]
                            )
                            bottomSheetState.hide()
                            bottomSheet = BottomSheet.None
                        }
                    }
                }
                else -> {
                    Text("No Item")
                }
            }
        }
    ) {
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
                        onClickStop = {
                            scope.launch {
                                bottomSheet = BottomSheet.Menu(it)
                                bottomSheetState.show()
                            }
                        }
                    )
                } else {
                    Text("??????????????????")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RouteMenu(onClickToStation: () -> Unit, onClickApproachNotify: () -> Unit) {
    LazyColumn {
        item {
            ListItem(
                modifier = Modifier.clickable {
                    onClickApproachNotify()
                },
                text = { Text("????????????") },
                icon = {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "????????????"
                    )
                }
            )
        }
        item {
            ListItem(
                modifier = Modifier.clickable {
                    onClickToStation()
                },
                text = { Text("??????????????????????????????") },
                icon = {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "??????????????????????????????"
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RouteNotifyApproachSetting(settings: List<String>, onClickIndex: (Int) -> Unit) {
    LazyColumn {
        itemsIndexed(settings) { index, item ->
            ListItem(
                modifier = Modifier.clickable {
                    onClickIndex(index)
                },
                text = { Text(item) }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
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
                Text("????????????-${route.routeName}")
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Sharp.ArrowBack, contentDescription = "???????????????")
                }
            },
            actions = {
                if (isLike) {
                    IconButton(
                        onClick = { onRemoveLike(route.routeId) }
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = "????????????????????????")
                    }
                } else {
                    IconButton(
                        onClick = { onLike(route.routeId) }
                    ) {
                        Icon(Icons.Outlined.FavoriteBorder, contentDescription = "????????????????????????")
                    }
                }
            }
        )
        if (realTimeRouteInfoList.isNotEmpty()) {
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
    onClickStop: (RealTimeRouteInfo.StopItem) -> Unit
) {
    HorizontalPager(state = pagerState) { page ->
        LazyColumn {
            items(realTimeRouteInfoList[page].stopItems) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            onClickStop(it)
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
                                    text = "(?????????)",
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
            route = Route("01", "???12", "XXX", "XXX"),
            realTimeRouteInfoList = emptyList(),
            isLike = true,
            observeApproachNotify = { _, _, _, _ -> },
            toStation = { },
            onBack = { },
            onLike = { },
            onRemoveLike = { }
        )
    }
}

private sealed class BottomSheet(val item: RealTimeRouteInfo.StopItem) {
    object None : BottomSheet(RealTimeRouteInfo.StopItem.empty)
    class Menu(item: RealTimeRouteInfo.StopItem) : BottomSheet(item)
    class ApproachNotify(item: RealTimeRouteInfo.StopItem, val routeId: String) : BottomSheet(item)
}