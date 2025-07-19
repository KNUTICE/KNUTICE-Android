package com.doyoonkim.main.splash

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.main.viewmodel.SplashStage
import com.doyoonkim.main.viewmodel.SplashViewModel
import com.doyoonkim.main.viewmodel.SyncStatus

@Composable
fun KnuticeSplashScreen(
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel,
    onPreProcessCompleted: (Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.startPreprocess()
    }

    LaunchedEffect(uiState.splashStage) {
        Log.d("SplashScreen", "Splash Stage: ${uiState.splashStage}")
        when (uiState.splashStage) {
            SplashStage.LOADING -> {

            }
            SplashStage.DISMISS -> {
                onPreProcessCompleted(true)
            }
            SplashStage.DISMISS_WITH_ERROR -> {
                onPreProcessCompleted(false)
            }
        }
    }

    if(uiState.splashStage == SplashStage.LOADING) {
        Column(
            modifier = modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.displayBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.wrapContentSize().weight(6f),
                verticalArrangement = Arrangement.spacedBy(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.knutice_icon_splash),
                    contentDescription = "App Icon",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(128.dp)
                )

                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.subTitle,
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentHeight()
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth().weight(5f, fill = false)
            ) {
                if (uiState.syncStatus == SyncStatus.PROCESSING) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.text_sync_in_progress),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.title,
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentHeight()
                        )

                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            color = MaterialTheme.colorScheme.variantPurple
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier.wrapContentSize()
                            .background(Color.Transparent)
                            .align(Alignment.Center),
                        shape = RoundedCornerShape(15.dp),
                        color = MaterialTheme.colorScheme.onAnyBackground
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(15.dp),
                            color = MaterialTheme.colorScheme.variantPurple,
                            trackColor = MaterialTheme.colorScheme.onAnyBackground
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KnuticeSplashScreen_Preview() {

}