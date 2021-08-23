package com.examprepare.easybus.feature.searchnearstop

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.examprepare.easybus.core.ui.TitleBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchNearStopScreen(toSystemSettings: () -> Unit, navigateBack: () -> Unit) {

    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
    )
    var location by remember { mutableStateOf<Location?>(null) }


    PermissionsRequired(locationPermissionState,
        permissionsNotGrantedContent = {
            PermissionNotGrantedDialog(
                permissionState = locationPermissionState,
                navigateBack = navigateBack
            )
        },
        permissionsNotAvailableContent = {
            PermissionNotAvailableDialog(
                toSystemSettings = toSystemSettings,
                navigateBack = navigateBack
            )
        }
    ) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            location =
                when {
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    }
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    }
                    else -> {
                        null
                    }
                }
        }
    }

    Column {
        TitleBar()
    }
}

@Composable
fun PermissionNotAvailableDialog(toSystemSettings: () -> Unit, navigateBack: () -> Unit) {
    AlertDialog(
        title = {
            Text("提醒")
        },
        text = {
            Text("請至系統設定，開啟位置權限以利後續使用")
        },
        confirmButton = {
            Button(onClick = { toSystemSettings() }) {
                Text("設定")
            }
        },
        dismissButton = {
            Button(onClick = { navigateBack() }) {
                Text("取消")
            }
        },
        onDismissRequest = { navigateBack() }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionNotGrantedDialog(
    permissionState: MultiplePermissionsState,
    navigateBack: () -> Unit
) {
    AlertDialog(
        title = {
            Text("提醒")
        },
        text = {
            Text("請同意提供位置權限，以利後續使用")
        },
        confirmButton = {
            Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                Text("同意")
            }
        },
        dismissButton = {
            Button(onClick = { navigateBack() }) {
                Text("取消")
            }
        },
        onDismissRequest = { navigateBack() }
    )
}