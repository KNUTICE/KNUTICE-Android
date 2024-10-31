package com.doyoonkim.knutice.presentation

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun OpenSourceLicenseNotice(
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxWidth().padding(5.dp),
        factory = { context ->
            WebView(context).apply {
                loadUrl("https://knutice.github.io/KNUTICE-OpenSourceLicense/Android/opensource.html")
            }
        }
    )
}

@Composable
@Preview
fun OpenSourceLicenseNotice_Preview() {
    OpenSourceLicenseNotice()
}