import com.android.build.api.dsl.LibraryExtension

plugins {
    // Common Android Library
    id("knutice.android.library")
    // Common Android Compose
    id("knutice.android.compose")
    // Dagger Android
    id("knutice.android.dagger")
}

configure<LibraryExtension>() {
    namespace = "com.doyoonkim.main"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.common)

    // Navigation for Compose
    implementation(libs.androidx.navigation.compose)
}