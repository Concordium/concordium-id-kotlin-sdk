package com.concordium.idapp.sdk.common

internal sealed class Configuration(
    val grpcUrl: String,
    val grpcPort: Int,
)

internal data object Mainnet : Configuration(
    grpcUrl = "https://grpc.mainnet.concordium.software",
    grpcPort = 20_000,
)

internal data object Testnet : Configuration(
    grpcUrl = "https://grpc.testnet.concordium.com",
    grpcPort = 20_000,
)

internal object Constants {
    const val MOBILE_URI_LINK = "concordiumidapp://wallet-connect?encodedUri="
    const val ID_APP_ID = "com.idwallet.app"
    const val ID_APP_PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=$ID_APP_ID"

    val SESSION_TOPIC_REGEX = Regex("^[0-9a-f]{64}$")

    val WC_URI_REGEX = Regex(
        """^wc:[0-9a-fA-F-]+@[12]\?bridge=.+&key=[0-9a-fA-F]+$"""
    )
}