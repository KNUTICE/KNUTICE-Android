package com.doyoonkim.main.campus.meal

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.ui.TopAppBarWithNavButton
import com.doyoonkim.main.BuildConfig
import com.doyoonkim.main.campus.components.LifecycleAwareWebView

@Composable
fun DiningMenuScreen(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit = {  }
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBarWithNavButton(
                titleText = "학식 조회"
            ) {
                // Top App Bar back button press
                onBackClicked()
            }
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) {contentPadding ->
        LifecycleAwareWebView(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            url = BuildConfig.KNUTICE_ORIGIN + BuildConfig.DINING_PATH,
            onLeaveWebView = {
                // System Back Button/Gesture Handling
                onBackClicked()
            }
        )
    }
}