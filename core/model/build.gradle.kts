plugins {
    id("knutice.jvm.library")

    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    // Kotlin Serialization
    implementation(libs.kotlin.serialization)
    implementation(libs.javax.inject)
}
