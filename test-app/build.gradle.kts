import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

fun getString(key: String, default: String = ""): String = localProperties.getProperty(key, default)
fun getLong(key: String, default: Long = 0L): Long =
    localProperties.getProperty(key)?.toLongOrNull() ?: default

android {
    namespace = "com.concordium.idapp.sdk.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.concordium.idapp.sdk.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        val wcUri = getString("WC_URI")
        val wcSessionTopic = getString("WC_SESSION_TOPIC")
        val unsignedCdiString = getString("UNSIGNED_CDI_STRING").replace("\"", "\\\"")
        val expiry = getLong("EXPIRY")

        buildConfigField("String", "WC_URI", "\"$wcUri\"")
        buildConfigField("String", "WC_SESSION_TOPIC", "\"$wcSessionTopic\"")
        buildConfigField("String", "UNSIGNED_CDI_STRING", "\"$unsignedCdiString\"")
        buildConfigField("Long", "EXPIRY", "${expiry}L")
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.concordium.android.sdk)
    implementation(project(":concordium-idapp-sdk"))

    testImplementation(libs.junit)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
