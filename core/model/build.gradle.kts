plugins {
    id("knutice.jvm.library")
}

dependencies {
    // Kotlin Serialization
    implementation(libs.kotlin.serialization)
    implementation(libs.javax.inject)
}
