package com.doyoonkim.main.tip

import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.ui.TipCategory
import com.doyoonkim.common.ui.TopAppBarWithBackButton

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
            TopAppBarWithBackButton(
                titleText = title,
                onBackPressed = onBackPressed
            )
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        AndroidView(
            modifier = modifier.fillMaxSize().padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    loadUrl(contentUrl)
                }
            }
        )
    }
}