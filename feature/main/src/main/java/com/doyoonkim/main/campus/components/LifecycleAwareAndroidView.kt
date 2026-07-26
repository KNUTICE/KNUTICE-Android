package com.doyoonkim.main.campus.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.doyoonkim.common.R
import com.doyoonkim.common.ui.PlaceholderScreen

// App-Level Issue
// Context Helper. Mitigate potential issue caused by Injected Context via Dagger.
private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun LifecycleAwareWebView(
    modifier: Modifier = Modifier,
    dynamicThemeEnabled: Boolean = true,
    url: String,
    onWebViewCreate: (WebView) -> Unit = { },
    onWebViewDestroy: (WebView) -> Unit = { },
    onLeaveWebView: () -> Unit
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var lastLoadedUrl by remember { mutableStateOf("") }

    // OS-level Resource Failure: When WebViewEngine is not available (WebView Engine Updates or OEM Swaps)
    var isWebViewEngineUnavailable by remember { mutableStateOf(false) }

    // Access Context via CompositionLocal
    val context = LocalContext.current
    val activityContext = remember(context) { context.findActivity() }

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
                Lifecycle.Event.ON_RESUME -> {
                    webViewInstance?.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    webViewInstance?.onPause()
                }
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

                // Chromium Memory Tear Down
                target.stopLoading()
                target.clearHistory()
                target.removeAllViews()
                target.loadUrl("about:blank") // Clear Rendering Process.
                // Remove Taget WebView from its ViewTree. (Allow GC collects it)
                (target.parent as? ViewGroup)?.removeView(target)
                target.destroy()
            }
            webViewInstance = null
        }
    }

    // If WebViewEngine is not available
    if (isWebViewEngineUnavailable || activityContext == null) {
        PlaceholderScreen(
            modifier = modifier,
            imageResource = R.drawable.question_mark,
            contentText = stringResource(R.string.text_content_unavailable)
        )
    } else {
        AndroidView(
            modifier = modifier,
            factory = { _ ->
                // Instead of using context provided via factory lambda, use explicitly retrieved context
                // to perform Early Exit to avoid unnecessary heavy composition task.
                try {
                    WebView(activityContext).apply {
                        // Application Default Configuration for Hosting WebApp

                        // Let WebView uses all available spaces assigned.
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        if (dynamicThemeEnabled) setBackgroundColor(Color.TRANSPARENT)

                        // WebView Default Settings for seamless UX in Hybrid condition.
                        overScrollMode = View.OVER_SCROLL_NEVER
                        isVerticalScrollBarEnabled = false
                        isHorizontalScrollBarEnabled = false

                        // WebView Performance Settings
                        settings.apply {
                            @SuppressLint("SetJavaScriptEnabled")
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            // Enable Global Cache Policy
                            cacheMode = WebSettings.LOAD_DEFAULT

                            // Ensure responsiveness
                            useWideViewPort = true // support HTML viewport tag.
                            loadWithOverviewMode = true // Zoom out to fit entire content initially.
                        }

                        // Prevent user long-click on WebView Content (by consuming it.)
                        setOnLongClickListener { true }

                        // Additional Configuration injected (Client, Listener)
                        onWebViewCreate(this)

                        // allocate the Instance
                        webViewInstance = this
                    }
                } catch (e: Exception) {
                    // Potential Exception Thrown: AndroidRuntimeException, ResourceNotFoundException
                    isWebViewEngineUnavailable = true

                    Log.e("LifecycleAwareWebView", "WebView Unavailable ${e.localizedMessage}")
                    // Return fallback dummy screen
                    View(activityContext)
                }
            },
            update = { view ->
                // Load WebApp
                if (view is WebView && lastLoadedUrl != url) {
                    view.loadUrl(url)
                    lastLoadedUrl = url
                }
            }
        )
    }
}
