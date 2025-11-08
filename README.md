# Concordium IDApp SDK — Integration Guide

This file documents how to integrate the `concordium-idapp-sdk` Android library into a consumer app, and shows the public API and examples for common tasks.

## Where to find the SDK
- Module: `concordium-idapp-sdk`

## Initialization
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

## Public API (summary)
The SDK exposes two main singletons and a small model interface:

1. `ConcordiumIDAppSDK` — core functionality
   - `initialize(context: Context, enableDebugLog: Boolean = false)`
   - `signAndSubmit(seedPhrase: String, expiry: Long, unsignedCdiStr: String, accountIndex: Int = 0, network: Network = Network.MAINNET): String`
   - `generateAccountWithSeedPhrase(seed: String, network: Network, accountIndex: Int = 0): CCDAccountKeyPair`
   - `clear()`

2. `ConcordiumIDAppPopup` — helpers to launch ID App UI flows
   - `invokeIdAppDeepLinkPopup(walletConnectUri: String)`
   - `invokeIdAppActionsPopup(walletConnectSessionTopic: String? = null, onCreateAccount: (() -> Unit)? = null, onRecoverAccount: (() -> Unit)? = null)`
   - `closePopup()`

3. `CCDAccountKeyPair` — model
   - `val publicKey: String`
   - `val signingKey: String`

## Examples

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

## Notes & gotchas
- Always call `ConcordiumIDAppSDK.initialize(context)` before any other function.
- `signAndSubmit` parses the `unsignedCdiStr` JSON; ensure it is valid. The function will throw if parsing fails.
- Network calls use the Concordium Java/Kotlin client; ensure correct network selection (`Network.MAINNET` vs `Network.TESTNET`) and permissions.
