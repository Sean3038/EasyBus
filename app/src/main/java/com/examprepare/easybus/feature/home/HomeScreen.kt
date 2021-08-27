package com.examprepare.easybus.feature.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.examprepare.easybus.core.ui.FailureView
import com.examprepare.easybus.feature.home.domain.model.FavoriteRoute
import com.examprepare.easybus.ui.theme.EasyBusTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    toSearchRoute: () -> Unit,
    toSearchNearStop: () -> Unit,
    openRoute: (String) -> Unit
) {
    val favoriteRoutes = viewModel.favoriteRoutes.collectAsState().value
    val failure = viewModel.failure.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.getFavoriteRoutes()
    }

    Home(
        favoriteRoutes = favoriteRoutes,
        toSearchRoute = toSearchRoute,
        toSearchNearStop = toSearchNearStop,
        openRoute = openRoute
    )
    FailureView(failure = failure)
}

@Composable
fun Home(
    favoriteRoutes: List<FavoriteRoute>,
    toSearchRoute: () -> Unit,
    toSearchNearStop: () -> Unit,
    openRoute: (String) -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row {
                    Text(text = "EasyBus")
                }
            }
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp),
                onClick = toSearchRoute
            ) {
                Text(text = "搜尋路線", style = MaterialTheme.typography.body1)
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp),
                onClick = toSearchNearStop
            ) {
                Text(text = "搜尋付近站牌", style = MaterialTheme.typography.body1)
            }

            LikeRoutesView(favoriteRoutes, openRoute)
        }
    }
}


@Composable
fun LikeRoutesView(favoriteRoutes: List<FavoriteRoute>, openRoute: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .wrapContentHeight()
            .padding(8.dp, 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(0.dp, 16.dp)) {
            Text(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
                text = "我的最愛"
            )
            LazyColumn(
                modifier = Modifier.wrapContentHeight(),
            ) {
                itemsIndexed(favoriteRoutes) { _, item ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { openRoute(item.routeId) }
                            .padding(16.dp, 8.dp),
                        text = item.routeName,
                        style = MaterialTheme.typography.body1,
                    )
                }
                if (favoriteRoutes.isEmpty()) {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = "尚無偏好路線",
                            textAlign = TextAlign.Center,
                            color = LocalContentColor.current.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.body1,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPreview() {
    EasyBusTheme {
        Home(
            listOf(),
            { },
            { },
            { }
        )
    }
}