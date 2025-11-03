package com.concordium.sdk.common

object Constants {
    const val MOBILE_URI_LINK = "concordiumidapp://wallet-connect?encodedUri="
    const val ID_APP_ID = "com.idwallet.app"
    const val ID_APP_PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=$ID_APP_ID"

    const val GRPC_MAINNET_URL = "https://grpc.mainnet.concordium.software"
    const val GRPC_TEST_URL = "grpc.testnet.concordium.com"

    const val GRPC_PORT = 20_000


    // These indices are set to 0, because this wallet only does 1 identity/account, but in a
    // proper wallet with multiple identities/account, these would be actual variables.
    const val IDENTITY_INDEX = 0
    const val CREDENTIAL_COUNTER = 0

    const val AR_THRESHOLD = 2L
    const val WALLET_PROXY_URL = "https://wallet-proxy.testnet.concordium.com"
    const val CALLBACK_URL = "concordiumwallet-example://identity-issuer/callback"

    const val TRANX_EXPIRY_IN_MINUTES = 16
}