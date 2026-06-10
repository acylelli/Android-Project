import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

android {
    namespace = "com.example.androidapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.androidapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["naverMapClientId"] =
            localProperties.getProperty("NAVER_MAP_CLIENT_ID", "")
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${localProperties.getProperty("GEMINI_API_KEY", "")}\"",
        )
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("androidx.credentials:credentials:1.7.0-alpha02")
    implementation("androidx.credentials:credentials-play-services-auth:1.7.0-alpha02")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.kakao.maps.open:android:2.9.5")
    implementation("com.naver.maps:map-sdk:3.23.2")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
