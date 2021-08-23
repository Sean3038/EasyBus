package com.examprepare.easybus.feature.searchnearstop

import com.examprepare.easybus.core.platform.BaseViewModel
import com.examprepare.easybus.feature.searchnearstop.domain.usecase.GetNearStop
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchNearStopViewModel @Inject constructor(
    private val getNearStop: GetNearStop
) : BaseViewModel() {


}