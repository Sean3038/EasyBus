package com.examprepare.easybus.feature.route

import androidx.lifecycle.viewModelScope
import com.examprepare.easybus.core.platform.BaseViewModel
import com.examprepare.easybus.feature.route.domain.usecase.GetRoute
import com.examprepare.easybus.feature.searchroute.domain.model.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val getRoute: GetRoute
) : BaseViewModel() {

    private val _route: MutableStateFlow<Route> = MutableStateFlow(Route.empty)
    val route = _route.asStateFlow()

    fun getRoute(routeId: String) {
        viewModelScope.launch {
            getRoute.run(GetRoute.Params(routeId = routeId)).fold(::handleFailure, ::handleGetRoute)
        }
    }

    private fun handleGetRoute(route: Route) {
        _route.value = route
    }
}