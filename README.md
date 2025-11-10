# Concordium IDApp SDK for Android

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

> Build secure and user-friendly blockchain applications with Concordium's official Android SDK

The Concordium IDApp SDK enables Android developers to seamlessly integrate Concordium blockchain functionality into their applications. This SDK provides a robust set of tools for account management, transaction signing, and secure interactions with the Concordium blockchain.

## 🚀 Features

- 🔐 Secure account management and key generation
- 📝 Transaction signing and submission
- 🔗 WalletConnect integration for ID App interactions
- 📱 Ready-to-use UI components

## 📦 Package Information
- **Module**: `concordium-idapp-sdk`
- **Latest Version**: [Check releases](https://github.com/Concordium/concordium-id-kotlin-sdk/releases)

## 🔧 Installation


### Option 1: Publish to Maven Local (local development)
If you want to test the SDK as a binary dependency without pushing to a remote repository, publish it to your local Maven cache and consume it from there.

1. Publish the SDK to your local Maven repository from the project root:

```bash
# publish all publications of the SDK module to mavenLocal
./gradlew :concordium-idapp-sdk:publishToMavenLocal
```

2. In your consumer project, make sure `mavenLocal()` appears before other repositories so Gradle can resolve the locally published artifact:

```kotlin
repositories {
    mavenLocal()
    google()
    mavenCentral()
}
```

3. Add the dependency using the same coordinates configured in the SDK module (`group`, `artifactId`, `version`). 

Example (Kotlin DSL):

```kotlin
dependencies {
    implementation("com.concordium.sdk:concordium-idapp-sdk:0.0.1")
}
```

Notes:
- Publishing to Maven Local is intended for local development and testing. For CI and team sharing prefer an internal Maven repository (Artifactory/Nexus/GitHub Packages).
- To remove a published local artifact, delete it from your local Maven cache (usually under `~/.m2/repository/com/concordium/sdk/concordium-idapp-sdk/<version>`).
- If you change `group`/`artifactId`/`version` update the consumer dependency accordingly.

### Option 1: Git Submodule
This method is ideal for developers who want to use the SDK directly and potentially contribute back to the project.

```bash
git submodule add https://github.com/Concordium/concordium-id-kotlin-sdk.git libs/concordium-id-kotlin-sdk
git submodule update --init --recursive
```

2. Include the module in your `settings.gradle.kts` (or `settings.gradle`):

```kotlin
include(":libs:concordium-idapp-sdk")
```

3. Add a project dependency in your app module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":libs:concordium-idapp-sdk"))
    implementation("com.concordium.sdk:concordium-android-sdk:11.1.0")
}
```

💡 **Tips:**
- Run Gradle sync after adding the dependency
- Check [releases page](https://github.com/Concordium/concordium-id-kotlin-sdk/releases) for the latest version

## 🚀 Getting Started

### Initialization
Call the initializer early in your app (for example, in `Application.onCreate`):

```kotlin
import com.concordium.idapp.sdk.api.ConcordiumIDAppSDK

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ConcordiumIDAppSDK.initialize(this, enableDebugLog = false)
    }
}
```

## 📚 API Reference

The SDK provides a clean and intuitive API through three main components:

### 1. 🛠 ConcordiumIDAppSDK
Core functionality for blockchain interactions:
   - `initialize(context: Context, enableDebugLog: Boolean = false)`
   - `signAndSubmit(seedPhrase: String, expiry: Long, unsignedCdiStr: String, accountIndex: Int = 0, network: Network = Network.MAINNET): String`
   - `generateAccountWithSeedPhrase(seed: String, network: Network, accountIndex: Int = 0): CCDAccountKeyPair`
   - `clear()`

### 2. 🖼 ConcordiumIDAppPopup
UI components and flows for user interactions:
   - `invokeIdAppDeepLinkPopup`: Launch WalletConnect flows
   - `invokeIdAppActionsPopup`: Present account creation/recovery options
   - `closePopup`: Dismiss active popups

### 3. 🔑 CCDAccountKeyPair
Essential account data model:
   - `publicKey`: Account's public verification key
   - `signingKey`: Account's signing key

## 💡 Code Examples

Here are some common use cases to help you get started:

### 1) Sign and submit a credential deployment transaction

Make sure `unsignedCdiStr` contains a valid JSON string matching `UnsignedCredentialDeploymentInfo`.

```kotlin
import com.concordium.idapp.sdk.api.ConcordiumIDAppSDK
import com.concordium.sdk.crypto.wallet.Network

