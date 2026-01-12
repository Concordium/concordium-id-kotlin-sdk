import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.concordium.idapp.sdk"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
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
    }
}

group = "com.concordium.sdk"
version = "0.0.2"

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(
        groupId = project.group.toString(),
        artifactId = project.name,
        version = project.version.toString()
    )

    pom {
        name.set("Concordium IDApp SDK for Android")
        description.set("The Concordium IDApp SDK enables Android developers to seamlessly integrate Concordium blockchain functionality into their applications.")
        inceptionYear.set("2025")
        url.set("https://github.com/Concordium/concordium-id-kotlin-sdk/")
        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://github.com/Concordium/concordium-id-kotlin-sdk/blob/main/LICENSE")
            }
        }
        developers {
            developer {
                id.set("concordium")
                name.set("Concordium")
                email.set("developers@concordium.com")
                organization.set("Concordium")
                organizationUrl.set("https://concordium.com")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/Concordium/concordium-id-kotlin-sdk.git")
            developerConnection.set("scm:git:ssh://git@github.com/Concordium/concordium-id-kotlin-sdk.git")
            url.set("https://github.com/Concordium/concordium-id-kotlin-sdk")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.zxing.core)
    implementation(libs.concordium.android.sdk)

    testImplementation(libs.junit)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
