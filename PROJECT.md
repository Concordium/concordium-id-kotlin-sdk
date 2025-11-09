# Concordium IDApp SDK - Technical Overview

The Concordium IDApp SDK is an Android library enabling blockchain integration for mobile applications. This document outlines the technical architecture and development guidelines.

## 🏗 Key Modules

- **concordium-idapp-sdk**: Core SDK implementation, Blockchain integration
- **test-app**: Sample implementation and integration examples

## 🛠 Technical Stack

### Requirements
- Android Studio Arctic Fox+
- JDK 11+
- Android SDK 21+
- Gradle 7.0+

### Core Technologies
```kotlin
// Build Configuration
android {
    compileSdk = 34
    minSdk = 21
    targetSdk = 34
}

// Key Dependencies
dependencies {
    implementation("com.concordium.sdk:concordium-android-sdk:11.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")
}

## � Core Components

### SDK Architecture
```
com.concordium.idapp.sdk
├── api/
│   ├── ConcordiumIDAppSDK     # Core blockchain operations
│   └── ConcordiumIDAppPopup   # UI components
├── model/
│   ├── CCDAccountKeyPair      # Account management
│   └── TransactionModels      # Blockchain transactions
└── ui/                        # Ready-to-use UI components
```

### Development Workflow
1. Branch from `main` for features/fixes
2. Write tests (unit, integration)
3. Submit PR with documentation
4. Review and merge
5. Create release tag

'### Configuration Setup

1. Update the following properties in your `local.properties`:

```properties
# Android SDK path
sdk.dir=/path/to/your/android/sdk

# WalletConnect Configuration
WC_URI=wc:xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx@2?relay-protocol=irn&symKey=your-symmetric-key
WC_SESSION_TOPIC=your-session-topic-here

# Unsigned Credential Deployment Info
UNSIGNED_CDI_STRING={"arData":{"yourData":"here"},"credId":"your-cred-id"}

# Transaction expiry time in epoch seconds
EXPIRY=3600
```