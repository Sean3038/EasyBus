package com.examprepare.easybus.core.navigation

import com.examprepare.easybus.feature.searchnearstop.SearchNearStopScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.examprepare.easybus.core.navigation.Destinations.Home
import com.examprepare.easybus.core.navigation.Destinations.Route
import com.examprepare.easybus.core.navigation.Destinations.SearchNearStop
import com.examprepare.easybus.feature.home.HomeScreen
import com.examprepare.easybus.feature.home.HomeViewModel
import com.examprepare.easybus.feature.routedetail.RouteScreen
import com.examprepare.easybus.feature.routedetail.RouteDetailViewModel
import com.examprepare.easybus.feature.searchnearstop.SearchNearStopViewModel
import com.examprepare.easybus.feature.searchroute.SearchRouteScreen
import com.examprepare.easybus.feature.searchroute.SearchRouteViewModel
import com.examprepare.easybus.ui.theme.EasyBusTheme

@Composable
fun EasyBusApp(toSystemSetting: () -> Unit, toSystemLocationSetting: () -> Unit) {
    val navController = rememberNavController()
    val actions = remember(navController) { Actions(navController) }
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
                SearchRouteScreen(viewModel, actions.toRoute)
            }
            composable(
                "$Route/{routeName}",
                arguments = listOf(navArgument(Destinations.RouteArgs.RouteName) {
                    type = NavType.StringType
                })
            ) {
                val viewModel = hiltViewModel<RouteDetailViewModel>()
                RouteScreen(
                    viewModel = viewModel,
                    routeName = it.arguments?.getString(Destinations.RouteArgs.RouteName) ?: "",
                    onBack = actions.navigateBack
                )
            }
            composable(SearchNearStop) {
                val viewModel = hiltViewModel<SearchNearStopViewModel>()
                SearchNearStopScreen(
                    viewModel = viewModel,
                    toSystemSettings = toSystemSetting,
                    toSystemLocationSetting = toSystemLocationSetting,
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

    object RouteArgs {
        const val RouteName = "routeName"
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

    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
}
