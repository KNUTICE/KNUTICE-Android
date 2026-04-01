package com.doyoonkim.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun OnForegroundFocusEffect(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onFocused: () -> Unit
) {
    // Instant Focus
    LaunchedEffect(Unit) {
        onFocused()
    }

    // LifecycleEvent-aware execution (executed only at the focus regain.)
    DisposableEffect(lifecycleOwner) {
        var wasPaused = false

        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    // OS WindowManager removed focus (e.g. Split Screen / dialog / background)
                    wasPaused = true
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (wasPaused) {
                        onFocused()
                    }
                }
                else -> { /* DO NOTHING */ }
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            // Unregister LifecycleObserver on Disposal
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}