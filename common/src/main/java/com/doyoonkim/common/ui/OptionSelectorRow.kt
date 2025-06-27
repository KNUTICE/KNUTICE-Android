package com.doyoonkim.common.ui

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun OptionSelectorRow(
    modifier: Modifier = Modifier,
    items: List<Pair<ImageVector, String>>
) {
    LazyRow(
        modifier = modifier.wrapContentWidth()
    ) {

    }
}