package com.doyoonkim.common.ui.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.model.MarkdownString

@Composable
fun MarkdownView(
    modifier: Modifier = Modifier,
    nodes: List<MarkdownString>,
    containerColor: Color = Color.Transparent,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    highlightColor: Color
) {
    Column(
        modifier = modifier
            .background(containerColor),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        nodes.forEach { node ->
            when (node) {
                is MarkdownString.Heading -> {
                    MarkdownHeading(
                        modifier = Modifier.padding(vertical = 5.dp),
                        text = node.text,
                        headingType = node.weight,
                        color = MarkdownHeadingColor(
                            content = highlightColor,
                            divider = secondaryTextColor
                        )
                    )
                }
                is MarkdownString.BulletPoint -> {
                    MarkdownBulletPoint(
                        modifier = Modifier
                            .padding(start = 2.dp, top = 3.dp, bottom = 3.dp),
                        text = node.text,
                        level = node.level,
                        textColor = primaryTextColor
                    )
                }
                is MarkdownString.Table -> {
                    MarkdownTable(
                        modifier = Modifier.fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(16.dp)),
                        tableHeader = node.header,
                        tableRows = node.contents,
                        color = MarkdownTableColor(
                            header = highlightColor,
                            content = primaryTextColor
                        )
                    )
                }
                is MarkdownString.Divider -> {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 5.dp),
                        thickness = 1.dp,
                        color = secondaryTextColor
                    )
                }
                is MarkdownString.Body -> {
                    MarkdownBody(
                        modifier = modifier.fillMaxWidth()
                            .wrapContentHeight()
                            .padding(vertical = 8.dp),
                        text = node.text,
                        color = primaryTextColor
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MarkdownView_Preview() {
    val testNodes = listOf(
        MarkdownString.Heading(1, text = "Hello"),
        MarkdownString.Heading(2, text = "Hello"),
        MarkdownString.Heading(3, text = "hello"),
        MarkdownString.BulletPoint(text = "**line 1**", level = 0),
        MarkdownString.BulletPoint(text = "line 1- 1", level = 1),
        MarkdownString.BulletPoint(text = "**line 2**", level = 0),
        MarkdownString.BulletPoint(text = "line 2 - 1", level = 1),
        MarkdownString.Table(header = listOf("Header 1", "Header 2"), contents = listOf(listOf("**Random**", "_Random_"), listOf("**_[naver](https://www.naver.com)_**", "String"))),
        MarkdownString.Body(text = "Some Random String\nSome **Random** String\n**_[naver](https://www.naver.com)_** and ")
    )


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        MarkdownView(
            modifier = Modifier.padding(vertical = 15.dp, horizontal = 10.dp),
            nodes = testNodes,
            containerColor = MaterialTheme.colorScheme.secondaryBackground,
            primaryTextColor = MaterialTheme.colorScheme.title,
            secondaryTextColor = MaterialTheme.colorScheme.subTitle,
            highlightColor = MaterialTheme.colorScheme.variantPurple,
        )
    }
}