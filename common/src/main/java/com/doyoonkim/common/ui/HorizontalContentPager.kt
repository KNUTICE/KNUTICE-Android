package com.doyoonkim.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.doyoonkim.common.theme.containerGray
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.variantPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MAX_PAGE_MULTIPLIER = 1000

@Composable
fun HorizontalContentPager(
    modifier: Modifier = Modifier,
    startingPage: Int,
    size: Int,
    progressDelay: Long = 5000L,
    pagerContent: @Composable (Int) -> Unit
) {
    // Allow user to reverse scroll
    // Prevent potential OutOfBounds due to Process Death. (Potential Size Shrink)
    val size = size.coerceAtLeast(1)
    val maxPage = remember(size) { size * MAX_PAGE_MULTIPLIER }
    val initialPage = startingPage + (maxPage / 2)

    // Pager State
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { maxPage.coerceAtLeast(1) }
    )

    val pagerInteractionSource = remember { MutableInteractionSource() }
    val isPagePressed by pagerInteractionSource.collectIsPressedAsState()
    val isPageDragged by pagerInteractionSource.collectIsDraggedAsState()
    val lifecycle = LocalLifecycleOwner.current

    // Auto-Advancing
    if (!isPagePressed && !isPageDragged && size > 1) {
        LaunchedEffect(pagerState.settledPage, lifecycle) {
            // Prevent potential resource draining when app is in background.
            // Cancel related coroutine when App enters onStop status.
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                delay(progressDelay)
                with(pagerState.currentPage) {
                    if (this < maxPage) {
                        pagerState.animateScrollToPage(this + 1)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = modifier.wrapContentSize(),
            pageSize = PageSize.Fill,
            beyondViewportPageCount = 2,
            userScrollEnabled = true
        ) { index ->
            pagerContent(index % size)
        }
        HorizontalPagerIndicator(
            total = size,
            indicatorSize = 10.dp,
            selectedProvider = {
                pagerState.currentPage % size
            }
        ) {
            val diff = it - (pagerState.currentPage % size)
            with(pagerState) {
                val target = currentPage + diff
                if (target in 0..maxPage && !isScrollInProgress) {
                    animateScrollToPage(target)
                }
            }
        }
    }
}

// Indicator
@Composable
private fun HorizontalPagerIndicator(
    total: Int,
    selectedProvider: () -> Int,
    indicatorSize: Dp = 20.dp,
    onIndicatorSelected: suspend (Int) -> Unit
) {
    // Local Coroutine Scope
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier.wrapContentWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.secondaryBackground,
                RoundedCornerShape(20.dp)
            )
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(indicatorSize * 3)
    ) {
        // Color
        val colorSelected = MaterialTheme.colorScheme.variantPurple
        val colorDefault = MaterialTheme.colorScheme.containerGray

        // Circle Based Indicator
        repeat(total) { idx ->
            Box(
                modifier = Modifier.size(indicatorSize)
                    .clickable {
                        scope.launch {
                            // UI Related Task (rememberCoroutineScope inherits Dispatchers.Main.immediate)
                            onIndicatorSelected(idx)
                        }
                    }
                    .drawWithCache {
                        onDrawBehind {
                            // Color Determination
                            val color = if (idx == selectedProvider()) colorSelected else colorDefault
                            drawCircle(
                                color = color,
                                radius = size.width / 2f,
                                center = Offset(size.width / 2f, size.height / 2f)
                            )
                        }
                    }
            )
        }
    }
}
