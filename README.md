# Concordium ID Kotlin SDK

The Concordium ID Kotlin SDK enables Android developers to easily integrate Concordium blockchain functionality into their applications. This SDK provides a seamless way to interact with the Concordium blockchain, manage accounts, and handle transactions.

## Features

- Account management (creation and recovery)
- Transaction signing and submission
- Deep link integration with Concordium ID App
- QR code generation for wallet connect
- Support for both Mainnet and Testnet

## Installation

Add the following dependency to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.concordium.sdk:concordium-android-sdk:latest_version")
}
```

## Initialization

Initialize the SDK in your Application class:

```kotlin
class YourApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ConcordiumIDAppSDK.initialize(
            context = applicationContext,
            enableDebugLog = true // Set to false for production
        )
    }
}
```

## Usage

### 1. Sign and Submit Transactions

```kotlin
ConcordiumIDAppSDK.signAndSubmit(
    seedPhrase = "your seed phrase here",
    inputTranx = transactionJson,
    accountIndex = 0, // optional, defaults to 0
    network = Network.MAINNET // or Network.TESTNET
)
```

### 2. Generate Account with Seed Phrase

```kotlin
val accountKeyPair = ConcordiumIDAppSDK.generateAccountWithSeedPhrase(
    seed = "your seed phrase",
    network = Network.MAINNET,
    accountIndex = 0 // optional, defaults to 0
)

// Access the keys
val publicKey = accountKeyPair.publicKey
val signingKey = accountKeyPair.signingKey
```

### 3. Invoke ID App Deep Link Popup

```kotlin
ConcordiumIDAppPopup.invokeIdAppDeepLinkPopup(
    walletConnectUri = "your-wallet-connect-uri"
)
```

### 4. Invoke ID App Actions Popup

```kotlin
ConcordiumIDAppPopup.invokeIdAppActionsPopup(
    walletConnectSessionTopic = "your-session-topic",
    onCreateAccount = {
        // Handle account creation
    },
    onRecoverAccount = {
        // Handle account recovery
    }
)
```

### 5. Close ID App Popup

```kotlin
ConcordiumIDAppPopup.closePopup()
```

## Android Manifest Configuration

Add the following permissions and queries to your AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.INTERNET" />

<queries>
    <package android:name="com.idwallet.app" />
</queries>
```

## Example Implementation

Here's a basic example of implementing the SDK in an Activity:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Sign and submit a transaction
        ConcordiumIDAppSDK.signAndSubmit(
            seedPhrase = "your-seed-phrase",
            inputTranx = readJsonFromAssets(context, "tranx.json"),
            network = Network.TESTNET
        )
        
        // Open deep link popup
        ConcordiumIDAppPopup.invokeIdAppDeepLinkPopup(
            walletConnectUri = "your-wallet-connect-uri"
        )
        
        // Handle account actions
        ConcordiumIDAppPopup.invokeIdAppActionsPopup(
            walletConnectSessionTopic = "your-session-topic",
            onCreateAccount = {
                // Handle account creation
            },
            onRecoverAccount = {
                // Handle account recovery
            }
        )
    }
}
```

## Error Handling

The SDK provides proper error handling mechanisms. Always wrap SDK calls in try-catch blocks:

```kotlin
runCatching {
    ConcordiumIDAppPopup.invokeIdAppActionsPopup(
        walletConnectSessionTopic = sessionTopic,
        onCreateAccount = { /* ... */ },
        onRecoverAccount = { /* ... */ }
    )
}.onFailure { error ->
    // Handle errors appropriately
    error.printStackTrace()
}
```

## Cleanup

When you're done using the SDK, make sure to clean up resources:

```kotlin
ConcordiumIDAppSDK.clear()
```

## Network Configuration

The SDK supports both Mainnet and Testnet environments:

- Mainnet URL: `https://grpc.mainnet.concordium.software`
- Testnet URL: `grpc.testnet.concordium.com`
- Default Port: `20000`

## Dependencies

The SDK uses the following key dependencies:
- Jetpack Compose for UI components
- ZXing for QR code generation
- Concordium Android SDK for blockchain interactions

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

[Add your license information here]

## Support

For support and questions, please [create an issue](https://github.com/Concordium/concordium-id-kotlin-sdk/issues) on our GitHub repository.