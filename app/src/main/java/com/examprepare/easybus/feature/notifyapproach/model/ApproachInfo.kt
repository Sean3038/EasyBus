package com.examprepare.easybus.feature.notifyapproach.model

data class ApproachInfo(
    val routeId: String,
    val routeName: String,
    val stopName: String,
    val targetMinute: Int
) {
    companion object {
        val empty = ApproachInfo("", "", "", Int.MAX_VALUE)
    }
}
