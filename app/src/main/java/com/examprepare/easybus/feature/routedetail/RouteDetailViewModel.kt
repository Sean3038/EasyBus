package com.examprepare.easybus.feature.routedetail

import androidx.lifecycle.viewModelScope
import com.examprepare.easybus.core.platform.BaseViewModel
import com.examprepare.easybus.feature.model.Route
import com.examprepare.easybus.feature.routedetail.model.RealTimeRouteInfo
import com.examprepare.easybus.feature.routedetail.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteDetailViewModel @Inject constructor(
    private val getRoute: GetRoute,
    private val addLikeRoute: AddLikeRoute,
    private val removeLikeRoute: RemoveLikeRoute,
    private val isLikeRoute: IsLikeRoute,
    private val getRealTimeRouteInfo: GetRealTimeRouteInfo
) : BaseViewModel() {

    private val _route: MutableStateFlow<Route> = MutableStateFlow(Route.empty)
    val route = _route.asStateFlow()

    private val _realTimeRouteInfoList: MutableStateFlow<List<RealTimeRouteInfo>> =
        MutableStateFlow(emptyList())
    val realTimeRouteInfoList = _realTimeRouteInfoList.asStateFlow()

    private val _isLike: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLike = _isLike.asStateFlow()


    fun getRoute(routeId: String) {
        viewModelScope.launch {
            getRoute(GetRoute.Params(routeId = routeId)) {
                it.fold(::handleFailure, ::handleGetRoute)
            }

            isLikeRoute(IsLikeRoute.Params(routeId = routeId)) {
                it.fold(::handleFailure, ::handleIsLikeRoute)
            }
        }
    }

    fun getRealTimeRouteInfo(routeId: String) {
        getRealTimeRouteInfo(GetRealTimeRouteInfo.Params(routeId = routeId)) {
            it.fold(::handleFailure, ::handleGetRealTimeRouteInfo)
        }
    }

    fun addLikeRoute(routeId: String) {
        viewModelScope.launch {
            addLikeRoute(AddLikeRoute.Params(routeId = routeId)) {
                it.fold(::handleFailure) {
                    handleAddLikeRoute()
                }
            }
        }
    }

    fun removeLikeRoute(routeId: String) {
        viewModelScope.launch {
            removeLikeRoute(RemoveLikeRoute.Params(routeId = routeId)) {
                it.fold(::handleFailure) {
                    handleRemoveLikeRoute()
                }
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

    private fun handleGetRealTimeRouteInfo(realTimeRouteInfoList: List<RealTimeRouteInfo>) {
        _realTimeRouteInfoList.value = realTimeRouteInfoList
    }
}