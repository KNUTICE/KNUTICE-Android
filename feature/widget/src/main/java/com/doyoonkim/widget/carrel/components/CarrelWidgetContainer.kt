@file:OptIn(ExperimentalGlancePreviewApi::class)

package com.doyoonkim.widget.carrel.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.doyoonkim.common.R
import com.doyoonkim.model.CarrelRoomStatusVO
import com.doyoonkim.widget.carrel.action.CarrelStatusRefreshAction
import com.doyoonkim.widget.components.WidgetHeader
import com.doyoonkim.widget.model.CarrelWidgetState
import com.doyoonkim.widget.theme.KnuticeWidgetTheme
import com.doyoonkim.widget.util.CarrelStatusBitmapGenerator
import com.doyoonkim.widget.util.NoticeWidgetUtil
@Composable
fun CarrelWidgetContainer(
    state: CarrelWidgetState,
    isLoading: Boolean = false,
    lastSync: Long = 0L
) {
    val context = LocalContext.current
    Log.d("Widget", "IsLoading: $isLoading")

    val titleString = context.getString(R.string.knutice_carrel_widget_title)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(10.dp)
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WidgetHeader(
            modifier = GlanceModifier.defaultWeight(),
            title = titleString,
            lastUpdated = lastSync,
            iconResId = R.drawable.outline_sync_24,
            onRefreshClick = actionRunCallback<CarrelStatusRefreshAction>()
        )
        Spacer(GlanceModifier.height(5.dp))

        Row(
            modifier = GlanceModifier.fillMaxWidth()
                .wrapContentHeight()
                .defaultWeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.status.forEachIndexed { idx, vo ->
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .defaultWeight()
                        .cornerRadius(15.dp)
                        .background(GlanceTheme.colors.secondaryContainer)
                        .clickable(
                            actionStartActivity(
                                NoticeWidgetUtil.createDeeplinkIntent(
                                    context.packageName,
                                    deeplink = "reading-room?roomId=${vo.id}&seat=0",
                                    type = null
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = GlanceModifier.background(Color.Transparent)
                            .padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val carrelName = vo.name.split(" ").firstOrNull() ?: vo.name
                        val currentRemainingSeats = vo.total - vo.occupied

                        Text(
                            text = carrelName,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = GlanceTheme.colors.primary
                            ),
                            maxLines = 2,
                            modifier = GlanceModifier
                        )

                        // Bitmap Image Generation
                        val statusBitmap = CarrelStatusBitmapGenerator.generateStatusBitmap(
                            context,
                            vo.occupied,
                            vo.total,
                            widgetSizeDp = 90f
                        )

                        Image(
                            provider = ImageProvider(statusBitmap),
                            contentDescription = null,
                            modifier = GlanceModifier
                                .size(90.dp)
                                .padding(vertical = 5.dp)
                        )

                        Text(
                            text = context.getString(
                                R.string.text_carrel_widget_remaining_seat,
                                currentRemainingSeats
                            ),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = GlanceTheme.colors.primary
                            ),
                            modifier = GlanceModifier
                        )
                    }

                    // Loading Indication
                    if (isLoading) {
                        Log.d("Widget", "Loading Dimming applied")
                        Box(
                            modifier = GlanceModifier.fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = GlanceTheme.colors.surfaceVariant
                            )
                        }
                    }
                }

                if (idx < state.status.size - 1) {
                    Spacer(GlanceModifier.width(7.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun CarrelWidget_Preview() {
    val dummyState = CarrelWidgetState(
        status = listOf(
            CarrelRoomStatusVO(
                name = "제1집중 학습 ZONE (콘센트)",
                total = 300,
                occupied = 10
            ),
            CarrelRoomStatusVO(
                name = "제2집중 학습 ZONE",
                total = 300,
                occupied = 160
            ),
            CarrelRoomStatusVO(
                name = "제3협업 학습 ZONE (콘센트)",
                total = 300,
                occupied = 280
            )
        )
    )

    GlanceTheme(colors = KnuticeWidgetTheme.colors) {
        CarrelWidgetContainer(dummyState, false, System.currentTimeMillis())
    }
}
