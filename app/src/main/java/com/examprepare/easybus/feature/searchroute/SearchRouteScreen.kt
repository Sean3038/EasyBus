package com.examprepare.easybus.feature.searchroute

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examprepare.easybus.core.ui.TitleBar
import com.examprepare.easybus.feature.searchroute.domain.model.Route
import com.examprepare.easybus.ui.theme.EasyBusTheme

@Composable
fun SearchRouteScreen(viewModel: SearchRouteViewModel, toRoute: (String) -> Unit) {
    val searchRouteName = viewModel.searchRouteName.collectAsState().value
    val routes = viewModel.routes.collectAsState().value
    SearchRoute(
        searchText = searchRouteName,
        routes = routes,
        onSearchChange = viewModel::onSearchChange,
        toRoute = toRoute
    )
}

@Composable
fun SearchRoute(
    searchText: String,
    routes: List<Route>,
    onSearchChange: (String) -> Unit,
    toRoute: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TitleBar()
        SearchBar(searchText, onSearchChange)
        RoutesView(routes, toRoute)
    }
}

@Composable
fun SearchBar(searchText: String, onSearchChange: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    Row {
        TextField(
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "查詢路線") },
            modifier = Modifier.fillMaxWidth(),
            value = searchText,
            onValueChange = onSearchChange,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Default,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
    }
}

@Composable
fun RoutesView(favoriteRoutes: List<Route>, toRoute: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
    ) {
        itemsIndexed(favoriteRoutes) { _, item ->
            Box(modifier = Modifier.clickable { toRoute(item.routeId) }) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    text = item.routeName,
                    style = TextStyle(fontSize = 24.sp),
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchRouteScreenPreview() {
    EasyBusTheme {
        SearchRoute("", emptyList(), {}) {}
    }
}

@Preview
@Composable
private fun SearchBarPreview() {
    SearchBar("") {}
}