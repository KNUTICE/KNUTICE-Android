package com.doyoonkim.widget.notices

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.wrapContentSize
import com.doyoonkim.widget.model.WidgetState
import com.doyoonkim.widget.notices.components.NoticeWidgetContainer
import com.doyoonkim.widget.notices.components.WidgetButton
import com.doyoonkim.widget.notices.components.WidgetHorizontalDivider
import com.doyoonkim.widget.notices.components.WidgetNoticeContainer
import com.doyoonkim.widget.notices.components.WidgetPlaceholder
import com.doyoonkim.widget.util.NoticeWidgetPhase
import com.doyoonkim.widget.util.NoticeWidgetUtil
import com.doyoonkim.widget.util.NoticeWidgetUtil.Companion.validatePhase

@Composable
fun NoticeWidgetContents(
    modifier: GlanceModifier = GlanceModifier,
    state: WidgetState
) {
    val context = LocalContext.current

    val phase = state.validatePhase()

    when (phase) {
        is NoticeWidgetPhase.NotConfigured -> {
            WidgetPlaceholder(
                modifier = modifier,
                titleText = "위젯 설정이 필요해요",
                textColor = GlanceTheme.colors.primary,
                containerColor = GlanceTheme.colors.primaryContainer
            ) {
                WidgetButton(
                    modifier = modifier.wrapContentSize(),
                    text = "위젯 설정하기",
                    textColor = GlanceTheme.colors.primary,
                    containerColor = GlanceTheme.colors.secondaryContainer
                ) {
                    actionStartActivity(
                        // Access Application via DeepLink
                        NoticeWidgetUtil.createDeeplinkIntent(
                            packageName = context.packageName,
                            type = "WIDGET_SETTINGS"
                        )
                    ).also { Log.d("Widget", "Click Event") }
                }
            }
        }
        is NoticeWidgetPhase.FetchFailure -> {
            NoticeWidgetContainer(
                modifier = modifier,
                title = state.category,
                titleColor = GlanceTheme.colors.primary,
                containerColor = GlanceTheme.colors.primaryContainer,
                contentContainerColor = GlanceTheme.colors.secondaryContainer
            ) { contentModifier ->
                WidgetPlaceholder(
                    modifier = contentModifier,
                    titleText = "공지를 가져올 수 없어요",
                    textColor = GlanceTheme.colors.primary,
                    containerColor = GlanceTheme.colors.secondaryContainer
                ) { /* NO EXTRA CONTENTS */ }
            }
        }
        is NoticeWidgetPhase.FetchCompleted -> {
            NoticeWidgetContainer(
                modifier = modifier,
                title = state.category,
                titleColor = GlanceTheme.colors.primary,
                containerColor = GlanceTheme.colors.primaryContainer,
                contentContainerColor = GlanceTheme.colors.secondaryContainer
            ) { contentModifier ->
                state.notices.forEachIndexed { index, vo ->
                    // Internal Container.
                    WidgetNoticeContainer(
                        modifier = contentModifier,
                        notice = vo,
                        onNoticeClicked = {
                            actionStartActivity(
                                // "noticeDetail/$id/${Uri.encode(url)}/${true}"
                                NoticeWidgetUtil.createDeeplinkIntent(
                                    packageName = context.packageName,
                                    deeplink = "noticeDetail/${vo.id}/${Uri.encode(vo.contentUrl)}/${true}",
                                    type = null
                                )
                            )
                        }
                    )
                    if (index < state.notices.size - 1) {
                        WidgetHorizontalDivider(
                            GlanceTheme.colors.secondary,
                            1.2.dp
                        )
                    }
                }
            }
        }
    }
}