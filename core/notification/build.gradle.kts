import com.android.build.api.dsl.LibraryExtension

plugins {
    id("knutice.android.library")
    id("knutice.android.dagger")
    id("knutice.android.firebase")
}

configure<LibraryExtension>() {
    namespace = "com.doyoonkim.notification"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.common)
    implementation(projects.core.model)

    // Firebase Cloud Messaging (Allow Transitive Access)
    api(libs.firebase.messaging)
    api(libs.firebase.messaging.directboot)

    // WorkManager
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.work.runtime.ktx)
}