import com.android.build.api.dsl.LibraryExtension

plugins {
    id("knutice.android.library")
    id("knutice.android.dagger")

    // Compose Compiler for Kotlin version 2.0+
    alias(libs.plugins.compose.compiler)

    // Kotlin Serialization
    alias(libs.plugins.kotlinSerialization)
}

configure<LibraryExtension>() {
    namespace = "com.doyoonkim.widget"

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Module Dependencies
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.common)

    // Jetpack Glance Dependencies
    api(libs.androidx.glance.appwidget)
    api(libs.androidx.glance.material3)
    // For AppWidget preview support in the IDE
    implementation(libs.androidx.glance.appwidget.preview)
    implementation(libs.androidx.glance.preview)

    // Material3 Implementation
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)

    // Kotlin Serialization
    implementation(libs.kotlin.serialization)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

}