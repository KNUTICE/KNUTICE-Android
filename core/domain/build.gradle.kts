plugins {
    id("knutice.jvm.library")
    id("knutice.jvm.dagger")
}

dependencies {
    implementation(projects.core.model)

    // Open Korean Tokenizer
    implementation(libs.open.korean.text)
}