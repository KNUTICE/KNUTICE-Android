import com.android.build.api.dsl.LibraryExtension

plugins {
    id("knutice.android.library")
    id("knutice.android.dagger")
    id("knutice.android.room")

    // Kotlin Serialization
    alias(libs.plugins.kotlinSerialization)
}

configure<LibraryExtension>() {
    namespace = "com.doyoonkim.data"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.domain)            // Dependency Inversion
    implementation(projects.common)

    // Kotlin Serialization
    implementation(libs.kotlin.serialization)

    // PreferenceDataStore
    implementation (libs.androidx.datastore.preferences)
}