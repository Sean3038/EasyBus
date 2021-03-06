package com.examprepare.easybus.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.examprepare.easybus.Const
import com.examprepare.easybus.core.navigation.Destinations.Home
import com.examprepare.easybus.core.navigation.Destinations.Route
import com.examprepare.easybus.core.navigation.Destinations.SearchNearStop
import com.examprepare.easybus.core.navigation.Destinations.Station
import com.examprepare.easybus.feature.home.HomeScreen
import com.examprepare.easybus.feature.home.HomeViewModel
import com.examprepare.easybus.feature.model.Direction
import com.examprepare.easybus.feature.routedetail.RouteDetailViewModel
import com.examprepare.easybus.feature.routedetail.RouteScreen
import com.examprepare.easybus.feature.searchnearstop.SearchNearStopScreen
import com.examprepare.easybus.feature.searchnearstop.SearchNearStopViewModel
import com.examprepare.easybus.feature.searchroute.SearchRouteScreen
import com.examprepare.easybus.feature.searchroute.SearchRouteViewModel
import com.examprepare.easybus.feature.stationdetail.StationDetailScreen
import com.examprepare.easybus.feature.stationdetail.StationDetailViewModel
import com.examprepare.easybus.ui.theme.EasyBusTheme

@Composable
fun EasyBusApp(
    observeApproachNotify: (routeId: String, stopId: String, direction: Direction, minute: Int) -> Unit,
    toSystemSetting: () -> Unit,
    toSystemLocationSetting: () -> Unit
) {
    val navController = rememberNavController()
    val actions = remember(navController) { Actions(navController) }
    val uri = Const.APP_URL
    EasyBusTheme {
        NavHost(navController = navController, startDestination = Home) {
            composable(Home) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                    toSearchRoute = actions.toSearchRoute,
                    toSearchNearStop = actions.toSearchNearStop,
                    openRoute = actions.toRoute
                )
            }
            composable(Route) {
                val viewModel = hiltViewModel<SearchRouteViewModel>()
                SearchRouteScreen(
                    viewModel = viewModel,
                    toRoute = actions.toRoute,
                    onBack = actions.navigateBack
                )
            }
            composable(
                "$Route/{${Destinations.RouteArgs.RouteID}}",
                arguments = listOf(navArgument(Destinations.RouteArgs.RouteID) {
                    type = NavType.StringType
                }),
                deepLinks = listOf(navDeepLink {
                    uriPattern = "$uri/$Route/{${Destinations.RouteArgs.RouteID}}"
                }),
            ) {
                val viewModel = hiltViewModel<RouteDetailViewModel>()
                RouteScreen(
                    viewModel = viewModel,
                    routeId = it.arguments?.getString(Destinations.RouteArgs.RouteID) ?: "",
                    observeApproachNotify = observeApproachNotify,
                    toStation = actions.toStation,
                    onBack = actions.navigateBack
                )
            }
            composable(SearchNearStop) {
                val viewModel = hiltViewModel<SearchNearStopViewModel>()
                SearchNearStopScreen(
                    viewModel = viewModel,
                    toSystemSettings = toSystemSetting,
                    toSystemLocationSetting = toSystemLocationSetting,
                    toStation = actions.toStation,
                    onBack = actions.navigateBack
                )
            }
            composable(
                "$Station/{${Destinations.StationArgs.StationID}}",
                arguments = listOf(navArgument(Destinations.StationArgs.StationID) {
                    type = NavType.StringType
                })
            ) {
                val viewModel = hiltViewModel<StationDetailViewModel>()
                StationDetailScreen(
                    viewModel = viewModel,
                    stationId = it.arguments?.getString(Destinations.StationArgs.StationID) ?: "",
                    toRoute = actions.toRoute,
                    navigateBack = actions.navigateBack
                )
            }
        }
    }
}

object Destinations {
    const val Home = "home"
    const val Route = "route"
    const val SearchNearStop = "search_near_stop"
    const val Station = "station"

    object RouteArgs {
        const val RouteID = "routeID"
    }

    object StationArgs {
        const val StationID = "stationId"
    }
}

class Actions(navController: NavHostController) {
    val toRoute: (String) -> Unit = { routeName ->
        navController.navigate("$Route/$routeName")
    }

    val toSearchRoute: () -> Unit = {
        navController.navigate(Route)
    }

    val toSearchNearStop: () -> Unit = {
        navController.navigate(SearchNearStop)
    }

    val toStation: (String) -> Unit = { stationId ->
        navController.navigate("$Station/$stationId")
    }

    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
}
