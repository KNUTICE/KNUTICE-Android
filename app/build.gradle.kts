import com.android.build.api.dsl.ApplicationExtension

plugins {
    id("knutice.android.application")
    id("knutice.android.compose")
    id("knutice.android.dagger")
    id("knutice.android.firebase")

    // Kotlin Serialization
    alias(libs.plugins.kotlinSerialization)
}

configure<ApplicationExtension>() {
    namespace = "com.doyoonkim.knutice"

    defaultConfig {
        applicationId = "com.doyoonkim.knutice"
        versionCode = 33
        versionName = "1.6.1"
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
    implementation(projects.feature.widget)
    implementation(projects.common)

    // Kotlin Serialization
    implementation(libs.kotlin.serialization)
    // Navigation for Compose
    implementation(libs.androidx.navigation.compose)
    // DataStore
    implementation (libs.androidx.datastore.preferences)
    // WorkManager
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.work.runtime.ktx)
    // Firebase Analytics
    implementation(libs.firebase.analytics)

    // Manual Implementation (Heavy Dependency)
    testImplementation(libs.robolectric)
}