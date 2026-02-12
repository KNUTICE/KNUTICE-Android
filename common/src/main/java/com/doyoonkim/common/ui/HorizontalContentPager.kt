package com.doyoonkim.common.ui

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun HorizontalContentPager(
    modifier: Modifier = Modifier,
    startingPage: Int,
    size: Int,
    progressDelay: Long = 5000L,
    pagerContent: @Composable (Int) -> Unit
) {
    // Allow user to reverse scroll
    val maxPage = 1000 * size
    val initialPage = startingPage + (maxPage / 2)

    // Pager State
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { maxPage }
    )

    val pagerInteractionSource = remember { MutableInteractionSource() }
    val isPagePressed by pagerInteractionSource.collectIsPressedAsState()

    // Auto-Advancing
    if (!isPagePressed && size > 1) {
        LaunchedEffect(pagerState, pagerInteractionSource) {
            while (true) {
                delay(progressDelay)
                with(pagerState.currentPage) {
                    if (this < maxPage) {
                        Log.d("HorizontalPager", "Current Statue: ${this}")
                        pagerState.animateScrollToPage(this + 1)
                    }
                }
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.wrapContentSize(),
        pageSize = PageSize.Fill,
        beyondViewportPageCount = 2,
        userScrollEnabled = true
    ) { index ->
        pagerContent(index)
    }
}