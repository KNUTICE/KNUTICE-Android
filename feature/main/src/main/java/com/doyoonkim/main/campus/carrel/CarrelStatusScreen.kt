package com.doyoonkim.main.campus.carrel

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.webkit.WebViewCompat
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.main.BuildConfig
import com.doyoonkim.main.campus.components.LifecycleAwareWebView
import com.doyoonkim.main.campus.model.WebAppAction
import com.doyoonkim.main.campus.model.WebBridgeEnvelop
import com.doyoonkim.main.viewmodel.CarrelStatusViewModel
import kotlinx.serialization.json.Json

@Composable
fun CarrelStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: CarrelStatusViewModel,
    roomId: String = "",
    onBackClicked: () -> Unit
) {
    val tokenState by viewModel.deviceToken.collectAsStateWithLifecycle()

    // Configuration State
    val isKeyProvided = remember { mutableStateOf(false) }
    val isRoomSelectionProvided = remember { mutableStateOf(false) }

    // WebView Message Listener
    val webMessageListener = remember {
        WebViewCompat.WebMessageListener { _, compat, _, _, _ ->
            // Receive message from the WebApp
            val rawMessage = compat.data ?: return@WebMessageListener

            // Check size of rawJason
            // If rawMessage is larget than 4KB, process it in background.
            if (rawMessage.length > 4096) {
                // TODO: Implement Heavy work processor.
                return@WebMessageListener
            }

            // Process Small JSON message on MainThread.
            try {
                val bridgeEnvelop = Json.decodeFromString<WebBridgeEnvelop>(rawMessage)
                when (bridgeEnvelop.type) {
                    WebAppAction.CLOSE_WEBVIEW.name -> { onBackClicked() }
                    else ->  Unit
                }
            } catch (e: Exception) {
                Log.d("WebMessageListener", "Invalid Envelope ${e.stackTraceToString()}")
            }
        }
    }

    // WebViewClient
    val webViewClient = remember {
        object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // If view is ready, return onPageFinished immediately to avoid multiple JS execution.
                if (!isKeyProvided.value) {
                    val call = "${BuildConfig.CARREL_BRIDGE}('${tokenState}');"
                    val script = "javascript:$call;"
                    view?.evaluateJavascript(script, null)

                    // Prevent calling onPageFinished multiple times when view is ready.
                    isKeyProvided.value = true
                }

                if (roomId.isNotBlank() && !isRoomSelectionProvided.value) {
                    val call = "${BuildConfig.CARREL_ROOM_BRIDGE}('${roomId}');"
                    val script = "javascript:$call"
                    view?.evaluateJavascript(script, null)

                    isRoomSelectionProvided.value = true
                }
                super.onPageFinished(view, url)
            }
        }
    }

    LifecycleAwareWebView(
        modifier = modifier.fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(MaterialTheme.colorScheme.displayBackground),
        url = BuildConfig.KNUTICE_ORIGIN + BuildConfig.CARREL_PATH,
        onWebViewCreate = { webView ->
            webView.webViewClient = webViewClient

            // Register WebMessageListener
            // Suppress RequiresFeature (minSdk is 31)
            @SuppressLint("RequiresFeature")
            WebViewCompat.addWebMessageListener(
                webView,
                "androidBridge",
                setOf(BuildConfig.KNUTICE_ORIGIN),
                webMessageListener
            )
        },
        onLeaveWebView = {
            // Provide onBack action that leave composable itself.
            onBackClicked()
        }
    )
}