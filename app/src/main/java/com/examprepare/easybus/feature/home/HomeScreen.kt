package com.examprepare.easybus.feature.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examprepare.easybus.core.ui.TitleBar
import com.examprepare.easybus.feature.home.domain.model.FavoriteRoute
import com.examprepare.easybus.ui.theme.EasyBusTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    toSearchRoute: () -> Unit,
    toSearchNearStop: () -> Unit,
    openRoute: (String) -> Unit
) {
    val favoriteRoutes = viewModel.favoriteRoutes.collectAsState()

    Home(
        favoriteRoutes = favoriteRoutes.value,
        toSearchRoute = toSearchRoute,
        toSearchNearStop = toSearchNearStop,
        openRoute = openRoute
    )
}

@Composable
fun Home(
    favoriteRoutes: List<FavoriteRoute>,
    toSearchRoute: () -> Unit,
    toSearchNearStop: () -> Unit,
    openRoute: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TitleBar()

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            onClick = toSearchRoute
        ) {
            Text(text = "搜尋路線")
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            onClick = toSearchNearStop
        ) {
            Text(text = "搜尋付近站牌")
        }

        LikeRoutesView(favoriteRoutes, openRoute)
    }
}


@Composable
fun LikeRoutesView(favoriteRoutes: List<FavoriteRoute>, openRoute: (String) -> Unit) {
    Column(modifier = Modifier.padding(8.dp, 0.dp)) {
        Text(
            modifier = Modifier
                .padding(0.dp, 16.dp)
                .fillMaxWidth(),
            style = TextStyle(fontSize = 24.sp),
            textAlign = TextAlign.Center,
            text = "我的最愛"
        )
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxSize()
        ) {
            itemsIndexed(favoriteRoutes) { _, item ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { openRoute(item.routeName) },
                    text = item.routeName,
                    style = TextStyle(fontSize = 24.sp),
                    color = MaterialTheme.colors.onBackground
                )
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
            listOf(
                FavoriteRoute("0", "紅12", true),
                FavoriteRoute("0", "紅12", true),
                FavoriteRoute("0", "紅12", true),
                FavoriteRoute("0", "紅12", true)
            ),
            { },
            { },
            { }
        )
    }
}