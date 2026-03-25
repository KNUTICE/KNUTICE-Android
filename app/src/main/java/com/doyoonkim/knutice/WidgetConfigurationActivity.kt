package com.doyoonkim.knutice

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doyoonkim.common.theme.KNUTICETheme
import com.doyoonkim.knutice.di.components.DaggerWidgetConfigSceneComponent
import com.doyoonkim.main.preference.WidgetPreferencesScreen
import com.doyoonkim.main.viewmodel.WidgetConfigViewModel

class WidgetConfigurationActivity : ComponentActivity() {
    private val TAG = "WidgetConfigurationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initial Result Value (Passive Value)
        // Allow OS safely discard pending widget when configuration failed.
        setResult(RESULT_CANCELED)

        // Source Widget Validation
        val widgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // Composable Screen
        setContent {
            KNUTICETheme {
                // Dependency Injection
                val appComponent = (application as MainApplication).appComponent
                val sceneComponent = DaggerWidgetConfigSceneComponent.factory().create(
                    systemService = appComponent,
                    localCacheProvider = appComponent
                )

                WidgetPreferencesScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel<WidgetConfigViewModel>(factory = sceneComponent.viewModelFactory())
                ) {
                    // Queue OS to process Pending Widget (Send RESULT_OK)
                    val result = Intent().apply {
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                    }
                    setResult(RESULT_OK, result)
                    // Exit Activity
                    this@WidgetConfigurationActivity.finish()
                }
            }
        }
    }

}