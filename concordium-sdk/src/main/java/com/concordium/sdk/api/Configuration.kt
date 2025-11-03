package com.concordium.sdk.api

import com.concordium.sdk.crypto.wallet.Network

// TODO check if needed ?
data class Configuration(
    val enableDebugging: Boolean,
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