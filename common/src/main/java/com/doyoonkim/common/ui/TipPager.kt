package com.doyoonkim.common.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.model.TipVO
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun TipPager(
    modifier: Modifier,
    tips: List<TipVO> = emptyList(),
    onTipClicked: (String) -> Unit
) {
    // Pager State
    val pagerState = rememberPagerState(pageCount = { Int.MAX_VALUE })

    val pageInteractionSource = remember { MutableInteractionSource() }
    val isPagePressed by pageInteractionSource.collectIsPressedAsState()

    // Auto-Advancing
    val isAutoAdvancing = !isPagePressed && tips.size > 1
    if (isAutoAdvancing) {
        LaunchedEffect(pagerState, pageInteractionSource) {
            while (true) {
                delay(5000L)
                with(pagerState.currentPage) {
                    if (this < Int.MAX_VALUE) {
                        pagerState.animateScrollToPage(this + 1)
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
            .wrapContentHeight()
            .background(Color.Transparent)
            .clickable {
                onTipClicked(
                    tips[pagerState.currentPage % tips.size].url
                )
            },
        color = MaterialTheme.colorScheme.onAnyBackground,
        shape = RoundedCornerShape(15.dp)
    ) {
        // Vertical Pager
        VerticalPager(
            modifier = modifier,
            pageSize = PageSize.Fixed(50.dp),
            userScrollEnabled = false,
            state = pagerState
        ) { index ->
            Log.d("TipPager", "Current Page: $index State Status: ${pagerState.currentPage}")
            Log.d("TipPager", "INDEX?: ${index % pagerState.pageCount}")
            TipContainer(
                modifier = Modifier.fillMaxWidth().height(50.dp)
                    .graphicsLayer {
                        val pageOffset = (
                                (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
                                ).absoluteValue

                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    },
                tipCategory = TipCategory.UPDATES,
                tipText = tips[index % tips.size].title
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TipPager_Preview() {

}