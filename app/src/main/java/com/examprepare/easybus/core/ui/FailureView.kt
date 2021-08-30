package com.examprepare.easybus.core.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.examprepare.easybus.core.exception.Failure

@Composable
fun FailureView(failure: Failure) {
    when (failure) {
        is Failure.NetworkConnection -> {
            FailureDialog(message = "請確認網路是否穩定")
        }
        is Failure.ServerError -> {
            FailureDialog(message = "目前無法連接至伺服器")
        }
        else -> {
        }
    }
}


@Composable
fun FailureDialog(message: String) {
    val isOpen = remember {
        mutableStateOf(true)
    }
    if (isOpen.value) {
        AlertDialog(
            onDismissRequest = { isOpen.value = false },
            title = {
                Text("錯誤")
            },
            text = {
                Text(message)
            },
            confirmButton = {
                Button(onClick = { isOpen.value = false }) {
                    Text("確認")
                }
            })
    }
}