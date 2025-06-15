package com.doyoonkim.main.preference

import android.webkit.WebView
import androidx.activity.compose.BackHandler
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
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                loadUrl("https://knutice.github.io/KNUTICE-OpenSourceLicense/Android/opensource.html")
            }
        }
    )
}