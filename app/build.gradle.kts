import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // Dagger-Hilt for Dependency Injection
    id("kotlin-kapt")

    // Keep this plugin in app module, since app module handles all related app information
    // such as App ID , etc. for using firebase services.
    alias(libs.plugins.google.gms.google.services)

    alias(libs.plugins.kotlinSerialization)

    // Required from Kotlin 2.0.0 (Every module using Compose)
    alias(libs.plugins.compose.compiler)

    // KSP Plugin for Room Database
//    id("com.google.devtools.ksp")
}

android {
    namespace = "com.doyoonkim.knutice"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.doyoonkim.knutice"
        minSdk = 31
        targetSdk = 35
        versionCode = 31
        versionName = "1.6.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("ExperimentalServerDebug") {
            initWith(buildTypes["debug"])
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.network)
    implementation(projects.core.notification)
    implementation(projects.feature.main)
    implementation(projects.feature.bookmark)
    implementation(projects.common)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.messaging.directboot)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.protolite.well.known.types)
    implementation(libs.firebase.analytics)

    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)        // Library to test coroutines in JUnit
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.ui.test)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Dagger
    implementation(libs.dagger)
    implementation(libs.dagger.android)
    implementation(libs.dagger.android.support)
    kapt(libs.dagger.compiler)
    kapt(libs.dagger.android.processor)

    // Dagger for Android Test
    androidTestImplementation(libs.dagger)
    androidTestImplementation(libs.dagger.compiler)
    kaptAndroidTest(libs.dagger.compiler)

    implementation(libs.kotlin.serialization)

    // Coroutine for Android
    implementation(libs.kotlinx.coroutines.android)
    // Navigation for Compose
    implementation(libs.androidx.navigation.compose)

    // DataStore
    implementation (libs.androidx.datastore.preferences)

    // WorkManager
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.work.runtime.ktx)

}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}