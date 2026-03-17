import com.android.build.api.dsl.LibraryExtension

plugins {
    id("knutice.android.library")
    id("knutice.android.compose")
    id("knutice.android.dagger")
}

configure<LibraryExtension>() {
    namespace = "com.doyoonkim.bookmark"
}

dependencies {
    implementation(projects.common)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.notification)

    implementation(libs.androidx.navigation.compose)
}