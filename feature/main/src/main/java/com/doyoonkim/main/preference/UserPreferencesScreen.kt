package com.doyoonkim.main.preference

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.buttonOnBackground
import com.doyoonkim.common.theme.containerGray
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.CircleGoButton
import com.doyoonkim.common.ui.RoundedCornerColumn
import com.doyoonkim.common.ui.RoundedCornerColumnTextItem
import com.doyoonkim.common.ui.RoundedCornerColumnTextItemWithExtraOnRight
import com.doyoonkim.common.ui.TopAppBarWithBackButton
import com.doyoonkim.main.viewmodel.SettingsViewModel

@Composable
fun UserPreferenceScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    onNotificationPreferenceClicked: () -> Unit,
    onCustomerServiceClicked: () -> Unit,
    onOssClicked: () -> Unit,
    onBackPressed: (Boolean) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler { onBackPressed(uiState.databaseSyncResult.completed) }

    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                titleText = stringResource(R.string.title_preference),
                onBackPressed = { onBackPressed(uiState.databaseSyncResult.completed) }
            )
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = modifier.fillMaxWidth()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RoundedCornerColumn(
                    backgroundColor = MaterialTheme.colorScheme.secondaryBackground
                ) {
                    RoundedCornerColumnTextItemWithExtraOnRight(
                        verticalPadding = 10.dp,
                        titleText = stringResource(R.string.enable_notification_title),
                        subTitleText = null,
                        primaryColor = MaterialTheme.colorScheme.title,
                        secondaryColor = MaterialTheme.colorScheme.subTitle,
                        hasBottomDivider = false
                    ) {
                        CircleGoButton(
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.buttonOnBackground,
                            contentColor = MaterialTheme.colorScheme.subTitle,
                            onClick = onNotificationPreferenceClicked
                        )
                    }
                }

                RoundedCornerColumn(
                    backgroundColor = MaterialTheme.colorScheme.secondaryBackground
                ) {
                    RoundedCornerColumnTextItemWithExtraOnRight(
                        verticalPadding = 10.dp,
                        titleText = stringResource(R.string.title_support),
                        subTitleText = null,
                        primaryColor = MaterialTheme.colorScheme.title,
                        secondaryColor = MaterialTheme.colorScheme.subTitle,
                        hasBottomDivider = false
                    ) {
                        CircleGoButton(
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.buttonOnBackground,
                            contentColor = MaterialTheme.colorScheme.subTitle,
                            onClick = onCustomerServiceClicked
                        )
                    }
                }

                RoundedCornerColumn(
                    backgroundColor = MaterialTheme.colorScheme.secondaryBackground
                ) {
                    RoundedCornerColumnTextItem(
                        verticalPadding = 12.dp,
                        titleText = stringResource(R.string.about_version),
                        subTitleText = stringResource(R.string.version_code),
                        primaryColor = MaterialTheme.colorScheme.title,
                        secondaryColor = MaterialTheme.colorScheme.subTitle,
                        hasBottomDivider = true
                    )

                    RoundedCornerColumnTextItemWithExtraOnRight(
                        verticalPadding = 10.dp,
                        titleText = stringResource(R.string.about_oss),
                        subTitleText = null,
                        primaryColor = MaterialTheme.colorScheme.title,
                        secondaryColor = MaterialTheme.colorScheme.subTitle,
                        hasBottomDivider = true
                    ) {
                        CircleGoButton(
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.buttonOnBackground,
                            contentColor = MaterialTheme.colorScheme.subTitle,
                            onClick = onOssClicked
                        )
                    }

                    RoundedCornerColumnTextItemWithExtraOnRight(
                        verticalPadding = 10.dp,
                        titleText = stringResource(R.string.text_manual_sync),
                        subTitleText = stringResource(R.string.text_manual_sync_description),
                        primaryColor = MaterialTheme.colorScheme.title,
                        secondaryColor = MaterialTheme.colorScheme.subTitle,
                        hasBottomDivider = false
                    ) {
                        IconButton(
                            onClick = { viewModel.requestManualDatabaseSync() },
                            modifier = modifier
                                .wrapContentSize()
                                .clip(CircleShape),
                            colors = IconButtonDefaults.iconButtonColors().copy(
                                containerColor = MaterialTheme.colorScheme.buttonOnBackground,
                                contentColor = MaterialTheme.colorScheme.subTitle
                            )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_sync_24),
                                contentDescription = "Go"
                            )
                        }
                    }
                }
            }

            if (uiState.isSyncDialogVisible) {
                Dialog(
                    onDismissRequest = {  },
                    properties = DialogProperties(
                        dismissOnBackPress = false, dismissOnClickOutside = false
                    )
                ) {
                    Surface(
                        modifier = Modifier.wrapContentSize()
                            .background(Color.Transparent),
                        color = MaterialTheme.colorScheme.onAnyBackground,
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .padding(20.dp)
                                .wrapContentHeight(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.text_manual_sync),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.title
                            )

                            Text(
                                text = if(uiState.databaseSyncResult.completed) {
                                    stringResource(R.string.text_sync_result) + " ${uiState.databaseSyncResult.targetCounts} / ${uiState.databaseSyncResult.failureCounts}"
                                } else { stringResource(R.string.text_sync_in_progress) },
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.subTitle
                            )

                            if (uiState.isSyncRequested) {
                                LinearProgressIndicator(
                                    modifier = Modifier.padding(20.dp),
                                    color = MaterialTheme.colorScheme.variantPurple
                                )
                            }

                            TextButton(
                                enabled = !uiState.isSyncRequested,
                                onClick = { viewModel.dismissSyncDialog() },
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                colors = ButtonDefaults.buttonColors().copy(
                                    containerColor = MaterialTheme.colorScheme.variantPurple,
                                    contentColor = MaterialTheme.colorScheme.title,
                                    disabledContentColor = MaterialTheme.colorScheme.containerGray,
                                    disabledContainerColor = MaterialTheme.colorScheme.subTitle
                                )
                            ) {
                                Text(
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserPreferencesScreen_Preview() {
}