package com.doyoonkim.knutice

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doyoonkim.common.theme.KNUTICETheme
import com.doyoonkim.knutice.di.components.DaggerWidgetConfigSceneComponent
import com.doyoonkim.knutice.di.util.DefaultSystemService
import com.doyoonkim.main.preference.WidgetPreferencesScreen
import com.doyoonkim.main.viewmodel.WidgetConfigViewModel

class WidgetConfigurationActivity : ComponentActivity() {
    private val TAG = "WidgetConfigurationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Source Widget Validation
        intent.extras?.let {
            val widgetId = it.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                this@WidgetConfigurationActivity.finish()
                return
            }
        }

        // Composable Screen
        setContent {
            KNUTICETheme {
                // Dependency Injection
                val appComponent = (application as MainApplication).appComponent
                val sceneComponent = DaggerWidgetConfigSceneComponent.factory().create(
                    DefaultSystemService(appComponent)
                )

                WidgetPreferencesScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel<WidgetConfigViewModel>(factory = sceneComponent.viewModelFactory())
                ) {
                    // Exit Activity
                    this@WidgetConfigurationActivity.finish()
                }
            }
        }
    }

}