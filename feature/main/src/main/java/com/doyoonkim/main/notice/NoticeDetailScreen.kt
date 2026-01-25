package com.doyoonkim.main.notice

import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.main.viewmodel.NoticeDetailViewModel
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.AiSummaryDialog
import com.doyoonkim.common.ui.markdown.MarkdownView
import com.doyoonkim.common.ui.TopAppBarWithBackButton
import com.doyoonkim.main.contract.NoticeDetailEvent
import com.doyoonkim.main.contract.NoticeDetailSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: NoticeDetailViewModel,
    noticeInfo: Triple<Int, String, Boolean>,
    onBookmarkCreate: (NoticeVO) -> Unit,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    BackHandler { viewModel.sendUiEvent(NoticeDetailEvent.GoBack) }

    LaunchedEffect(Unit) {
        viewModel.sendUiEvent(NoticeDetailEvent.RequestNotice(noticeInfo.first))

        viewModel.uiSideEffect.collect { effect ->
            when (effect) {
                is NoticeDetailSideEffect.NavToEditBookmark -> onBookmarkCreate(effect.target)
                is NoticeDetailSideEffect.ShowDownloadToast -> {
                    Toast.makeText(context, R.string.text_download, Toast.LENGTH_LONG).show()
                }
                is NoticeDetailSideEffect.NavToDownload -> {
                    // Guide user to the File application
                    context.startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
                }
                is NoticeDetailSideEffect.NavToBack -> onBackPressed()
            }
        }
    }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                titleText = uiState.receivedNotice?.title ?: "",
                onBackPressed = { viewModel.sendUiEvent(NoticeDetailEvent.GoBack) }
            )
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (noticeInfo.second.isNotBlank()) {
                if (!uiState.isLoadingCompleted) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        progress = {
                            uiState.loadingStatus
                        }
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize()
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

                                        evaluateJavascript(
                                            """
                                                let div_accessibility = document.getElementById('accessibility');
                                                let div_header = document.getElementById('header');
                                                let div_footer = document.getElementById('footer');
                                               
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
                                        viewModel.sendUiEvent(
                                            NoticeDetailEvent.UpdateLoadingStatus(newProgress)
                                        )
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
//                                      allowScanningByMediaScanner()     Deprecated.
                                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                                    }
                                    val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                                    downloadManager.enqueue(request).also {
                                        // Guide user to the File application
                                        viewModel.sendUiEvent(NoticeDetailEvent.RequestDownloadAttachment)
                                    }
                                }

                                visibility = View.INVISIBLE
                                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                loadUrl(noticeInfo.second)
                            }
                        }
                    )

                    if (noticeInfo.third) {
                        Column(
                            modifier = Modifier.wrapContentSize()
                                .padding(end = 10.dp, bottom = 30.dp)
                                .align(Alignment.BottomEnd),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (uiState.receivedNotice?.isSummaryAvailable == true) {
                                FloatingActionButton(
                                    modifier = Modifier.wrapContentSize(),
                                    onClick = {
                                        viewModel.sendUiEvent(NoticeDetailEvent.RequestNoticeSummary)
                                    },
                                    containerColor = MaterialTheme.colorScheme.variantPurple
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.outline_wand_stars_24),
                                        contentDescription = "Summary",
                                        tint = Color.White
                                    )
                                }
                            }

                            FloatingActionButton(
                                modifier = Modifier.wrapContentSize(),
                                onClick = {
                                    viewModel.sendUiEvent(NoticeDetailEvent.RequestBookmarkCreation)
                                },
                                containerColor = if (uiState.isReceived) {
                                    MaterialTheme.colorScheme.variantPurple
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

                    if (uiState.isSummarizationVisible) {
                        ModalBottomSheet(
                            onDismissRequest = { viewModel.sendUiEvent(NoticeDetailEvent.DismissBottomSheet) },
                            modifier = Modifier.background(Color.Transparent),
                            sheetState = bottomSheetState,
                            containerColor = MaterialTheme.colorScheme.displayBackground,
                            scrimColor = Color.Transparent
                        ) {
                            AiSummaryDialog(
                                modifier = Modifier.height(
                                    with(LocalConfiguration.current) {
                                        screenHeightDp * 0.6
                                    }.dp
                                ).verticalScroll(rememberScrollState()),
                                icon = R.drawable.ai_logo,
                                header = stringResource(R.string.header_ai_summary),
                                attribution = stringResource(R.string.attribution_knutice_ai),
                                footer = stringResource(R.string.footer_ai_disclaimer),
                                containerColor = MaterialTheme.colorScheme.secondaryBackground,
                                primaryTextColor = MaterialTheme.colorScheme.title,
                                secondaryTextColor = MaterialTheme.colorScheme.subTitle,
                                isContentAvailable = uiState.summarizedContent.isNullOrEmpty()
                            ) {
                                MarkdownView(
                                    modifier = Modifier.wrapContentSize()
                                        .padding(5.dp),
                                    nodes = uiState.summarizedContent!!,
                                    containerColor = MaterialTheme.colorScheme.secondaryBackground,
                                    primaryTextColor = MaterialTheme.colorScheme.title,
                                    secondaryTextColor = MaterialTheme.colorScheme.subTitle,
                                    highlightColor = MaterialTheme.colorScheme.variantPurple
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun NoticeDetailScreen_Preview() {

}