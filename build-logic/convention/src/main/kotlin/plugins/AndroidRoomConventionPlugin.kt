package plugins

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            // Version Catalog Access Helper
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            // Plugin Configuration
            with(pluginManager) {
                apply("com.google.devtools.ksp")
            }

            extensions.configure<KspExtension> {
                // Configure Room Schema Location
                arg("room.schemaLocation", "$projectDir/schemas")
            }

            dependencies {
                add("implementation", libs.findLibrary("androidx-room-runtime").get())
                add("ksp", libs.findLibrary("androidx-room-compiler").get())
                add("implementation", libs.findLibrary("androidx-room-ktx").get())
            }

        }

}