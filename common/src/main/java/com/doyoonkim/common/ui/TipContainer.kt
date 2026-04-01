package com.doyoonkim.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.title

enum class TipCategory { UPDATES, SYS_NOTICE, GENERAL_TIP }

@Composable
fun TipContainer(
    modifier: Modifier = Modifier,
    tipCategory: TipCategory = TipCategory.UPDATES,
    tipText: String = ""
) {
    val tagTitle = when(tipCategory) {
        TipCategory.GENERAL_TIP -> stringResource(R.string.text_tip_general)
        TipCategory.UPDATES -> stringResource(R.string.text_tip_updates)
        TipCategory.SYS_NOTICE -> stringResource(R.string.text_tip_sys_notice)
    }

    Row(
        modifier = modifier.padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            modifier = Modifier.wrapContentSize(),
            text = tipText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TipContainer_Preview() {
    TipContainer(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        tipCategory = TipCategory.SYS_NOTICE,
        tipText = "1.4.2 업데이트 이후 푸시 알림이 표출되지 않는 문제"
    )
}