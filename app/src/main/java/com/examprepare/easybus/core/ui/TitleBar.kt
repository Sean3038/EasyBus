package com.examprepare.easybus.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun TitleBar() {
    TopAppBar(
        title = {
            Row {
                Text(text = "EasyBus")
            }
        }
    )
}