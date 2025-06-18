package com.doyoonkim.main.notice

import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.res.Configuration
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
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.main.viewmodel.NoticeDetailViewModel
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.textPurple

@Composable
fun NoticeDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: NoticeDetailViewModel,
    noticeInfo: Triple<Int, String, Boolean>,
    onBookmarkCreate: (NoticeVO) -> Unit,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler { onBackPressed() }

    LaunchedEffect(uiState.isReceived) {
        if (!uiState.isReceived) viewModel.getTargetNoticeById(noticeInfo.first)
    }

    if (noticeInfo.second.isNotBlank()) {
        Box(
            modifier = modifier
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
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
                            val request = DownloadManager.Request(url.toUri())
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
                        loadUrl(noticeInfo.second)
                    }
                }
            )

            if (noticeInfo.third) {
                FloatingActionButton(
                    modifier = Modifier.wrapContentSize()
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                        .padding(end = 10.dp, bottom = 30.dp)
                        .align(Alignment.BottomEnd),
                    onClick = {
                        if (uiState.isReceived) uiState.receivedNotice?.let(onBookmarkCreate)
                    },
                    containerColor = if (uiState.isReceived) {
                        MaterialTheme.colorScheme.textPurple
                    } else {
                        MaterialTheme.colorScheme.subTitle
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add to bookmark",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun NoticeDetailScreen_Preview() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        FloatingActionButton(
            modifier = Modifier.wrapContentSize()
                .padding(end = 10.dp, bottom = 30.dp)
                .align(Alignment.BottomEnd),
            onClick = {
                // TODO Set proper paramter for onBookmarkCreate()
//                if (true) uiState.receivedNotice?.let()
            },
            containerColor = if (true) {
                MaterialTheme.colorScheme.textPurple
            } else {
                MaterialTheme.colorScheme.subTitle
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add to bookmark",
                tint = Color.White
            )
        }
    }
}