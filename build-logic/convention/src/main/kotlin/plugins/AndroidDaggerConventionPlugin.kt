package plugins

import extensions.configureDaggerCommon
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class AndroidDaggerConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Version Catalog Access Helper
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            // Configure Necessary Plugins
            with(pluginManager) {
                apply("kotlin-kapt")

                // Kapt Configuration
                withPlugin("kotlin-kapt") {
                    extensions.configure<KaptExtension>("kapt") {
                        correctErrorTypes = true
                    }
                }
            }

            // Configure Pure Dagger Common Dependencies
            configureDaggerCommon()

            // Required Dependencies (Dagger 2 for Android)
            dependencies {
                add("implementation", libs.findLibrary("dagger-android").get())
                add("implementation", libs.findLibrary("dagger-android-support").get())
                add("kapt", libs.findLibrary("dagger-android-processor").get())

                // Test Implementation
                add("androidTestImplementation", libs.findLibrary("dagger").get())
                add("kaptAndroidTest", libs.findLibrary("dagger-compiler").get())
            }
        }
    }

}