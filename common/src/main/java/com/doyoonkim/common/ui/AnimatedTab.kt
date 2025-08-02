package com.doyoonkim.common.ui

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.title

@Composable
fun AnimatedTab(
    modifier: Modifier = Modifier,
    tabTitles: List<String> = listOf("tab"),
    initialTabIndex: Int = 0,
    containerColor: Color,
    titleColor: Color,
    colorOnSelect: Color,
    tab: @Composable (Int) -> Unit
) {
    var tabSize by remember { mutableStateOf(IntSize.Zero) }
    var selected by remember { mutableIntStateOf(initialTabIndex) }

    // Animation Value
    val animationValue by animateFloatAsState(
        targetValue = ((tabSize.width / tabTitles.size) * selected).toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "Tab Animation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Surface(
            modifier = Modifier.wrapContentSize()
                .background(Color.Transparent)
                .padding(bottom = 10.dp),
            color = containerColor,
            shape = RoundedCornerShape(10.dp)
        ) {
            Box(
                modifier = Modifier.wrapContentSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                // Highlight Indicator
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(3.dp)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .offset(x = with(LocalDensity.current) { animationValue.toDp() })
                            .size(
                                width = with(LocalDensity.current) {
                                    (tabSize.width / tabTitles.size).toDp()
                                },
                                height = with(LocalDensity.current) {
                                    tabSize.height.toDp()
                                }
                            )
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(colorOnSelect)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Transparent)
                        .onGloballyPositioned { coordinates ->
                            tabSize = coordinates.size
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabTitles.forEachIndexed { index, value ->
                        Text(
                            text = value,
                            color = titleColor,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentHeight()
                                .weight(1f)
                                .padding(vertical = 5.dp)
                                .clickable(
                                    interactionSource = null,
                                    indication = null
                                ) {
                                    selected = index
                                }
                        )
                    }
                }
            }
        }
        tab(selected)
    }

}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun AnimatedTab_Preview() {
    val dummyTabs = listOf("Notices", "Bookmarks", "Others")

    AnimatedTab(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        tabTitles = dummyTabs,
        initialTabIndex = 0,
        containerColor = MaterialTheme.colorScheme.secondaryBackground,
        titleColor = MaterialTheme.colorScheme.title,
        colorOnSelect = MaterialTheme.colorScheme.onAnyBackground
    ) { }
}