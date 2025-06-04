import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.doyoonkim.network"
    compileSdk = 35

    val properties = Properties().apply {
        load(FileInputStream("${rootDir}/local.properties"))
    }
    val apiBaseLive = properties["api_live"] ?: ""

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "API_LIVE", "\"$apiBaseLive\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Coroutine for Android
    implementation(libs.kotlinx.coroutines.android)

    // Retrofit 2
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Dagger
//    implementation(libs.dagger)
//    implementation(libs.dagger.android)
//    implementation(libs.dagger.android.support)
//    implementation(libs.dagger.compiler)
//    implementation(libs.dagger.android.processor)
}