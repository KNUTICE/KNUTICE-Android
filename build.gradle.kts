// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // ksp
    alias(libs.plugins.google.devtools.ksp) apply false

    alias(libs.plugins.google.gms.google.services) apply false

    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.android.library) apply false

    // Required from Kotlin 2.0.0
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false

    // Room Gradle Plugin
    alias(libs.plugins.androidx.room) apply false
}