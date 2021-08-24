import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.examprepare.easybus.core.ui.TitleBar
import com.examprepare.easybus.feature.searchnearstop.SearchNearStopViewModel
import com.examprepare.easybus.feature.searchnearstop.domain.model.Stop
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchNearStopScreen(
    viewModel: SearchNearStopViewModel,
    toSystemSettings: () -> Unit,
    toSystemLocationSetting: () -> Unit,
    navigateBack: () -> Unit
) {
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
    )
    var location by remember { mutableStateOf<Location?>(null) }
    val nearStops = viewModel.nearStops.collectAsState().value

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
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var locationProvider by remember {
            mutableStateOf(
                when {
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                        LocationManager.NETWORK_PROVIDER
                    }
                    locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER) -> {
                        LocationManager.PASSIVE_PROVIDER
                    }
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                        LocationManager.GPS_PROVIDER
                    }
                    else -> ""
                }
            )
        }

        if (locationProvider.isNotBlank()) {
            DisposableEffect(locationManager) {
                //get location immediately
                val fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        location = it
                    }

                //get location regularly
                val locationListener = object : LocationListener {
                    override fun onLocationChanged(newLocation: Location) {
                        location = newLocation
                    }

                    override fun onProviderEnabled(provider: String) {
                        locationProvider = provider
                    }

                    override fun onProviderDisabled(provider: String) {
                        locationProvider = ""
                        location = null
                    }
                }

                locationManager.requestLocationUpdates(
                    locationProvider,
                    0,
                    0f,
                    locationListener
                )
                onDispose {
                    locationManager.removeUpdates(locationListener)
                }
            }
        } else {
            LocationNotAvailableDialog(
                toSystemLocationSettings = toSystemLocationSetting,
                navigateBack = navigateBack
            )
        }
    }

    LaunchedEffect(location) {
        location?.let {
            viewModel.searchNearStops(it.latitude, it.longitude)
        }
    }

    SearchNearStop(location = location, nearStops = nearStops)
}

@Composable
fun SearchNearStop(location: Location?, nearStops: List<Stop>) {
    Scaffold(topBar = { TitleBar() }) {
        Column {
            if (location != null) {
                Text("目前位置 經度: ${location.latitude} 緯度：${location.longitude}")
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn {
                    if (nearStops.isEmpty()) {
                        item {
                            Text("附近無公車站牌")
                        }
                    } else {
                        itemsIndexed(nearStops) { _, item ->
                            Text("${item.stopName} 經度：${item.positionLatitude} 緯度：${item.positionLongitude} ID:${item.stopId}")
                        }
                    }
                }
            } else {
                Text("無法取得目前位置")
            }
        }
    }
}

@Composable
fun LocationNotAvailableDialog(toSystemLocationSettings: () -> Unit, navigateBack: () -> Unit) {
    AlertDialog(
        title = {
            Text("提醒")
        },
        text = {
            Text("請至系統設定，開啟定位功能")
        },
        confirmButton = {
            Button(onClick = { toSystemLocationSettings() }) {
                Text("定位設定")
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