package com.examprepare.easybus.core.service

class PTXService(private val api: PTXApi) {

    suspend fun getRoutes() = api.getRoutes()

    suspend fun getRoutes(routeName: String) = api.getRoutes(routeName)
}