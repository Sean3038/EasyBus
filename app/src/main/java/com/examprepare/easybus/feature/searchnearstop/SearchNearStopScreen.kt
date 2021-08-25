package com.examprepare.easybus.feature.searchnearstop

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.examprepare.easybus.Const
import com.examprepare.easybus.R
import com.examprepare.easybus.core.ui.TitleBar
import com.examprepare.easybus.core.util.rememberMapViewWithLifecycle
import com.examprepare.easybus.feature.model.Station
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.launch


@Composable
fun SearchNearStopScreen(
    viewModel: SearchNearStopViewModel,
    toSystemSettings: () -> Unit,
    toSystemLocationSetting: () -> Unit,
    toStation: (String) -> Unit,
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
            viewModel.searchNearStops(
                Const.GET_NEAR_STATION_RADIUS_METERS,
                it.latitude,
                it.longitude
            )
        }
    }

    SearchNearStop(location = location.value, nearStations = nearStations)
}

@Composable
fun SearchNearStop(location: Location?, nearStations: List<Station>) {
    Scaffold(topBar = { TitleBar() }) {
        Column {
            if (location != null && nearStations.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(16.dp),
                    text = "搜尋附近${Const.GET_NEAR_STATION_RADIUS_METERS}公尺內站牌，每${Const.GET_NEAR_STATION_INTERVAL_MILLISECONDS / 60000}分鐘更新"
                )

                val context = LocalContext.current
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

                        map.setInfoWindowAdapter(StationInfoAdapter(context, nearStations))
                        map.addCircle(
                            CircleOptions()
                                .center(LatLng(location.latitude, location.longitude))
                                .radius(Const.GET_NEAR_STATION_RADIUS_METERS.toDouble())
                                .strokeWidth(2f)
                                .strokeColor(Color.CYAN)
                                .fillColor(Color.argb(64, 0, 0, 255))
                        )
                        map.addMarker(markerOptions)
                        nearStations.forEachIndexed { index, station ->
                            val stopPosition = LatLng(station.positionLat, station.positionLon)
                            val markPosition = MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                                .title(station.stationName)
                                .position(stopPosition)
                                .snippet(index.toString())
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

class StationInfoAdapter constructor(
    private val context: Context,
    private val stations: List<Station>
) :
    GoogleMap.InfoWindowAdapter {

    private val stationInfoView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_mark_info, null)

    override fun getInfoWindow(p0: Marker): View? = null

    override fun getInfoContents(p0: Marker): View? {
        val index = p0.snippet?.toInt()
        index?.let {
            val station: Station = stations[index]
            val textStationTitle = stationInfoView.findViewById<TextView>(R.id.textStationTitle)
            val textStationAddress = stationInfoView.findViewById<TextView>(R.id.textStationAddress)
            val listRouts = stationInfoView.findViewById<RecyclerView>(R.id.listRoutes)
            textStationTitle.text = station.stationName
            textStationAddress.text = station.stationAddress
            listRouts.layoutManager = GridLayoutManager(context, 3)
            listRouts.adapter = RouteNameAdapter(context, station.routeItems)
            return stationInfoView
        }
        return null
    }
}

class RouteNameAdapter(
    private val context: Context,
    private val routeItems: List<Station.RouteItem>
) :
    RecyclerView.Adapter<RouteNameAdapter.RouteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder =
        RouteViewHolder(LayoutInflater.from(context).inflate(R.layout.item_route, parent, false))

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val item = routeItems[position]
        holder.textRouteName.text = item.routeName
    }

    override fun getItemCount(): Int = routeItems.size

    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textRouteName: TextView = itemView.findViewById(R.id.textRouteName)
    }
}