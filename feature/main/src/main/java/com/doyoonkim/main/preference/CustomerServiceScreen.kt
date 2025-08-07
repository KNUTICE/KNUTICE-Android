package com.doyoonkim.main.preference

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.main.viewmodel.CustomerServiceViewModel
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.TopAppBarWithBackButton
import com.doyoonkim.main.contract.CustomerServiceEvent
import com.doyoonkim.main.contract.CustomerServiceSideEffect

@Composable
fun CustomerServiceScreen(
    modifier: Modifier = Modifier,
    viewModel: CustomerServiceViewModel,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Version Information for Report Submission
    val versionInfo = stringResource(R.string.version_code)
    val adjustImePadding = Modifier.consumeWindowInsets(WindowInsets.ime).imePadding()
    val localFocusManager = LocalFocusManager.current

    BackHandler { viewModel.sendUiEvent(CustomerServiceEvent.GoBack) }

    LaunchedEffect(Unit) {
        viewModel.uiSideEffect.collect { effect ->
            when (effect) {
                is CustomerServiceSideEffect.NavToBack -> onBackPressed()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                titleText = stringResource(R.string.title_customer_service),
                onBackPressed = {
                    viewModel.sendUiEvent(CustomerServiceEvent.GoBack)
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        Box(
            modifier = modifier.fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { localFocusManager.clearFocus() }
                    )
                }
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.customer_service_subtitile_1),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.variantPurple,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.customer_service_subtitle_2),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.variantPurple,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier.fillMaxWidth().weight(5f)
                        .padding(top = 25.dp, bottom = 25.dp)
                        .then(adjustImePadding)
                ) {
                    TextField(
                        modifier = Modifier.fillMaxSize(),
                        value = uiState.userReport,
                        placeholder = { Text(stringResource(R.string.placeholder_customer_report)) },
                        enabled = !uiState.isSubmissionCompleted,
                        onValueChange = {
                            viewModel.sendUiEvent(CustomerServiceEvent.UpdateUserReport(it))
                        },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.title,
                            unfocusedTextColor = MaterialTheme.colorScheme.subTitle,
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryBackground,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryBackground,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(15.dp)
                    )

                    Text(
                        text = "${uiState.userReport.length}/500",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.subTitle,
                        modifier = Modifier.wrapContentSize()
                            .padding(15.dp)
                            .align(Alignment.BottomEnd)
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 3.dp, end = 3.dp)
                    ,
                    enabled = !uiState.isSubmissionCompleted && uiState.exceedMinCharacters,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.variantPurple,
                        contentColor = Color.White,
                    ),
                    onClick = {
                        viewModel.sendUiEvent(CustomerServiceEvent.SubmitUserReport(versionInfo))
                    }
                ) {
                    Text(
                        text = stringResource(R.string.btn_submit),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            if (uiState.isSubmissionCompleted) {
                Dialog(
                    onDismissRequest = { /* Do nothing on onDismiss */ }
                ) {
                    Surface(
                        modifier = Modifier.padding(15.dp)
                            .clip(RoundedCornerShape(15.dp)),
                        color = MaterialTheme.colorScheme.surfaceBright
                    ) {
                        Column(
                            modifier = Modifier.wrapContentHeight()
                                .padding(30.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                text = stringResource(
                                    if (!uiState.isSubmissionFailed) R.string.submission_completed_title
                                    else R.string.error_submission_unavailable
                                )
                            )
                            Text(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                text = stringResource(
                                    if (!uiState.isSubmissionFailed) R.string.submission_completed__subtitle
                                    else R.string.error_submission_unavailable_description
                                )
                            )
                            Button(
                                onClick = {
                                    viewModel.sendUiEvent(
                                        CustomerServiceEvent.ResetSubmissionStatus
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    text = stringResource(R.string.btn_confirm)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}