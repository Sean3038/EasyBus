package com.examprepare.easybus.feature.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun RouteScreen(routeName: String) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        Text(routeName)
    }

}