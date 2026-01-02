plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.iwatdigital.dropdroid.data"
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
    implementation(project(":core"))
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.room:room-runtime:${rootProject.ext["libs.room"]}")
    implementation("androidx.room:room-ktx:${rootProject.ext["libs.room"]}")
    kapt("androidx.room:room-compiler:${rootProject.ext["libs.room"]}")
    implementation("com.google.dagger:hilt-android:${rootProject.ext["libs.hilt"]}")
    kapt("com.google.dagger:hilt-compiler:${rootProject.ext["libs.hilt"]}")
}
