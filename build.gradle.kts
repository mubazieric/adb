plugins {
    id("com.android.application") version "8.4.2" apply false
    id("com.android.library") version "8.4.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.24" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24" apply false
}

ext.set(
    "libs",
    mapOf(
        "compileSdk" to 34,
        "minSdk" to 29,
        "targetSdk" to 34,
        "kotlinCompilerExtension" to "1.5.14",
        "composeBom" to "2024.06.00",
        "hilt" to "2.51.1",
        "room" to "2.6.1",
        "lifecycle" to "2.8.3",
        "activityCompose" to "1.9.0",
        "navigationCompose" to "2.7.7",
        "playServicesNearby" to "18.5.0",
        "accompanistPermissions" to "0.35.1-alpha"
    )
)
