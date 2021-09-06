package com.examprepare.easybus.core.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.examprepare.easybus.core.exception.Failure

@Composable
fun FailureView(failure: Failure, onDismissCallback: () -> Unit = {}) {
    when (failure) {
        is Failure.NetworkConnection -> {
            FailureDialog(message = "請確認網路是否穩定", onDismissCallback = onDismissCallback)
        }
        is Failure.ServerError -> {
            FailureDialog(message = "目前無法連接至伺服器", onDismissCallback = onDismissCallback)
        }
        is Failure.None -> {
        }
        else -> {
        }
    }
}


@Composable
fun FailureDialog(message: String, onDismissCallback: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismissCallback() },
        title = {
            Text("錯誤")
        },
        text = {
            Text(message)
        },
        confirmButton = {
            Button(onClick = { onDismissCallback() }) {
                Text("確認")
            }
        })
}