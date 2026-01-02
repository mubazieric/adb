plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.iwatdigital.dropdroid"
    compileSdk = rootProject.ext["libs.compileSdk"] as Int

    defaultConfig {
        applicationId = "com.iwatdigital.dropdroid"
        minSdk = rootProject.ext["libs.minSdk"] as Int
        targetSdk = rootProject.ext["libs.targetSdk"] as Int
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.ext["libs.kotlinCompilerExtension"] as String
    }
    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":transport-nearby"))
    implementation(project(":transport-wifidirect"))

    implementation(platform("androidx.compose:compose-bom:${rootProject.ext["libs.composeBom"]}"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${rootProject.ext["libs.lifecycle"]}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${rootProject.ext["libs.lifecycle"]}")
    implementation("androidx.activity:activity-compose:${rootProject.ext["libs.activityCompose"]}")
    implementation("androidx.navigation:navigation-compose:${rootProject.ext["libs.navigationCompose"]}")
    implementation("com.google.accompanist:accompanist-permissions:${rootProject.ext["libs.accompanistPermissions"]}")

    implementation("com.google.dagger:hilt-android:${rootProject.ext["libs.hilt"]}")
    kapt("com.google.dagger:hilt-compiler:${rootProject.ext["libs.hilt"]}")

    implementation("androidx.room:room-ktx:${rootProject.ext["libs.room"]}")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:${rootProject.ext["libs.composeBom"]}"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
