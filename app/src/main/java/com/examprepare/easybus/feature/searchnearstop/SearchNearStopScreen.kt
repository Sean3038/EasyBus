import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.examprepare.easybus.Const
import com.examprepare.easybus.core.ui.TitleBar
import com.examprepare.easybus.core.util.rememberMapViewWithLifecycle
import com.examprepare.easybus.feature.model.Station
import com.examprepare.easybus.feature.searchnearstop.SearchNearStopViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.launch


@Composable
fun SearchNearStopScreen(
    viewModel: SearchNearStopViewModel,
    toSystemSettings: () -> Unit,
    toSystemLocationSetting: () -> Unit,
    navigateBack: () -> Unit
) {
    val location = remember { mutableStateOf<Location?>(null) }
    val nearStations = viewModel.nearStations.collectAsState().value

    RequestLocation(
        location = location,
        toSystemSettings = toSystemSettings,
        toSystemLocationSettings = toSystemLocationSetting,
        navigateBack = navigateBack
    )

    LaunchedEffect(location.value) {
        location.value?.let {
            viewModel.searchNearStops(it.latitude, it.longitude)
        }
    }

    SearchNearStop(location = location.value, nearStations = nearStations)
}

@Composable
fun SearchNearStop(location: Location?, nearStations: List<Station>) {
    Scaffold(topBar = { TitleBar() }) {
        Column {
            if (location != null) {
                Text("目前位置 經度: ${location.latitude} 緯度：${location.longitude}")
                Spacer(modifier = Modifier.height(10.dp))

                val scope = rememberCoroutineScope()
                val mapView = rememberMapViewWithLifecycle()

                AndroidView(factory = { mapView }) {
                    scope.launch {
                        val map = mapView.awaitMap()
                        map.clear()

                        val destination = LatLng(location.latitude, location.longitude)

                        val markerOptions = MarkerOptions()
                            .title("目前位置")
                            .position(destination)
                        map.addMarker(markerOptions)

                        map.addCircle(
                            CircleOptions()
                                .center(LatLng(location.latitude, location.longitude))
                                .radius(500.0)
                                .strokeWidth(2f)
                                .strokeColor(Color.CYAN)
                                .fillColor(Color.argb(64, 0, 0, 255))
                        )

                        nearStations.forEach {
                            val stopPosition = LatLng(it.positionLat, it.positionLon)
                            val markPosition = MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                                .title(it.stationName)
                                .position(stopPosition)
                            map.addMarker(markPosition)
                        }

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15f))
                    }
                }
            } else {
                Text("無法取得目前位置")
            }
        }
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocation(
    location: MutableState<Location?>,
    toSystemSettings: () -> Unit,
    toSystemLocationSettings: () -> Unit,
    navigateBack: () -> Unit
) {
    AskLocationPermission(
        toSystemSettings = toSystemSettings,
        navigateBack = navigateBack
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
                        location.value = it
                    }

                //get location regularly
                val locationListener = object : LocationListener {
                    override fun onLocationChanged(newLocation: Location) {
                        location.value = newLocation
                    }

                    override fun onProviderEnabled(provider: String) {
                        locationProvider = provider
                    }

                    override fun onProviderDisabled(provider: String) {
                        locationProvider = ""
                        location.value = null
                    }
                }

                locationManager.requestLocationUpdates(
                    locationProvider,
                    Const.GET_NEAR_STATION_INTERVAL_MILLISECONDS,
                    0f,
                    locationListener
                )
                onDispose {
                    locationManager.removeUpdates(locationListener)
                }
            }
        } else {
            LocationNotAvailableDialog(
                toSystemLocationSettings = toSystemLocationSettings,
                navigateBack = navigateBack
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskLocationPermission(
    toSystemSettings: () -> Unit,
    navigateBack: () -> Unit,
    onGrantAll: @Composable () -> Unit
) {
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
    )

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
        onGrantAll()
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