import com.android.build.api.dsl.LibraryExtension
import java.io.FileInputStream
import java.util.Properties

plugins {
    // Common Android Library
    id("knutice.android.library")
    // Common Android Compose
    id("knutice.android.compose")
    // Dagger Android
    id("knutice.android.dagger")

    // Kotlin Serialization
    alias(libs.plugins.kotlinSerialization)
}

configure<LibraryExtension>() {
    namespace = "com.doyoonkim.main"

    // BuildConfig
    val properties = Properties().apply {
        load(FileInputStream("${rootDir}/local.properties"))
    }

    val origin = properties["knutice_web_app_origin"] ?: ""
    val carrelPath = properties["carrel_path"] ?: ""
    val carrelBridge = properties["carrel_bridge"] ?: ""

    val diningPath = properties["dining_path"] ?: ""

    defaultConfig {
        buildConfigField("String", "KNUTICE_ORIGIN", "\"$origin\"")
        buildConfigField("String", "CARREL_PATH", "\"$carrelPath\"")
        buildConfigField("String", "CARREL_BRIDGE", "\"$carrelBridge\"")
        buildConfigField("String", "DINING_PATH", "\"$diningPath\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.common)

    // Navigation for Compose
    implementation(libs.androidx.navigation.compose)

    // Kotlin Serialization
    implementation(libs.kotlin.serialization)

    // Androidx WebView library
    implementation(libs.androidx.webkit)
}