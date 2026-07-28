package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class FirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            // Version Catalog Access Helper
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.withPlugin("com.android.application") {
                // Google Services Plugin (Only for Application Module)
                pluginManager.apply("com.google.gms.google-services")
            }

            // Dependency Configuration
            dependencies {
                // Firebase BoM
                val bom = libs.findLibrary("firebase-bom").get()
                add("implementation", platform(bom))
                // Add necessary dependencies per need
            }
        }
}
