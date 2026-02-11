package plugins

import extensions.configureDaggerCommon
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            // Version Catalog Access Helper
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            // Plugin Configuration
            with(pluginManager) {
                apply("java-library")
                apply("org.jetbrains.kotlin.jvm")
                apply("kotlin-kapt")

                withPlugin("kotlin-kapt") {
                    extensions.configure<KaptExtension>("kapt") {
                        correctErrorTypes = true
                    }
                }
            }

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            extensions.configure<KotlinJvmProjectExtension> {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }

            // Pure Dagger Configuration
            configureDaggerCommon()

            dependencies {
                // Kotlin Coroutines Core
                add("implementation", libs.findLibrary("kotlinx-coroutines-core").get())

                // Test Implementation
                add("testImplementation", libs.findLibrary("junit").get())
                add("testImplementation", libs.findLibrary("mockk").get())
                add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
            }
        }

}