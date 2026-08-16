package extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Project Extension for Android Common

internal fun Project.configureAndroidCommon() {
    // Version Catalog Access Helper
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    extensions.getByType<CommonExtension>().apply {
        // Compile SDK
        compileSdk = 36

        // Default Configuration
        with(defaultConfig) {
            minSdk = 31
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        // Compile Options
        with(compileOptions) {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        // Force Jvm Version for Kotlin Complier
        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

        // Common Dependencies
        dependencies {
            // Core KTX Dependencies
            add("implementation", libs.findLibrary("androidx-core-ktx").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
            // Kotlin Coroutine for Android
            add("implementation", libs.findLibrary("kotlinx-coroutines-android").get())
            // AndoridX Test Library provides Kotlin Extension
            add("implementation", libs.findLibrary("androidx-junit-ktx").get())

            // Test Implementation
            add("testImplementation", libs.findLibrary("junit").get())
            add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
            add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())
            add("androidTestImplementation", libs.findLibrary("mockk-android").get())
        }
    }
}
