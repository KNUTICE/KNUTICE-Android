package com.doyoonkim.common.ui.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class MarkdownHeadingColor(
    val content: Color,
    val divider: Color
)

data class MarkdownTableColor(
    val header: Color,
    val content: Color
)

@Composable
fun MarkdownHeading(
    modifier: Modifier = Modifier,
    text: String,
    headingType: Int,
    color: MarkdownHeadingColor
) {
    Column(
        modifier = modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = color.content,
                fontWeight = FontWeight.ExtraBold,
                fontSize = (20 - (headingType * 2)).sp
            ),
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 5.dp)
        )
        Spacer(Modifier.height(5.dp))
        HorizontalDivider(thickness = 1.dp, color = color.divider)
    }
}

@Composable
fun MarkdownBulletPoint(
    modifier: Modifier = Modifier,
    text: String,
    level: Int,
    textColor: Color
) {
    // Run syntax process under Dispatchers.Default
    val processedContent : AnnotatedString by produceState(
        initialValue = AnnotatedString(text),
        key1 = text
    ) {
        // Syntax Processing
        val result = withContext(Dispatchers.Default) {
            getAnnotatedString(text)
        }

        // Update value and initiate re-rendering
        value = result
    }

    Text(
        text = buildAnnotatedString {
            repeat(level) {
                append("\t")
            }
            append("•  ")
        } + processedContent,
        style = TextStyle(
            color = textColor,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        modifier = modifier.fillMaxWidth()
            .wrapContentHeight()
    )
}

@Composable
fun MarkdownTable(
    modifier: Modifier = Modifier,
    tableHeader: List<String>,
    tableRows: List<List<String>>,
    color: MarkdownTableColor,
) {
    // Why?
    Column(
        modifier = modifier
    ) {
        // Header
        Row(
            modifier = Modifier.background(color.header),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tableHeader.forEach { h ->
                // Run syntax process under Dispatchers.Default
                val processedContent : AnnotatedString by produceState(
                    initialValue = AnnotatedString(h),
                    key1 = h
                ) {
                    // Syntax Processing
                    val result = withContext(Dispatchers.Default) {
                        getAnnotatedString(h)
                    }

                    // Update value and initiate re-rendering
                    value = result
                }

                Text(
                    text = processedContent,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = color.content
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        tableRows.forEach { row ->
            Row(
                modifier = Modifier.background(Color.Transparent)
                    .height(intrinsicSize = IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { c ->
                    // Run syntax process under Dispatchers.Default
                    val processedContent : AnnotatedString by produceState(
                        initialValue = AnnotatedString(c),
                        key1 = c
                    ) {
                        // Syntax Processing
                        val result = withContext(Dispatchers.Default) {
                            getAnnotatedString(c)
                        }

                        // Update value and initiate re-rendering
                        value = result
                    }

                    Text(
                        text = processedContent,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = color.content
                        ),
                        modifier = Modifier.weight(1f)
                    )

                }
            }
        }
    }
}

@Composable
fun MarkdownBody(
    modifier: Modifier = Modifier,
    text: String,
    color: Color
) {
    // Run syntax process under Dispatchers.Default
    val processedContent : AnnotatedString by produceState(
        initialValue = AnnotatedString(text),
        key1 = text
    ) {
        // Syntax Processing
        val result = withContext(Dispatchers.Default) {
            getAnnotatedString(text)
        }

        // Update value and initiate re-rendering
        value = result
    }

    Text(
        text = processedContent,
        style = TextStyle(
            color = color,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 22.sp
        ),
        modifier = modifier
    )
}

private fun getAnnotatedString(text: String) =
    buildAnnotatedString {
        syntaxProcess(text, this)
    }

private fun syntaxProcess(text: String, builder: AnnotatedString.Builder) {
    // Base Condition
    if (text.isEmpty()) return

    // Regex
    val boldResult = Regex("\\*\\*(.*?)\\*\\*").find(text)
    val italicResult = Regex("\\_(.*?)\\_").find(text)
    val hyperlinkResult = Regex("\\[([^\\]]+)\\]\\(([^)]+)\\)").find(text)

    // Extract very first result
    // make above result into single list, and take the minimum value using range.first.
    val result = listOfNotNull(boldResult, italicResult, hyperlinkResult).minByOrNull {
        it.range.first
    }

    if (result != null) {
        // At least one match result found.
        // Process text before matches
        builder.append(text.substring(0, result.range.first))

        // Process Matches. Invoke recursive call to process compound syntax.
        when(result) {
            boldResult -> {
                val content = result.groupValues[1]
                builder.withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    // Recursive call to process compound Syntax
                    syntaxProcess(content, this)
                }
            }
            italicResult -> {
                val content = result.groupValues[1]
                builder.withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                    // Recursive call to process compound Syntax
                    syntaxProcess(content, this)
                }
            }
            hyperlinkResult -> {
                val tag = result.groupValues[1]
                val link = result.groupValues[2]

                builder.withLink(
                    link = LinkAnnotation.Url(
                        url = link,
                        styles = TextLinkStyles(
                            SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    )
                ) {
                    // Recursive call to process compound Syntax
                    syntaxProcess(tag, this)
                }
            }
        }

        if (result.range.last + 1 < text.length) {
            // process remaining text.
            // Recursive call to check matches in the remaining text.
            syntaxProcess(text.substring(result.range.last + 1), builder)
        }

    } else {
        // No matches found. Process Plain Text (Leaf Node)
        builder.append(text)
    }
}