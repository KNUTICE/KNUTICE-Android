import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // Dagger-Hilt for Dependency Injection
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

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
    compileSdk = 34

    val properties = Properties().apply {
        load(FileInputStream("${rootDir}/local.properties"))
    }
    val apiMigrated = properties["api_migrated"] ?: ""

    defaultConfig {
        applicationId = "com.doyoonkim.knutice"
        minSdk = 31
        targetSdk = 34
        versionCode = 15
        versionName = "1.4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "API_MIGRATED", "\"$apiMigrated\"")

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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
    implementation(projects.feature.main)
    implementation(projects.feature.bookmark)
    implementation(projects.common)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.messaging.directboot)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.protolite.well.known.types)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)        // Library to test coroutines in JUnit
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.kotlin.serialization)

    // Coroutine for Android
    implementation(libs.kotlinx.coroutines.android)
    // Navigation for Compose
    implementation(libs.androidx.navigation.compose)
    // Coil
    implementation(libs.coil.compose)

    // Dagger Hilt for Dependency Injection
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)       // With Compose Navigation
    kapt(libs.hilt.android.compiler)

    // Retrofit 2
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Jsoup HTML Parser Library
    implementation(libs.jsoup)

    // DataStore
    implementation (libs.androidx.datastore.preferences)

    // Room Database
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    // Room Database - Kotlin Extensions and Coroutine Support
    implementation(libs.androidx.room.ktx)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}