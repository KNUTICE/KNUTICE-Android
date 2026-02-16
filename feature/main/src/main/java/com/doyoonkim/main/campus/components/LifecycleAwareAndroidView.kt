package com.doyoonkim.main.campus.components

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun LifecycleAwareWebView(
    modifier: Modifier = Modifier,
    url: String,
    onWebViewCreate: (WebView) -> Unit = {  },
    onWebViewDestroy: (WebView) -> Unit = {  },
    onLeaveWebView: () -> Unit
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    // WebView BackHandler
    BackHandler {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onLeaveWebView()
        }
    }

    // Local Lifecycle Owner
    val lifecycleOwner = LocalLifecycleOwner.current
    // Manage Lifecycle of View.
    DisposableEffect(lifecycleOwner) {
        // Lifecycle Observer
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> { webViewInstance?.onResume() }
                Lifecycle.Event.ON_PAUSE -> { webViewInstance?.onPause() }
                else -> Unit
            }
        }

        // Register Observer to Owner
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        // onDispose
        onDispose {
            // Unregister Observer
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)

            // Destroy WebView + unregister it from memory.
            // Capture 'at-moment' value for preventing potential race condition.
            val target = webViewInstance
            if (target != null) {
                onWebViewDestroy(target)
                target.destroy()
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                // Application Default Configuration for Hosting WebApp

                // Let WebView uses all available spaces assigned.
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(Color.TRANSPARENT)

                // WebView Default Settings for seamless UX in Hybrid condition.
                overScrollMode = View.OVER_SCROLL_NEVER
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                // WebView Performance Settings
                settings.apply {
                    @SuppressLint("SetJavaScriptEnabled")
                    javaScriptEnabled = true
                    domStorageEnabled = true

                    // Ensure responsiveness
                    useWideViewPort = true          // support HTML viewport tag.
                    loadWithOverviewMode = true     // Zoom out to fit entire content initially.
                }

                // Prevent user long-click on WebView Content (by consuming it.)
                setOnLongClickListener { true }

                // Additional Configuration injected (Client, Listener)
                onWebViewCreate(this)

                // Load WebApp
                loadUrl(url)
            }
        },
        update = { view -> webViewInstance = view }
    )
}