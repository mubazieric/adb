plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.iwatdigital.dropdroid.core"
    compileSdk = rootProject.ext["libs.compileSdk"] as Int

    defaultConfig {
        minSdk = rootProject.ext["libs.minSdk"] as Int
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        buildConfig = false
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:${rootProject.ext["libs.composeBom"]}"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}
