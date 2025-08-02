plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("kotlin-kapt")
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    // Dagger
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    // Open Korean Tokenizer
    implementation(libs.open.korean.text)
}