package extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

// Pure Dagger Common Dependencies
internal fun Project.configureDaggerCommon() {
    // Version Catalog Access Helper
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    // Common Dagger Dependencies (Pure Dagger)
    dependencies {
        add("implementation", libs.findLibrary("dagger").get())
        add("kapt", libs.findLibrary("dagger-compiler").get())

        // Test Implementation
        add("testImplementation", libs.findLibrary("dagger").get())
        add("kaptTest", libs.findLibrary("dagger-compiler").get())
    }
}