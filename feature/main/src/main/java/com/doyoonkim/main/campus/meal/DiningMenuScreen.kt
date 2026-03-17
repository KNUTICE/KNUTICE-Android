package com.doyoonkim.main.campus.meal

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.ui.TopAppBarWithNavButton
import com.doyoonkim.main.BuildConfig
import com.doyoonkim.main.campus.components.LifecycleAwareWebView

@Composable
fun DiningMenuScreen(
    modifier: Modifier = Modifier,
    hallSelection: String = "",
    onBackClicked: () -> Unit = {  }
) {

    var isHallSelectionProvided by remember { mutableStateOf(false) }

    // WebViewClient
    val diningWebViewClient = remember {
        object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // Check hallSelection is provided.
                if (hallSelection.isNotBlank() && !isHallSelectionProvided) {
                    val call = BuildConfig.DINING_BRIDGE + "('$hallSelection');"

                    // JS Injection
                    view?.evaluateJavascript("""
                        $call
                    """.trimIndent(), null)
                    isHallSelectionProvided = true
                }
                super.onPageFinished(view, url)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBarWithNavButton(
                titleText = "학식 조회"
            ) {
                // Top App Bar back button press
                onBackClicked()
            }
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) {contentPadding ->
        LifecycleAwareWebView(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            url = BuildConfig.KNUTICE_ORIGIN + BuildConfig.DINING_PATH,
            onWebViewCreate = { webView ->
                webView.webViewClient = diningWebViewClient
            },
            onLeaveWebView = {
                // System Back Button/Gesture Handling
                onBackClicked()
            }
        )
    }
}