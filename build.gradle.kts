// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // Dagger Hilt for Dependency Injection
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    alias(libs.plugins.google.gms.google.services) apply false

    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.android.library) apply false

    // Required from Kotlin 2.0.0
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false

    // KSP Plugin for Room Database
//    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}