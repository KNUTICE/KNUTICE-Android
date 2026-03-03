package com.doyoonkim.main.tip

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.doyoonkim.common.R
import com.doyoonkim.common.ui.TipCategory
import com.doyoonkim.common.ui.TopAppBarWithNavButton
import com.doyoonkim.main.campus.components.LifecycleAwareWebView

@Composable
fun TipDetailScreen(
    modifier: Modifier = Modifier,
    tipCategory: String,
    contentUrl: String,
    onBackPressed: () -> Unit
) {
    BackHandler { onBackPressed() }

    val title = when (tipCategory) {
        TipCategory.GENERAL_TIP.name -> stringResource(R.string.text_tip_general)
        TipCategory.UPDATES.name -> stringResource(R.string.text_tip_updates)
        TipCategory.SYS_NOTICE.name -> stringResource(R.string.text_tip_sys_notice)
        else -> stringResource(R.string.app_name)
    }

    Scaffold(
        topBar = {
            TopAppBarWithNavButton(
                titleText = title,
                onBackPressed = onBackPressed
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier.wrapContentSize()
                .padding(horizontal = 10.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            LifecycleAwareWebView(
                modifier = modifier.fillMaxSize()
                    .padding(innerPadding),
                dynamicThemeEnabled = false,
                url = contentUrl
            ) {
                onBackPressed()
            }
        }
    }
}