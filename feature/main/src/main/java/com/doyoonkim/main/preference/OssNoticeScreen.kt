package com.doyoonkim.main.preference

import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun OssNoticeScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    BackHandler { onBackPressed() }

    AndroidView(
        modifier = modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom)),
        factory = { context ->
            WebView(context).apply {
                loadUrl("https://knutice.github.io/KNUTICE-OpenSourceLicense/Android/opensource.html")
            }
        }
    )
}