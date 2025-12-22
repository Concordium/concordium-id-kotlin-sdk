plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
    id("signing")
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

// Generate sources jar
android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            afterEvaluate {
                from(components["release"])
            }
            pom {
                name.set("Concordium IDApp SDK for Android")
                description.set("Android SDK for seamless integration with Concordium blockchain functionality, providing tools for account management, transaction signing, and secure interactions.")
                url.set("https://github.com/Concordium/concordium-id-kotlin-sdk")

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
    }

    repositories {
        maven {
            name = "sonatype"
            url = if (project.version.toString().endsWith("SNAPSHOT")) {
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            } else {
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            }
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_CENTRAL_TOKEN")
            }
        }
        mavenLocal()
    }
}

signing {
    val signingKey = System.getenv("GPG_SIGNING_KEY")
    val signingPassphrase = System.getenv("GPG_SIGNING_PASSPHRASE")

    if (!signingKey.isNullOrBlank() && !signingPassphrase.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassphrase)
        sign(publishing.publications["release"])
    } else {
        // Log a friendly warning for local builds / CI without signing
        println("⚠️  GPG signing credentials not found - artifacts will not be signed")
        println("   This is expected for local builds. Signing is only required for publishing.")
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
