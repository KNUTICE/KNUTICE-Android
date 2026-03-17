package com.doyoonkim.knutice

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doyoonkim.common.theme.KNUTICETheme
import com.doyoonkim.knutice.di.components.DaggerSplashSceneComponent
import com.doyoonkim.main.splash.KnuticeSplashScreen
import com.doyoonkim.main.viewmodel.SplashViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    private val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check whether SplashActivity is a base of the Task Stack. If not, skip SplashActivity
        with(intent) {
            Log.d(TAG, "Current State: isTaskRoot: ${isTaskRoot}")
            Log.d(TAG, "Current State: has category: ${hasCategory(Intent.CATEGORY_LAUNCHER)}")
            Log.d(TAG, "Current State: action?: ${action?.equals(Intent.ACTION_VIEW)}")

            // Activity.isTaskRoot -> Is this activity placed at the very bottom of the Task Stack?
            // CATEGORY_LAUNCHER + ACTION_MAIN -> Enter the application via App Icon click.
            if (!isTaskRoot && action?.equals(Intent.ACTION_VIEW) == true) {
                // SplashScreen will be immediately bypassed in two case:
                // 1. App is in fore/background and user tries to enter the app via external entry point (Push Notification / Widget)
                // 2. App is in background and user tries to enter the app via clicking App Icon.
                // Do not process SplashScreen and delegate user to MainActivity immediately.
                delegateUser()
                return
            }
        }


        setContent {
            KNUTICETheme {
                val appComponent = (application as MainApplication).appComponent
                val splashSceneComponent = DaggerSplashSceneComponent.factory().create(
                    systemServices = appComponent,
                    networkProvider = appComponent,
                    localStorageProvider = appComponent,
                    firebaseInfrastructureProvider = appComponent
                )

                KnuticeSplashScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel<SplashViewModel>(factory = splashSceneComponent.viewModelFactory())
                ) { isSuccessful ->
                    Log.d("SplashActivity", "Splash Result: $isSuccessful")
                    // Handle Splash Result.
                    if (isSuccessful) {
                        // Delegate user to the MainActivity with delegated Intent
                        delegateUser()
                    } else {
                        // Initial Setup failed. Close application.
                        this.finish()
                    }

                }
            }
        }
    }

    // Receive Intent and forward it to MainActivity (Trampoline Activity Behavior)
    private fun delegateUser() {
        val delegateIntent = Intent(this, MainActivity::class.java).apply {
            // Forward Deeplink information
            data = intent.data
            action = intent.action

            // Forward Intent Extras
            intent.extras?.let {
                this.putExtras(it)
            }

            // Flag Settings
            // Put MainActivity on Stack (singleTask) if there's no existing one. If there's existing instance, reuse it.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        // Dismiss Animated Activity Transition
        val transitionOption = ActivityOptions.makeCustomAnimation(this, 0, 0)

        // Delegation
        startActivity(delegateIntent, transitionOption.toBundle())
        // Close SplashActivity for unexpected visit via Back-button action.
        this.finish()
    }

}