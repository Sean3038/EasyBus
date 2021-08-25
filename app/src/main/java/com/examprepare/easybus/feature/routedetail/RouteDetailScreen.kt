package com.examprepare.easybus.feature.routedetail

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.examprepare.easybus.core.ui.FailureView
import com.examprepare.easybus.feature.model.Route
import com.examprepare.easybus.ui.theme.EasyBusTheme

@Composable
fun RouteScreen(viewModel: RouteDetailViewModel, routeName: String, onBack: () -> Unit) {
    val route = viewModel.route.collectAsState().value
    val isLike = viewModel.isLike.collectAsState().value
    val failure = viewModel.failure.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.getRoute(routeName)
    }
    Route(
        route = route,
        isLike = isLike,
        onBack = onBack,
        onLike = viewModel::addLikeRoute,
        onRemoveLike = viewModel::removeLikeRoute
    )
    FailureView(failure = failure)
}

@Composable
fun Route(
    route: Route,
    isLike: Boolean,
    onBack: () -> Unit,
    onLike: (String) -> Unit,
    onRemoveLike: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(topBar = {
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
        }) {
            Text(text = route.routeName)
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RouteScreenPreview() {
    EasyBusTheme {
        Route(
            route = Route("01", "紅12"),
            isLike = true,
            onBack = { },
            onLike = { },
            onRemoveLike = { }
        )
    }
}