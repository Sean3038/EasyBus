package com.examprepare.easybus

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.navigation.Destinations.Route
import com.examprepare.easybus.core.navigation.EasyBusApp
import com.examprepare.easybus.core.notification.NotificationManager
import com.examprepare.easybus.feature.model.Direction
import com.examprepare.easybus.feature.notifyapproach.NotifyApproachViewModel
import com.examprepare.easybus.feature.notifyapproach.exception.NoEstimateArrivalFailure
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
                    "${Const.APP_URL}/$Route/${it.routeId}".toUri(),
                    context,
                    MainActivity::class.java
                )

                val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
                    addNextIntentWithParentStack(deepLinkIntent)
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }

                notificationManager.notifyApproach(
                    title = "????????????",
                    message = "??????${it.routeName}???${it.targetMinute}???????????????${it.stopName}???????????????",
                    pendingIntent = deepLinkPendingIntent
                )

            }
        }
        lifecycleScope.launchWhenCreated {
            notifyApproachViewModel.failure.collect {
                when (it) {
                    NoEstimateArrivalFailure -> showToastMessage("????????????????????????")
                    Failure.NetworkConnection -> showToastMessage("??????????????????")
                    Failure.ServerError -> showToastMessage("?????????????????????")
                    Failure.None -> {
                    }
                    else -> showToastMessage("??????????????????")
                }
                notifyApproachViewModel.onDismissFailure()
            }
        }
    }

    private fun toSystemSetting() {
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }

    private fun toSystemLocationSetting() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
