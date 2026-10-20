plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// All four variables are required together. Debug builds need no release keys.
val releaseKeyNames = listOf(
    "ANDROID_KEYSTORE_PATH", "ANDROID_KEYSTORE_PASSWORD",
    "ANDROID_KEY_ALIAS", "ANDROID_KEY_PASSWORD"
)
val releaseKeys = releaseKeyNames.associateWith { System.getenv(it) }
val hasReleaseKey = releaseKeys.values.all { !it.isNullOrBlank() }
check(releaseKeys.values.none { !it.isNullOrBlank() } || hasReleaseKey) {
    "Set all four ANDROID_KEY* environment variables, or clear all of them."
}

android {
    namespace = "com.example.pockettasks"
    compileSdk = 36
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "com.example.pockettasks"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }
    signingConfigs {
        if (hasReleaseKey) {
            create("release") {
                storeFile = file(releaseKeys.getValue("ANDROID_KEYSTORE_PATH")!!)
                storePassword = releaseKeys.getValue("ANDROID_KEYSTORE_PASSWORD")
                keyAlias = releaseKeys.getValue("ANDROID_KEY_ALIAS")
                keyPassword = releaseKeys.getValue("ANDROID_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            if (hasReleaseKey) signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.09.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.3")
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    testImplementation("junit:junit:4.13.2")
}
