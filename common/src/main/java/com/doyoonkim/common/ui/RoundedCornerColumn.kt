package com.doyoonkim.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple

@Composable
fun SingleRoundedCornerItem(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    cornerRadius: Dp = 15.dp,
    backgroundColor: Color,
    primaryColor: Color,
    secondaryColor: Color,
    titleText: String,
    descriptionText: String?,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .background(Color.Transparent),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundedCornerColumn(
            modifier = modifier.fillMaxWidth().wrapContentHeight(),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            cornerRadius = cornerRadius,
            backgroundColor = backgroundColor
        ) {
            RoundedCornerColumnTextItemWithExtraOnRight(
                modifier = Modifier,
                verticalPadding = 15.dp,
                titleText = titleText,
                primaryColor = primaryColor,
                hasBottomDivider = false,
                extraOnRight = content
            )
        }

        descriptionText?.let {
            Text(
                text = it,
                color = secondaryColor,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 40.dp)
            )
        }
    }
}

@Composable
fun RoundedCornerColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    cornerRadius: Dp = 15.dp,
    backgroundColor: Color,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Surface(
        modifier = modifier
            .wrapContentSize()
            .background(Color.Transparent)
            .clip(RoundedCornerShape(cornerRadius)),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoundedCornerColumn_Preview() {
    SingleRoundedCornerItem(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        backgroundColor = MaterialTheme.colorScheme.secondaryBackground,
        primaryColor = MaterialTheme.colorScheme.title,
        secondaryColor = MaterialTheme.colorScheme.subTitle,
        titleText = "취업 공지",
        descriptionText = "채용 정보, 취업 지원 프로그램, 진로 상담 등 학생들의 진로 설계를 돕기 위한 소식을 알려드려요"
    ) {
        Switch(
            checked = true,
            colors = SwitchDefaults.colors().copy(
                checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                checkedThumbColor = Color.White
            ),
            onCheckedChange = {
                /* no-op */
            },
            enabled = true
        )
    }
}
