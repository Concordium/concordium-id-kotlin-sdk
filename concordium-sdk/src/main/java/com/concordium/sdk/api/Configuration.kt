package com.concordium.sdk.api

import com.concordium.sdk.common.Constants.TRANX_EXPIRY_IN_MINUTES
import com.concordium.sdk.crypto.wallet.Network

// TODO check if needed ?
data class Configuration(
    val enableDebugging: Boolean,
    val tranxExpiryInMins: Int = TRANX_EXPIRY_IN_MINUTES,
    val network: Network,
) {
    companion object {
        val TEST = Configuration(
            enableDebugging = true,
            network = Network.TESTNET
        )
        val PROD = Configuration(
            enableDebugging = false,
            network = Network.MAINNET
        )
    }
}