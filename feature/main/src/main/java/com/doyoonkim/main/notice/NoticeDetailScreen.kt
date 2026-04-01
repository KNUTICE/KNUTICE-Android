package com.doyoonkim.main.notice

import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.doyoonkim.common.ui.ConfigurableTopAppBar
import com.doyoonkim.common.ui.markdown.MarkdownView
import com.doyoonkim.main.campus.components.LifecycleAwareWebView
import com.doyoonkim.main.contract.NoticeDetailEvent
import com.doyoonkim.main.contract.NoticeDetailSideEffect
import java.io.File
import java.net.URLDecoder
import java.util.Locale

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

    LaunchedEffect(uiState.abTestLayoutState) {
        // Activate AB Test
        viewModel.sendUiEvent(NoticeDetailEvent.ActivateAbTest)
    }

    LaunchedEffect(Unit) {
        viewModel.sendUiEvent(NoticeDetailEvent.RequestNotice(noticeInfo.first, noticeInfo.third))

        viewModel.uiSideEffect.collect { effect ->
            when (effect) {
                is NoticeDetailSideEffect.NavToEditBookmark -> onBookmarkCreate(effect.target)
                is NoticeDetailSideEffect.ShowDownloadToast -> {
                    Toast.makeText(context, R.string.text_download, Toast.LENGTH_LONG).show()
                }
                is NoticeDetailSideEffect.ShowDownloadUnableToast -> {
                    Toast.makeText(context, R.string.text_download_not_available, Toast.LENGTH_LONG).show()
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
    val bottomSheetHeight = with(LocalConfiguration.current) {
        screenHeightDp * 0.6
    }.dp

    // WebViewClient
    val webClient = remember {
        object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                view?.evaluateJavascript(
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
                    view.visibility = View.VISIBLE
                }
                super.onPageFinished(view, url)
            }
        }
    }

    val chromeClient = remember {
        object: WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                // Update progress status
                viewModel.sendUiEvent(
                    NoticeDetailEvent.UpdateLoadingStatus(newProgress)
                )
                super.onProgressChanged(view, newProgress)
            }
        }
    }

    val downloadListener = remember {
        DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val request = DownloadManager.Request(url.toUri())

            // URL (UTF-8) Encoded Raw Filename
            val rawFilename = URLUtil.guessFileName(url, contentDisposition, mimetype)
            val decodedFilename = try {
                URLDecoder.decode(rawFilename, "UTF-8")
            } catch (e: Exception) {
                Log.d("DownloadListener", "Unable to decode the raw filename")
                rawFilename
            }.run {
                // Clean filename
                File(this).name
            }

            // Explicitly provide mimetype to process implicit intent via Notification correctly.
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(url)
                ?: decodedFilename.substringAfterLast(".", "")
            var resolvedMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase(Locale.ROOT)
            ) ?: mimetype

            // Process specific file extension (HWP)
            if (decodedFilename.endsWith(".hwp", ignoreCase = true)) {
                // override resolve Mime Type via MimeTypeMap.
                resolvedMimeType = "application/x-hwp"
            }

            // save session data before downloading the target file.
            val cookies = CookieManager.getInstance().getCookie(url)

            Log.d("NoticeDetailScreen", "Processed Filename: $decodedFilename")
            request.apply {
                setMimeType(resolvedMimeType)
                addRequestHeader("cookie", cookies)
                addRequestHeader("User-Agent", userAgent)
                setDescription("Downloading File")
                setTitle(decodedFilename)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    decodedFilename
                )
            }

            // Potential Error Handling
            try {
                val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request).also {
                    // Guide user to the File application
                    viewModel.sendUiEvent(NoticeDetailEvent.RequestDownloadAttachment(true))
                }
            } catch (e: Exception) {
                // Potential Error: Download Manager is disabled
                viewModel.sendUiEvent(NoticeDetailEvent.RequestDownloadAttachment(false))
            }
        }
    }

    Scaffold(
        topBar = {
            ConfigurableTopAppBar(
                titleText = uiState.receivedNotice?.title ?: "",
                onBackPressed = { viewModel.sendUiEvent(NoticeDetailEvent.GoBack) }
            ) {
                // Layout_B (Bookmark Button on TopAppBar
                if (uiState.isBookmarkButtonVisible && uiState.abTestLayoutState.isAiFabBottomEnd) {
                    IconButton(
                        onClick = { viewModel.sendUiEvent(NoticeDetailEvent.RequestBookmarkCreation) }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.outline_bookmark_24),
                            contentDescription = "Add Bookmark",
                            modifier = Modifier.wrapContentSize(),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.title)
                        )
                    }
                }
            }
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
                    LifecycleAwareWebView(
                        modifier = Modifier.fillMaxSize(),
                        url = noticeInfo.second,
                        dynamicThemeEnabled = false,
                        onWebViewCreate = { webView ->
                            with(webView) {
                                settings.defaultTextEncodingName = "UTF-8"
                                webViewClient = webClient
                                webChromeClient = chromeClient

                                // Set Download Listener
                                setDownloadListener(downloadListener)

                                visibility = View.INVISIBLE
                                // Enabled due to WebPage configuration on KNUT side.
                                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            }
                        },
                    ) { viewModel.sendUiEvent(NoticeDetailEvent.GoBack) }

                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .wrapContentHeight()
                            .padding(start = 10.dp, end = 10.dp, bottom = 30.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        if (uiState.abTestLayoutState.isAiFabBottomStart && uiState.isBookmarkButtonVisible) {
                            FloatingActionButton(
                                modifier = Modifier.wrapContentSize()
                                    .align(Alignment.CenterStart),
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

                            FloatingActionButton(
                                modifier = Modifier.wrapContentSize()
                                    .align(Alignment.CenterEnd),
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

                        if (uiState.abTestLayoutState.isAiFabBottomEnd) {
                            FloatingActionButton(
                                modifier = Modifier.wrapContentSize()
                                    .align(Alignment.CenterEnd),
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
                                    bottomSheetHeight
                                ).verticalScroll(rememberScrollState()),
                                icon = R.drawable.knutice_ai_logo,
                                header = stringResource(R.string.header_ai_summary),
                                attribution = stringResource(R.string.attribution_knutice_ai),
                                footer = stringResource(R.string.footer_ai_disclaimer),
                                containerColor = MaterialTheme.colorScheme.secondaryBackground,
                                primaryTextColor = MaterialTheme.colorScheme.title,
                                secondaryTextColor = MaterialTheme.colorScheme.subTitle,
                                isContentAvailable = !uiState.summarizedContent.isNullOrEmpty()
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