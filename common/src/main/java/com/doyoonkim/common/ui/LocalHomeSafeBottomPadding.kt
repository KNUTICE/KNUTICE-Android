package com.doyoonkim.common.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp

// Provide bottom padding for Floating Navigation Bar via CompositionLocal
val LocalHomeSafeBottomPadding = compositionLocalOf<Dp> {
    error(
        "LocalHomeSafeBottomPadding is not provided. Ensure this screen is a child of Composable" +
                " where this Local Composition is being provided."
    )
}