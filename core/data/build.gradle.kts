import com.android.build.api.dsl.LibraryExtension

plugins {
    id("knutice.android.library")
    id("knutice.android.dagger")
    id("knutice.android.room")
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
}