package com.examprepare.easybus.feature.home

import androidx.lifecycle.viewModelScope
import com.examprepare.easybus.core.interactor.UseCase
import com.examprepare.easybus.core.platform.BaseViewModel
import com.examprepare.easybus.feature.home.domain.model.FavoriteRoute
import com.examprepare.easybus.feature.home.domain.usecase.GetFavoriteRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFavoriteRoutes: GetFavoriteRoutes
) : BaseViewModel() {

    private val _favoriteRoutes = MutableStateFlow<List<FavoriteRoute>>(emptyList())
    val favoriteRoutes = _favoriteRoutes.asStateFlow()

    fun getFavoriteRoutes() {
        viewModelScope.launch {
            getFavoriteRoutes.run(UseCase.None()).fold(::handleFailure, ::handleGetFavoriteRoutes)
        }
    }

    private fun handleGetFavoriteRoutes(favoriteRoutes: List<FavoriteRoute>) {
        _favoriteRoutes.value = favoriteRoutes
    }
}
