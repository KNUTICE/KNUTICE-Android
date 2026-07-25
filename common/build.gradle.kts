import com.android.build.api.dsl.LibraryExtension

plugins {
    id("knutice.android.library")
    id("knutice.android.compose")
    id("knutice.android.dagger")

    alias(libs.plugins.kotlinSerialization)
}

configure<LibraryExtension> {
    namespace = "com.doyoonkim.common"
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    // Coil
    implementation(libs.coil.compose)
    // Navigation For Compose
    implementation(libs.androidx.navigation.compose)
    // Kotlin Serialization
    implementation(libs.kotlin.serialization)
    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
}
