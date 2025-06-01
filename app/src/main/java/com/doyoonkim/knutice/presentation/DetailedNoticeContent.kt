package com.doyoonkim.knutice.presentation

import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.ui.theme.displayBackground
import com.doyoonkim.knutice.viewModel.DetailedNoticeContentViewModel
import okio.Path.Companion.toPath

@Composable
fun DetailedNoticeContent(
    modifier: Modifier = Modifier,
    viewModel: DetailedNoticeContentViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.displayBackground),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            progress = {
                state.loadingStatue
            }
        )

        if (state.requestedNotice.url != "Unknown") {
            AndroidView(
                modifier = Modifier,
                factory = { context ->
                    WebView(context).apply {
                        //Enable Javascript
                        // Security Alert: XSS Vulnerability
                        settings.javaScriptEnabled = true
                        settings.defaultTextEncodingName = "UTF-8"

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                val theme = context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)

                                evaluateJavascript(
                                    """
                                let div_accessibility = document.getElementById('accessibility');
                                let div_header = document.getElementById('header');
                                let div_point = document.getElementById('point');
                                let div_footer = document.getElementById('footer');
                                let div_footer_root = document.getElementById('fb-root');
                                
                                let section_svisual = document.getElementById('svisual');
                                let section_location = document.getElementById('location');
                                let aside_remote = document.getElementById('remote');
                                
                                let p_board_butt = document.getElementsByClassName('board_butt');
                                
                                div_accessibility.remove();
                                div_header.remove();
                                div_footer.remove();
                                
                                aside_remote.remove();
                                p_board_butt[0].remove();
                                
                            """.trimIndent(),
                                ) { result ->
                                    Log.d("Android Web View Client", "RESULT: $result")
                                    visibility = View.VISIBLE
                                }
                                super.onPageFinished(view, url)
                            }
                        }

                        // For Progress Indicator
                        webChromeClient = object: WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                // Update progress status
                                viewModel.updateLoadingStatus(newProgress)
                                super.onProgressChanged(view, newProgress)
                            }
                        }

                        setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                            val request = DownloadManager.Request(Uri.parse(url))
                            val filename = URLUtil.guessFileName(url, contentDisposition, mimetype).also { Log.d("DownloadManager", "Filename: $it") }
                            // save session data before downloading the target file.
                            val cookies = CookieManager.getInstance().getCookie(url)

                            request.apply {
                                setMimeType(mimetype)
                                addRequestHeader("cookie", cookies)
                                addRequestHeader("User-Agent", userAgent)
                                setDescription("Downloading File")
                                setTitle(filename)
//                            allowScanningByMediaScanner()     Deprecated.
                                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                            }
                            val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                            downloadManager.enqueue(request).also {
                                Toast.makeText(context, R.string.text_download, Toast.LENGTH_LONG).show()
                                // Guide user to the File application
                                context.startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
                            }
                        }

                        visibility = View.INVISIBLE
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        loadUrl(state.requestedNotice.url)
                    }
                }
            )
        }
    }
}



@Preview
@Composable
fun DetailedNoticeContent_Preview() {
}