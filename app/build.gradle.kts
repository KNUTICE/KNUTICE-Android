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
        versionCode = 41
        versionName = "@string/version_code"
    }
    buildTypes {
        // Set application id prefix for Debug Build
        debug {
            applicationIdSuffix = ".debug"
            manifestPlaceholders["appLabel"] = "@string/app_name_debug"
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["appLabel"] = "@string/app_name"
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/LICENSE.md"
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
    implementation(projects.core.infrastructure)
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

    implementation(libs.androidx.lifecycle.process)

    // Manual Implementation (Heavy Dependency)
    testImplementation(libs.robolectric)
}