fun submit(seedPhrase: String, unsignedCdiJson: String) {
    val expiryEpochSec = 1710000000L // choose appropriate expiry
    val txHash = ConcordiumIDAppSDK.signAndSubmit(
        seedPhrase = seedPhrase,
        expiry = expiryEpochSec,
        unsignedCdiStr = unsignedCdiJson,
        accountIndex = 0,
        network = Network.MAINNET,
    )
    println("Transaction hash: $txHash")
}
```

### 2) Launching ID App deep-link flow

```kotlin
import com.concordium.idapp.sdk.api.ConcordiumIDAppPopup

val walletConnectUri = "wc:...@2?relay-protocol=...&symKey=..."
ConcordiumIDAppPopup.invokeIdAppDeepLinkPopup(walletConnectUri)
```

### 4) Present create/recover actions popup

```kotlin
ConcordiumIDAppPopup.invokeIdAppActionsPopup(
    walletConnectSessionTopic = "abcd1234...",
    onCreateAccount = { /* handle create */ },
    onRecoverAccount = { /* handle recover */ },
)
```

### 3) Derive account keys from a seed phrase

```kotlin
import com.concordium.idapp.sdk.api.ConcordiumIDAppSDK
import com.concordium.idapp.sdk.api.model.CCDAccountKeyPair
import com.concordium.sdk.crypto.wallet.Network

fun showKeys(seed: String) {
    val keys: CCDAccountKeyPair = ConcordiumIDAppSDK.generateAccountWithSeedPhrase(
        seed = seed,
        network = Network.MAINNET,
        accountIndex = 0,
    )

    println("publicKey=${keys.publicKey}")
    println("signingKey=${keys.signingKey}")
}
```
## Cleanup

When you're done using the SDK, make sure to clean up resources:

```kotlin
ConcordiumIDAppSDK.clear()
```

## 🌐 Network Configuration

The SDK seamlessly integrates with both production and testing environments:

### Mainnet
- **URL**: `https://grpc.mainnet.concordium.software`
- **Use Case**: Production deployments
- **Default Port**: `20000`

### Testnet
- **URL**: `grpc.testnet.concordium.com`
- **Use Case**: Development and testing
- **Default Port**: `20000`

## 📦 Dependencies

The SDK is built with industry-standard technologies:
- Jetpack Compose for UI components
- ZXing for QR code generation
- Concordium Android SDK for blockchain interactions

## 📋 Release Guidelines

### Creating a New Release
Follow these steps to publish a new version of the SDK:

1. Ensure you are on the `main` branch and it is up to date:
   ```bash
   git checkout main
   git pull origin main
   ```

2. Make sure all tests pass and the build is successful:
   ```bash
   ./gradlew clean build test
   ```

3. Update the version number in `concordium-idapp-sdk/build.gradle.kts`:
   - Follow semantic versioning (MAJOR.MINOR.PATCH)
   - Example: `version = "1.0.0"`

4. Commit the version change:
   ```bash
   git add concordium-idapp-sdk/build.gradle.kts
   git commit -m "Bump version to x.y.z"
   ```

5. Create and push a new tag:
   ```bash
   git tag -a vx.y.z -m "Release version x.y.z"
   git push origin main --tags
   ```

6. Create a release on GitHub:
   - Go to the repository's Releases page
   - Click "Create a new release"
   - Select the tag you just created
   - Title: "Release vx.y.z"
   - Add release notes describing the changes
   - Attach any relevant binaries or documentation

Notes:
- Replace x.y.z with the actual version number
- Always create releases from the `main` branch
- Ensure all changes are properly documented
- Follow semantic versioning guidelines:
  - MAJOR: Breaking changes
  - MINOR: New features (backwards compatible)
  - PATCH: Bug fixes (backwards compatible)

## 🤝 Contributing

We welcome contributions from the community! Here's how you can help:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to your branch
5. Open a Pull Request

Please ensure your code follows our coding standards and includes appropriate tests.

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 💬 Support

Need help? We're here for you!

- 📝 [Create an issue](https://github.com/Concordium/concordium-id-kotlin-sdk/issues) for bug reports or feature requests

## ⚠️ Important Notes
- Always call `ConcordiumIDAppSDK.initialize(context)` before any other function.
- `signAndSubmit` parses the `unsignedCdiStr` JSON; ensure it is valid. The function will throw if parsing fails.
- Network calls use the Concordium Java/Kotlin client; ensure correct network selection (`Network.MAINNET` vs `Network.TESTNET`) and permissions.
