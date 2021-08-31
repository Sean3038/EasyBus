package com.examprepare.easybus.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.examprepare.easybus.feature.model.StopStatus
import com.examprepare.easybus.ui.theme.Blue400
import com.examprepare.easybus.ui.theme.Gray500
import com.examprepare.easybus.ui.theme.Green400
import com.examprepare.easybus.ui.theme.Red400

@Composable
fun StopStatusBadge(estimateTime: Int?, stopStatus: StopStatus) {
    Surface(
        modifier = Modifier
            .width(90.dp)
            .height(60.dp),
        color =
        when (stopStatus) {
            StopStatus.NoOperation, StopStatus.NoShift, StopStatus.NoneDeparture -> Gray500
            StopStatus.NonStop -> Red400
            StopStatus.Normal -> if (estimateTime != null) Blue400 else MaterialTheme.colors.surface
            StopStatus.OnPulledIN -> Green400
            else -> MaterialTheme.colors.surface
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            when (stopStatus) {
                StopStatus.NoOperation ->
                    Text(
                        text = "今日停駛",
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center
                    )
                StopStatus.NoShift ->
                    Text(
                        text = "末班已過",
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center
                    )
                StopStatus.NonStop ->
                    Text(
                        text = "不停靠",
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center
                    )
                StopStatus.None -> {
                }
                StopStatus.NoneDeparture ->
                    Text(
                        text = "尚未發車",
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center
                    )
                StopStatus.Normal ->
                    if (estimateTime != null) {
                        Text(
                            text = "${estimateTime / 60}分",
                            style = MaterialTheme.typography.h5,
                            textAlign = TextAlign.Center
                        )
                    }
                StopStatus.OnPulledIN -> {
                    Text(
                        text = "即將進站",
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}