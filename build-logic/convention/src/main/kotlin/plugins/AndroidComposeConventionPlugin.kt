package plugins

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * @author kimdoyoon
 * Created 2/10/26 at 3:02 AM
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Access Helper
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            // Android Block Configuration
            // Polymorphic approach for configuration object
            val android = extensions.getByType<CommonExtension>()
            android.run {
                buildFeatures.compose = true
            }

            dependencies {
                "implementation" (libs.findLibrary("androidx-activity-compose").get())

                val bom = libs.findLibrary("androidx-compose-bom").get()
                "implementation" (platform(bom))
                "implementation" (libs.findLibrary("androidx-ui").get())
                "implementation" (libs.findLibrary("androidx-ui-graphics").get())
                "implementation" (libs.findLibrary("androidx-ui-tooling-preview").get())
                "implementation" (libs.findLibrary("androidx-material3").get())
                "implementation" (libs.findLibrary("androidx-material").get())

                "androidTestImplementation" (platform(bom))
                "androidTestImplementation" (libs.findLibrary("androidx-ui-test-junit4").get())
                "debugImplementation" (libs.findLibrary("androidx-ui-tooling").get())
                "debugImplementation" (libs.findLibrary("androidx-ui-test-manifest").get())
            }
        }
    }
}
