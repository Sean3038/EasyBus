package com.examprepare.easybus.core.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.Composable

@Composable
fun TitleBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Sharp.ArrowBack, contentDescription = "退回上一頁")
            }
        }
    )
}