import org.jetbrains.kotlin.gradle.internal.builtins.StandardNames.FqNames.target

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // ksp
    alias(libs.plugins.google.devtools.ksp) apply false

    alias(libs.plugins.google.gms.google.services) apply false

    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.android.library) apply false

    // Required from Kotlin 2.0.0
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false

    // Room Gradle Plugin
    alias(libs.plugins.androidx.room) apply false

    // Spotless
    id("com.diffplug.spotless") version "8.8.0"
}

spotless {
    // optional: limit format enforcement to just the files changed by this feature branch
    ratchetFrom("origin/development")

    format("misc") {
        // define the files to apply `misc` to
        target("*.gradle", ".gitattributes", ".gitignore")

        // define the steps to apply to those files
        trimTrailingWhitespace()
        leadingSpacesToTabs() // or leadingTabsToSpaces. Takes an integer argument if you don't like 4
        endWithNewline()
    }

    kotlin {
        // Target all Kotlin files
        target("**/*.kt")
        // MUST exclude generated code to preserve Gradle Build Cache
        targetExclude("**/build/**/*.kt", "**/generated/**/*.kt")

        // Pin Ktlint version to ensure deterministic CI runs
        ktlint("1.2.1")
            .setEditorConfigPath("$projectDir/.editorconfig")
            .editorConfigOverride(
                mapOf(
                    "ktlint_standard_no-wildcard-imports" to "disabled",
                    "ktlint_standard_filename" to "disabled",
                    "ktlint_standard_package-name" to "disabled",
                    "ktlint_standard_class-naming" to "disabled",
                    "ktlint_standard_function-naming" to "disabled",
                    "ktlint_standard_property-naming" to "disabled",
                    "ktlint_standard_backing-property-naming" to "disabled"
                )
            )
    }

    kotlinGradle {
        target("*.gradle.kts", "**/*.gradle.kts")
        targetExclude("**/build/**/*.gradle.kts")
        ktlint("1.2.1")
    }
}
