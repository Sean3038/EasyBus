package com.examprepare.easybus.feature.model

sealed class StopStatus {
    object None : StopStatus()//無
    object Normal : StopStatus()//正常
    object NoneDeparture : StopStatus()//尚未發車
    object NoShift : StopStatus()//末班車已過
    object NonStop : StopStatus()//交管不停靠
    object NoOperation : StopStatus()//今日未營運
    object OnPulledIN : StopStatus()//即將進站
}