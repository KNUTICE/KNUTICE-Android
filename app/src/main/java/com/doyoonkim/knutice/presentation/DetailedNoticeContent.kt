package com.doyoonkim.knutice.presentation

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.doyoonkim.knutice.ui.theme.buttonContainer
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.presentation.component.LazyText
import com.doyoonkim.knutice.ui.theme.displayBackground
import com.doyoonkim.knutice.viewModel.DetailedNoticeContentViewModel

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
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    //Enable Javascript
                    // Security Alert: XSS Vulnerability
                    settings.javaScriptEnabled = true

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            val theme = context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
                            when (theme) {
                                Configuration.UI_MODE_NIGHT_YES -> {
                                    evaluateJavascript(
                                        """
                                           var themeStyle = 'div, p, span, ul { background-color: #262729 !important; color: #ffffff !important; }  .bbs_detail { border-top: 0px; } .bbs_detail_tit, .info { background-color: #333437 !important; color: #ffffff !important; border-radius: 15px; border-bottom: 0px; } .bbs_detail_tit h2 {color: #ffffff !important } .bbs_detail_tit .info li { color: #ffffff !important } .bbs_detail span { color: #ffffff !important } .bbs_detail_file { background-color: #787879 !important; color: #ffffff !important; border-radius: 15px; margin-top: 10px; padding: 15px; } .bbs_detail_file a { color: #ffffff; }',
                                               head = document.head || document.getElementsByTagName('head')[0],
                                               style = document.createElement('style');
                                                
                                               head.appendChild(style);
                                               style.type = 'text/css';
                                               if (style.styleSheet) {
                                                   style.styleSheet.cssText = themeStyle;
                                               } else {
                                                   style.appendChild(document.createTextNode(themeStyle));
                                               }
                                        """.trimIndent()
                                    ) {
                                        Log.d("DetailedNoticeContent", "Dark Theme Applied")
                                    }
                                }
                            }

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
                                div_point.remove();
                                div_footer.remove();
                                div_footer_root.remove();
                                
                                section_svisual.remove();
                                section_location.remove();
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

                    visibility = View.INVISIBLE
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    loadUrl(state.url)
                }
            }
        )
    }
}



@Preview
@Composable
fun DetailedNoticeContent_Preview() {
}