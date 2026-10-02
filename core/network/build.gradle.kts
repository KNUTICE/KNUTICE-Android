import com.android.build.api.dsl.LibraryExtension
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("knutice.android.library")
    id("knutice.android.dagger")
}

configure<LibraryExtension> {
    namespace = "com.doyoonkim.network"

    // BuildConfig
    val properties =
        Properties().apply {
            load(FileInputStream("$rootDir/local.properties"))
        }
    val apiBaseLive = properties["api_migrated"] ?: ""

    defaultConfig {
        buildConfigField("String", "API_LIVE", "\"$apiBaseLive\"")
    }

    buildFeatures {
        buildConfig = true
    }

    testOptions {
        unitTests {
            // let unmocked Android-owned classes return default value.
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.common)

    testImplementation(libs.okhttp3.mockwebserver)
    testImplementation(libs.logging.interceptor)
    androidTestImplementation(libs.okhttp3.mockwebserver)

    // Retrofit 2
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
}
