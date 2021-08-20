package com.examprepare.easybus.feature.route

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.examprepare.easybus.core.ui.FailureView
import com.examprepare.easybus.feature.searchroute.domain.model.Route
import com.examprepare.easybus.ui.theme.EasyBusTheme

@Composable
fun RouteScreen(viewModel: RouteViewModel, routeName: String, onBack: () -> Unit) {
    val route = viewModel.route.collectAsState().value
    val failure = viewModel.failure.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.getRoute(routeName)
    }
    Route(
        route = route,
        onBack = onBack
    )
    FailureView(failure = failure)
}

@Composable
fun Route(route: Route, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = {
                Row {
                    Text(text = "EasyBus")
                }
            },
            navigationIcon = {
                Icon(
                    Icons.Sharp.ArrowBack,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(8.dp),
                    contentDescription = "退回上一頁"
                )
            }
        )
        Text(text = route.routeName)
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RouteScreenPreview() {
    EasyBusTheme {
        Route(
            Route("01", "紅11")
        ) { }
    }
}