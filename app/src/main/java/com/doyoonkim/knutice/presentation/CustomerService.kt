package com.doyoonkim.knutice.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.ui.theme.subTitle
import com.doyoonkim.knutice.ui.theme.textPurple
import com.doyoonkim.knutice.ui.theme.title
import com.doyoonkim.knutice.viewModel.CustomerServiceViewModel

@Composable
fun CustomerService(
   modifier: Modifier = Modifier,
   viewModel: CustomerServiceViewModel = hiltViewModel(),
   onSubmitRequested: (String) -> Unit = {},
   onCloseRequested: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.customer_service_subtitile_1),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.textPurple,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.customer_service_subtitle_2),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.textPurple,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )


        Box(
            modifier = Modifier.fillMaxWidth().weight(5f)
                .padding(top = 25.dp, bottom = 25.dp)
        ) {
            TextField(
                modifier = Modifier.fillMaxSize(),
                value = uiState.userReport,
                placeholder = { Text(stringResource(R.string.placeholder_customer_report)) },
                onValueChange = {
                    viewModel.updateUserReportContent(it)
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.title,
                    unfocusedTextColor = MaterialTheme.colorScheme.subTitle,
                    focusedContainerColor = MaterialTheme.colorScheme.containerBackground,
                    unfocusedContainerColor = MaterialTheme.colorScheme.containerBackground,
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
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            enabled = true,
            shape = RoundedCornerShape(10.dp),
            onClick = { viewModel.submitUserReport() }
        ) {
            Text(
                text = stringResource(R.string.btn_submit),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true, locale = "ko-rKR",)
fun CustomerService_Preview() {
    CustomerService(Modifier.padding(10.dp))
}