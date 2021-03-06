package com.examprepare.easybus.feature.searchroute

import androidx.lifecycle.viewModelScope
import com.examprepare.easybus.core.platform.BaseViewModel
import com.examprepare.easybus.feature.model.SearchRouteResult
import com.examprepare.easybus.feature.searchroute.usecase.SearchRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchRouteViewModel @Inject constructor(
    private val searchRoute: SearchRoute
) : BaseViewModel() {

    private val _searchRouteName: MutableStateFlow<String> = MutableStateFlow("");
    val searchRouteName = _searchRouteName.asStateFlow()
    private val _items: MutableStateFlow<List<SearchRouteResult.Item>> =
        MutableStateFlow(emptyList())
    val items = _items.asStateFlow()

    init {
        viewModelScope.launch {
            searchRoute(SearchRoute.Params("")) {
                it.fold(::handleFailure, ::handleGetSearchResult)
            }
        }
    }

    fun onSearchChange(searchRouteName: String) {
        viewModelScope.launch {
            _searchRouteName.value = searchRouteName
            searchRoute(SearchRoute.Params(searchRouteName)) {
                it.fold(::handleFailure, ::handleGetSearchResult)
            }
        }
    }

    private fun handleGetSearchResult(result: SearchRouteResult) {
        _items.value = result.results.sortedBy { it.routeName }
    }
}