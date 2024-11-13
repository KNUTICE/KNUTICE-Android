import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // Dagger-Hilt for Dependency Injection
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)

    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.doyoonkim.knutice"
    compileSdk = 34

    val properties = Properties().apply {
        load(FileInputStream("${rootDir}/local.properties"))
    }
    val apiRoot = properties["api_root"] ?: ""

    defaultConfig {
        applicationId = "com.doyoonkim.knutice"
        minSdk = 29
        targetSdk = 34
        versionCode = 3
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "API_ROOT", "\"$apiRoot\"")
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

}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}