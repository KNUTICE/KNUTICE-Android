package com.doyoonkim.main.preference

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.common.MajorResources
import com.doyoonkim.common.NoticeResources
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.containerGray
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.NavButtonType
import com.doyoonkim.common.ui.RectangleImageButton
import com.doyoonkim.common.ui.TopAppBarWithNavButton
import com.doyoonkim.main.contract.WidgetConfigEvent
import com.doyoonkim.main.contract.WidgetConfigSideEffect
import com.doyoonkim.main.viewmodel.WidgetConfigViewModel
import com.doyoonkim.model.WidgetCategoryPolicy
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetPreferencesScreen(
    modifier: Modifier = Modifier,
    viewModel: WidgetConfigViewModel,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Confirm Snack Bar State
    val snackBarState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sendUiEvent(WidgetConfigEvent.FetchStatus)
        viewModel.uiSideEffect.collect { sideEffect ->
            when (sideEffect) {
                is WidgetConfigSideEffect.CloseSettings -> onBackClicked()
                is WidgetConfigSideEffect.ShowProcessedSnackBark -> {
                    launch {
                        snackBarState.showSnackbar(
                            context.getString(R.string.notice_widget_set_confirm),
                            actionLabel = context.getString(R.string.btn_confirm),
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBarWithNavButton(
                titleText = stringResource(R.string.notice_widget_config_title),
                navButtonType = NavButtonType.CLOSE
            ) {
                viewModel.sendUiEvent(WidgetConfigEvent.Exit)
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarState
            ) { data ->
                Snackbar(
                    modifier = Modifier.padding(bottom = 80.dp),
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.secondaryBackground,
                    contentColor = MaterialTheme.colorScheme.title,
                    actionColor = MaterialTheme.colorScheme.variantPurple,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { contentPadding ->
        Box(
            modifier = modifier.padding(contentPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = modifier
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = stringResource(R.string.notice_widget_config_header),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.title
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }

                // Default Options (Main Notice Categories)
                items(uiState.defaultCategories) {
                    val categoryText = NoticeResources.getStringResourcesByCategory(it)
                    val icon = NoticeResources.getDrawableResourceByCategory(it)
                    val itemPolicy = WidgetCategoryPolicy.Main(it)

                    RectangleImageButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(categoryText),
                        icon = painterResource(icon),
                        isSelected = uiState.selectedCategoryPolicy == itemPolicy,
                        iconSize = 48.dp,
                        textColor = MaterialTheme.colorScheme.title,
                        containerColor = MaterialTheme.colorScheme.secondaryBackground,
                        border = MaterialTheme.colorScheme.onAnyBackground,
                        borderHighlighted = MaterialTheme.colorScheme.variantPurple
                    ) {
                        viewModel.sendUiEvent(WidgetConfigEvent.SelectPolicy(itemPolicy))
                    }
                }

                // Major Category Selection (Conditional)
                val majorCategoryAvailable = uiState.majorSubscribed
                if (majorCategoryAvailable != null) {
                    item {
                        val categoryText = MajorResources.getLocalizedString(majorCategoryAvailable)
                        val icon = MajorResources.getDrawable()
                        val itemPolicy = WidgetCategoryPolicy.Major

                        RectangleImageButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(categoryText),
                            icon = painterResource(icon),
                            isSelected = uiState.selectedCategoryPolicy == itemPolicy,
                            iconSize = 48.dp,
                            textColor = MaterialTheme.colorScheme.title,
                            containerColor = MaterialTheme.colorScheme.secondaryBackground,
                            border = MaterialTheme.colorScheme.onAnyBackground,
                            borderHighlighted = MaterialTheme.colorScheme.variantPurple
                        ) {
                            viewModel.sendUiEvent(WidgetConfigEvent.SelectPolicy(itemPolicy))
                        }
                    }
                }

                // Spacer (prevent Apply Button to overlay the content
                item { Spacer(Modifier.height(80.dp)) }
            }

            Button(
                onClick = { viewModel.sendUiEvent(WidgetConfigEvent.SaveSelection) },
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
                    .align(Alignment.BottomCenter),
                enabled = uiState.selectedCategoryPolicy != WidgetCategoryPolicy.Unconfigured,
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.variantPurple,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.containerGray,
                    disabledContentColor = MaterialTheme.colorScheme.onAnyBackground
                ),
                contentPadding = PaddingValues(5.dp)
            ) {
                Text(
                    text = stringResource(R.string.notice_widget_config_apply_button),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    ),
                    modifier = Modifier.wrapContentSize()
                        .padding(12.dp)
                )
            }

            // Circular Progress Indicator.
            if (uiState.isProcessing) {
                Dialog(
                    onDismissRequest = { },
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false,
                        usePlatformDefaultWidth = false
                    )
                ) {
                    Surface(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Transparent),
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
