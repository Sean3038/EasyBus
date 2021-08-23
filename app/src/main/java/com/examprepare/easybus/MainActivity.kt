package com.examprepare.easybus

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.examprepare.easybus.core.navigation.EasyBusApp
import com.examprepare.easybus.ui.theme.EasyBusTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EasyBusTheme {
                EasyBusApp(toSystemSetting = {
                    toSystemSetting()
                })
            }
        }
    }

    fun toSystemSetting() {
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }
}