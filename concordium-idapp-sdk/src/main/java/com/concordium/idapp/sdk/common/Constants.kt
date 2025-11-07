package com.concordium.idapp.sdk.common

internal object Constants {
    const val MOBILE_URI_LINK = "concordiumidapp://wallet-connect?encodedUri="
    const val ID_APP_ID = "com.idwallet.app"
    const val ID_APP_PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=$ID_APP_ID"

    const val GRPC_MAINNET_URL = "https://grpc.mainnet.concordium.software"
    const val GRPC_TEST_URL = "grpc.testnet.concordium.com"

    const val GRPC_PORT = 20_000

    val SESSION_TOPIC_REGEX = Regex("^[0-9a-f]{64}$")

    val WC_URI_REGEX = Regex(
        """^wc:[0-9a-fA-F-]+@[12]\?bridge=.+&key=[0-9a-fA-F]+$"""
    )
}