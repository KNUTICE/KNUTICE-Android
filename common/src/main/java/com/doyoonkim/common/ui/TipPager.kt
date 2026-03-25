package com.doyoonkim.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.model.TipVO
import kotlinx.coroutines.delay

@Composable
fun TipPager(
    modifier: Modifier,
    tips: List<TipVO> = emptyList(),
    isLoading: Boolean = false,
    onTipClicked: (String) -> Unit
) {
    // Pager State
    val pagerState = rememberPagerState(pageCount = { Int.MAX_VALUE })

    val pageInteractionSource = remember { MutableInteractionSource() }
    val isPagePressed by pageInteractionSource.collectIsPressedAsState()
    val lifecycle = LocalLifecycleOwner.current

    // Auto-Advancing
    val isAutoAdvancing = !isPagePressed && tips.size > 1
    if (isAutoAdvancing) {
        LaunchedEffect(pagerState.settledPage, lifecycle ) {
            // Prevent potential resource draining under Auto-Advancing implementation
            // when app is in background.
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                delay(5000L)
                with(pagerState.currentPage) {
                    if (this < Int.MAX_VALUE) {
                        pagerState.animateScrollToPage(this + 1)
                    }
                }
            }
        }
    }

    // Either loading or tip is not empty, Pager becomes visible.
    // Loading -> Placeholder (Skeleton Animation)
    // Tip is not empty -> Show tips.
    if (isLoading || tips.isNotEmpty()) {
        Surface(
            modifier = modifier
                .background(Color.Transparent)
                .clickable(
                    enabled = !isLoading && tips.isNotEmpty()
                ) {
                    onTipClicked(
                        tips[pagerState.currentPage % tips.size].url
                    )
                },
            color = MaterialTheme.colorScheme.secondaryBackground,
            shape = RoundedCornerShape(15.dp)
        ) {
            if (isLoading) {
                AnimatedGradient(
                    modifier = Modifier.height(30.dp)
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                )
            } else {
                // Vertical Pager
                VerticalPager(
                    modifier = Modifier.height(50.dp),
                    pageSize = PageSize.Fill,
                    userScrollEnabled = false,
                    state = pagerState
                ) { index ->
                    TipContainer(
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        tipCategory = TipCategory.UPDATES,
                        tipText = tips[index % tips.size].title
                    )
                }
            }
        }
    }
}
