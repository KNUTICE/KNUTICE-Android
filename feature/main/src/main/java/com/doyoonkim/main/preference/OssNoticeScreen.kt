package com.doyoonkim.main.preference

import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.doyoonkim.common.ui.TopAppBarWithBackButton
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.displayBackground

@Composable
fun OssNoticeScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    BackHandler { onBackPressed() }

    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                titleText = stringResource(R.string.oss_notice),
                onBackPressed = onBackPressed
            )
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        AndroidView(
            modifier = modifier.fillMaxSize().padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    loadUrl("https://knutice.github.io/KNUTICE-OpenSourceLicense/Android/opensource.html")
                }
            }
        )
    }
}