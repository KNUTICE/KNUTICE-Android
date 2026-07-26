package plugins

import extensions.configureDaggerCommon
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class JvmDaggerConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            with(pluginManager) {
                apply("kotlin-kapt")

                withPlugin("kotlin-kapt") {
                    extensions.configure<KaptExtension>("kapt") {
                        correctErrorTypes = true
                    }
                }
            }

            // Pure Dagger Dependencies
            configureDaggerCommon()
        }
}
