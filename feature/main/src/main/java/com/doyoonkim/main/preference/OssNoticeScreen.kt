package com.doyoonkim.main.preference

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.doyoonkim.common.ui.TopAppBarWithNavButton
import com.doyoonkim.common.R
import com.doyoonkim.main.campus.components.LifecycleAwareWebView

@Composable
fun OssNoticeScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    BackHandler { onBackPressed() }

    Scaffold(
        topBar = {
            TopAppBarWithNavButton(
                titleText = stringResource(R.string.oss_notice),
                onBackPressed = onBackPressed
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        LifecycleAwareWebView(
            modifier = modifier.fillMaxSize().padding(innerPadding),
            dynamicThemeEnabled = false,
            url = "https://knutice.github.io/KNUTICE-OpenSourceLicense/Android/opensource.html"
        ) { onBackPressed() }
    }
}