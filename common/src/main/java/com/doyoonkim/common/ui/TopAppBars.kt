package com.doyoonkim.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.title

/**
 * @author kimdoyoon
 * Created 6/23/25 at 11:38 PM
 */

enum class NavButtonType { BACK, CLOSE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithNavButton(
    modifier: Modifier = Modifier,
    titleText: String,
    navButtonType: NavButtonType = NavButtonType.BACK,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = titleText,
                textAlign = TextAlign.Left,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { onBackPressed() }
            ) {
                Icon(
                    imageVector = when(navButtonType) {
                      NavButtonType.BACK -> Icons.AutoMirrored.Filled.KeyboardArrowLeft
                      NavButtonType.CLOSE -> Icons.Filled.Clear
                    },
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.displayBackground,
            titleContentColor = MaterialTheme.colorScheme.title,
            navigationIconContentColor = MaterialTheme.colorScheme.title
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithActions(
    modifier: Modifier = Modifier,
    titleText: String,
    actions: @Composable (RowScope.() -> Unit)
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = titleText,
                    textAlign = TextAlign.Left,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W900,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.title
                )
            }
        },
        actions = {
            actions()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.displayBackground,
            titleContentColor = MaterialTheme.colorScheme.title
        )
    )
}