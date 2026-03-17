package plugins

import com.android.build.api.dsl.LibraryExtension
import extensions.configureAndroidCommon
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/**
 * @author kimdoyoon
 * Created 2/10/26 at 2:37 AM
 */


class AndroidLibraryConventionPlugin : Plugin<Project> {
    private val TAG = "plugins.AndroidLibraryConventionPlugin"

    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            // Primitive Plugin
            with(pluginManager) {
                /*
                    plugins {
                        alias(libs.plugins.android.library)
                        alias(libs.plugins.kotlin.android)
                    }
                 */
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            // Default BuildTypes Configuration
            extensions.configure<LibraryExtension> {
                buildTypes {
                    release {
                        // Prevent Accidental Obfuscation.
                        isMinifyEnabled = false
                    }
                }
            }

            // Android Common Extension
            configureAndroidCommon()

            // Library Specific Configuration
            extensions.getByType<LibraryExtension>().apply {
                defaultConfig {
                    // Library Module specifically needs consumerProguardFile.
                    consumerProguardFiles("consumer-rules.pro")
                }
            }
        }
    }
}