import com.android.build.api.dsl.LibraryExtension

plugins {
    id("knutice.android.library")
    id("knutice.android.firebase")
    id("knutice.android.dagger")
}

configure<LibraryExtension>() {
    namespace = "com.doyoonkim.infrastructure"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.common)
    implementation(projects.core.model)

    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)
}