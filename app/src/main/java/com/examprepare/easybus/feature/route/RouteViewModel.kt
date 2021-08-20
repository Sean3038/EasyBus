package com.examprepare.easybus.feature.route

import androidx.lifecycle.viewModelScope
import com.examprepare.easybus.core.platform.BaseViewModel
import com.examprepare.easybus.feature.route.domain.usecase.AddLikeRoute
import com.examprepare.easybus.feature.route.domain.usecase.GetRoute
import com.examprepare.easybus.feature.route.domain.usecase.IsLikeRoute
import com.examprepare.easybus.feature.route.domain.usecase.RemoveLikeRoute
import com.examprepare.easybus.feature.searchroute.domain.model.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val getRoute: GetRoute,
    private val addLikeRoute: AddLikeRoute,
    private val removeLikeRoute: RemoveLikeRoute,
    private val isLikeRoute: IsLikeRoute
) : BaseViewModel() {

    private val _route: MutableStateFlow<Route> = MutableStateFlow(Route.empty)
    val route = _route.asStateFlow()
    private val _isLike: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLike = _isLike.asStateFlow()


    fun getRoute(routeId: String) {
        viewModelScope.launch {
            getRoute.run(GetRoute.Params(routeId = routeId))
                .fold(::handleFailure, ::handleGetRoute)
            isLikeRoute.run(IsLikeRoute.Params(routeId = routeId))
                .fold(::handleFailure, ::handleIsLikeRoute)
        }
    }

    fun addLikeRoute(routeId: String) {
        viewModelScope.launch {
            addLikeRoute.run(AddLikeRoute.Params(routeId = routeId))
                .fold(::handleFailure) {
                    handleAddLikeRoute()
                }
        }
    }

    fun removeLikeRoute(routeId: String) {
        viewModelScope.launch {
            removeLikeRoute.run(RemoveLikeRoute.Params(routeId = routeId))
                .fold(::handleFailure) {
                    handleRemoveLikeRoute()
                }
        }
    }

    private fun handleGetRoute(route: Route) {
        _route.value = route
    }

    private fun handleAddLikeRoute() {
        _isLike.value = true
    }

    private fun handleRemoveLikeRoute() {
        _isLike.value = false
    }

    private fun handleIsLikeRoute(isLike: Boolean) {
        _isLike.value = isLike
    }
}