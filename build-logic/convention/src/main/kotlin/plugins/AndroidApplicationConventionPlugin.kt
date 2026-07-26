package plugins

import com.android.build.api.dsl.ApplicationExtension
import extensions.configureAndroidCommon
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * @author kimdoyoon
 * Created 2/10/26 at 3:39 AM
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }

        // Common Android Configuration
        configureAndroidCommon()

        // Application Specific Configuration
        extensions.configure<ApplicationExtension> {
            defaultConfig {
                targetSdk = 35
                vectorDrawables { useSupportLibrary = true }
            }
        }

        dependencies {
            "implementation" (libs.findLibrary("androidx-appcompat").get())
            "implementation" (libs.findLibrary("material").get())
        }
    }
}
