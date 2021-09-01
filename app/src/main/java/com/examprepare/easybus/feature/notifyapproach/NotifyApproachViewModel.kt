package com.examprepare.easybus.feature.notifyapproach

import androidx.lifecycle.viewModelScope
import com.examprepare.easybus.core.platform.BaseViewModel
import com.examprepare.easybus.feature.model.Direction
import com.examprepare.easybus.feature.notifyapproach.model.ApproachInfo
import com.examprepare.easybus.feature.notifyapproach.usecase.ObserveApproachInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotifyApproachViewModel @Inject constructor(
    private val observeApproachInfo: ObserveApproachInfo
) : BaseViewModel() {

    private val _approachInfo = MutableStateFlow(ApproachInfo.empty)
    val approachInfo = _approachInfo.asStateFlow()

    fun observeApproachNotify(routeId: String, stopId: String, direction: Direction, minute: Int) {
        viewModelScope.launch {
            observeApproachInfo(ObserveApproachInfo.Params(routeId, stopId, direction, minute)) {
                it.fold(::handleFailure, ::handleApproachInfo)
            }
        }
    }

    private fun handleApproachInfo(approachInfo: ApproachInfo) {
        _approachInfo.value = approachInfo
    }
}