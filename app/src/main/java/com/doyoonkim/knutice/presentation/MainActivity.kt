package com.doyoonkim.knutice.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.doyoonkim.knutice.ui.theme.KNUTICETheme
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.ui.theme.notificationType1
import com.doyoonkim.knutice.ui.theme.title
import com.example.knutice.R

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KNUTICETheme {
                MainServiceScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainServiceScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.title,
                    containerColor = MaterialTheme.colorScheme.containerBackground
                ),
                actions = {
                    IconButton(
                        onClick = {  }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.baseline_settings_24),
                            contentDescription = "Settings",
                            modifier = Modifier.wrapContentSize(),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.title)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        CategorizedNotification(
            Modifier.padding(innerPadding)
        )
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
        color = MaterialTheme.colorScheme.notificationType1
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    KNUTICETheme {
        MainServiceScreen()
    }
}