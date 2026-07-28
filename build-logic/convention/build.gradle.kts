// Dependencies for configuring a custom plugin

plugins {
    `kotlin-dsl`
}

group = "com.doyoonkim.buildlogic"

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "knutice.android.application"
            implementationClass = "plugins.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "knutice.android.library"
            implementationClass = "plugins.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "knutice.android.compose"
            implementationClass = "plugins.AndroidComposeConventionPlugin"
        }
        register("androidDagger") {
            id = "knutice.android.dagger"
            implementationClass = "plugins.AndroidDaggerConventionPlugin"
        }
        register("androidFirebase") {
            id = "knutice.android.firebase"
            implementationClass = "plugins.FirebaseConventionPlugin"
        }
        register("androidRoom") {
            id = "knutice.android.room"
            implementationClass = "plugins.AndroidRoomConventionPlugin"
        }
        register("jvmLibrary") {
            id = "knutice.jvm.library"
            implementationClass = "plugins.JvmLibraryConventionPlugin"
        }
        register("jvmDagger") {
            id = "knutice.jvm.dagger"
            implementationClass = "plugins.JvmDaggerConventionPlugin"
        }
    }
}

dependencies {
    // Allow custom-defined plugins to refer Android/Kotlin classes.
    // compileOnly --> Avoid bundle following libraries.
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.plugin)
    compileOnly(libs.ksp.gradlePlugin)
}
