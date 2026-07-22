package com.doyoonkim.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.theme.containerGray
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.variantPurple
import kotlinx.coroutines.launch

@Composable
fun HorizontalPagerWithTab(
    initialPage: Int,
    tabItems: List<String>,
    isTabDynamic: Boolean = false,
    onAddTabItemClicked: () -> Unit = { },
    tabContent: @Composable ((Int) -> Unit)
) {
    val coroutineScope = rememberCoroutineScope()

    var selected by remember { mutableIntStateOf(initialPage) }

    // Pager
    val pagerSize = tabItems.size
    val pagerState = rememberPagerState(
        initialPage = selected,
        pageCount = { pagerSize }
    )

    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.settledPage != selected) selected = pagerState.settledPage
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(tabItems) { i, e ->
                Box(
                    modifier = Modifier.wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(horizontal = 3.dp)
                            .background(Color.Transparent)
                            .clickable {
                                coroutineScope.launch {
                                    selected = i
                                    pagerState.animateScrollToPage(selected)
                                }
                            },
                        color = if (i == selected) {
                            MaterialTheme.colorScheme.variantPurple
                        } else {
                            MaterialTheme.colorScheme.secondaryBackground
                        },
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .wrapContentSize()
                                .fillMaxHeight()
                                .padding(vertical = 5.dp, horizontal = 15.dp),
                            text = e,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = if (selected == i) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                color = if (i == selected) {
                                    Color.White
                                } else {
                                    MaterialTheme.colorScheme.subTitle
                                }
                            )
                        )
                    }
                }
            }

            if (isTabDynamic) {
                item("New Tab Item Button") {
                    Box(
                        modifier = Modifier.wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(horizontal = 3.dp)
                                .background(Color.Transparent)
                                .clickable {
                                    onAddTabItemClicked()
                                },
                            color = MaterialTheme.colorScheme.containerGray,
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .fillMaxHeight()
                                    .padding(vertical = 5.dp, horizontal = 15.dp),
                                text = "+",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill,
            userScrollEnabled = true
        ) { pageNumber ->
            tabContent(pageNumber)
        }
    }
}