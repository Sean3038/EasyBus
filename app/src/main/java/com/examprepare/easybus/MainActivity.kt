package com.examprepare.easybus

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.examprepare.easybus.core.navigation.Destinations
import com.examprepare.easybus.core.navigation.Destinations.Route
import com.examprepare.easybus.core.navigation.EasyBusApp
import com.examprepare.easybus.core.notification.NotificationManager
import com.examprepare.easybus.feature.model.Direction
import com.examprepare.easybus.feature.notifyapproach.NotifyApproachViewModel
import com.examprepare.easybus.feature.notifyapproach.model.ApproachInfo
import com.examprepare.easybus.ui.theme.EasyBusTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notificationManager: NotificationManager

    private val notifyApproachViewModel: NotifyApproachViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EasyBusTheme {
                EasyBusApp(
                    observeApproachNotify = this::observeApproachNotify,
                    toSystemSetting = this::toSystemSetting,
                    toSystemLocationSetting = this::toSystemLocationSetting
                )
            }
        }

        lifecycleScope.launchWhenCreated {
            notifyApproachViewModel.approachInfo.collect {
                if (it == ApproachInfo.empty) return@collect

                val context = applicationContext
                val deepLinkIntent = Intent(
                    Intent.ACTION_VIEW,
                    "https://easybus.com/route/${it.routeId}".toUri(),
                    context,
                    MainActivity::class.java
                )

                val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
                    addNextIntentWithParentStack(deepLinkIntent)
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }

                notificationManager.notifyApproach(
                    title = "到站通知",
                    message = "公車${it.routeName}在${it.targetMinute}分鐘內抵達${it.stopName}，點擊查看",
                    pendingIntent = deepLinkPendingIntent
                )
            }
        }
    }

    private fun toSystemSetting() {
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }

    private fun toSystemLocationSetting() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun observeApproachNotify(
        routeId: String,
        stopId: String,
        direction: Direction,
        minute: Int
    ) {
        notifyApproachViewModel.observeApproachNotify(routeId, stopId, direction, minute)
    }

}